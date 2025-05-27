package objektwerks

import com.github.blemale.scaffeine.{Cache, Scaffeine}
import com.typesafe.config.Config
import com.zaxxer.hikari.HikariDataSource

import java.util.concurrent.TimeUnit
import javax.sql.DataSource

import scala.concurrent.duration.FiniteDuration

import scalikejdbc.*

object Store:
  def apply(config: Config) = new Store( cache(config), dataSource(config) )

  private def cache(config: Config): Cache[String, String] =
    Scaffeine()
      .initialCapacity(config.getInt("cache.initialSize"))
      .maximumSize(config.getInt("cache.maxSize"))
      .expireAfterWrite( FiniteDuration( config.getLong("cache.expireAfter"), TimeUnit.HOURS) )
      .build[String, String]()

  private def dataSource(config: Config): DataSource =
    val ds = HikariDataSource()
    ds.setDataSourceClassName(config.getString("db.driver"))
    ds.addDataSourceProperty("url", config.getString("db.url"))
    ds.addDataSourceProperty("user", config.getString("db.user"))
    ds.addDataSourceProperty("password", config.getString("db.password"))
    ds

final class Store(cache: Cache[String, String],
                  dataSource: DataSource):
  ConnectionPool.singleton( DataSourceConnectionPool(dataSource) )

  def register(account: Account): Long =
    addAccount(account)

  def login(email: String, pin: String): Option[Account] =
    DB readOnly { implicit session =>
      sql"select * from account where email = $email and pin = $pin"
        .map(rs =>
          Account(
            rs.long("id"),
            rs.string("license"),
            rs.string("email"),
            rs.string("pin"),
            rs.string("activated")
          )
        )
        .single()
    }

  def isAuthorized(license: String): Boolean =
    cache.getIfPresent(license) match
      case Some(_) =>
        true
      case None =>
        val optionalLicense = DB readOnly { implicit session =>
          sql"select license from account where license = $license"
            .map(rs => rs.string("license"))
            .single()
        }
        if optionalLicense.isDefined then
          cache.put(license, license)
          true
        else false

  private def addAccount(account: Account): Long =
    DB localTx { implicit session =>
      sql"""
        insert into account(license, email, pin, activated)
        values(${account.license}, ${account.email}, ${account.pin}, ${account.activated})
      """
      .updateAndReturnGeneratedKey()
    }

  def listParticipant(email: String): Option[Participant] =
    DB readOnly { implicit session =>
      sql"select * from particpant where email = $email"
        .map(rs =>
          Participant(
            rs.long("id"),
            rs.string("email"),
            rs.string("activated")
          )
        )
        .single()
    }

  def addParticipant(participant: Participant): Long =
    DB localTx { implicit session =>
      sql"""
        insert into participant(email, activated)
        values(${participant.email}, ${participant.activated})
        """
        .updateAndReturnGeneratedKey()
    }

  def listSurveys(accountId: Long): List[Survey] =
    DB readOnly { implicit session =>
      sql"select * from survey where account_id = $accountId order by released desc"
        .map(rs =>
          Survey(
            rs.long("id"),
            rs.long("account_id"),
            rs.string("title"),
            rs.string("created"),
            rs.string("released")
          )
        )
        .list()
    }

  def addSurvey(survey: Survey): Long =
    DB localTx { implicit session =>
      sql"""
        insert into survey(account_id, title, created, release)
        values(${survey.accountId}, ${survey.title}, ${survey.created}, ${survey.released})
        """
        .updateAndReturnGeneratedKey()
    }

  def updateSurvey(survey: Survey): Int = ???

  def listQuestions(surveyId: Long): List[Question] = ???

  def addQuestion(question: Question): Long = ???

  def updateQuestion(question: Question): Int = ???

  def listAnswers(surveyId: Long, participantId: Long): List[Answer] = ???

  def addAnswer(answer: Answer): Long = ???

  def listFaults(): List[Fault] =
    DB readOnly { implicit session =>
      sql"select * from fault order by occurred desc"
        .map(rs =>
          Fault(
            rs.string("cause"),
            rs.string("occurred")
          )
        )
        .list()
    }

  def addFault(fault: Fault): Fault =
    DB localTx { implicit session =>
      sql"""
        insert into fault(cause, occurred) values(${fault.cause}, ${fault.occurred})
        """
        .update()
    }
    fault