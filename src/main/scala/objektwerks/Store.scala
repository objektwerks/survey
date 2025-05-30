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
      sql"select * from participant where email = $email"
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

  def isSurveyReleased(surveyId: Long): Boolean =
    DB readOnly { implicit session =>
      val optionalSurvey = sql"select * from survey where id = $surveyId"
        .map(rs =>
          Survey(
            rs.long("id"),
            rs.long("account_id"),
            rs.string("title"),
            rs.string("created"),
            rs.string("released")
          )
        )
        .single()
        optionalSurvey.map(survey => survey.isReleased).getOrElse(false)
    }

  def releaseSurvey(surveyId: Long, released: String): Int =
    DB localTx { implicit session =>
      sql"""
        update survey set released = ${released}
        where id = ${surveyId}
        """
        .update()
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
        insert into survey(account_id, title, created, released)
        values(${survey.accountId}, ${survey.title}, ${survey.created}, ${survey.released})
        """
        .updateAndReturnGeneratedKey()
    }

  def updateSurvey(survey: Survey): Int =
    DB localTx { implicit session =>
      sql"""
        update survey set title = ${survey.title}, created = ${survey.created}, released = ${survey.released}
        where id = ${survey.id}
        """
        .update()
    }

  def listQuestions(surveyId: Long): List[Question] =
    DB readOnly { implicit session =>
      sql"select * from question where survey_id = $surveyId order by created desc"
        .map(rs =>
          Question(
            rs.long("id"),
            rs.long("survey_id"),
            rs.string("question"),
            rs.string("choices").split(",").toList,
            rs.string("typeof"),
            rs.string("created")
          )
        )
        .list()
    }

  def addQuestion(question: Question): Long =
    DB localTx { implicit session =>
      sql"""
        insert into question(survey_id, question, choices, typeof, created)
        values(${question.surveyId}, ${question.question}, ${question.choices.mkString(",")}, ${question.typeof}, ${question.created})
        """
        .updateAndReturnGeneratedKey()
    }

  def updateQuestion(question: Question): Int =
    DB localTx { implicit session =>
      sql"""
        update question set question = ${question.question},
        choices = ${question.choices.mkString(",")},
        typeof = ${question.typeof}
        where id = ${question.id}
        """
        .update()
    }

  def listAnswers(surveyId: Long, participantId: Long): List[Answer] =
    DB readOnly { implicit session =>
      sql"select * from answer where survey_id = $surveyId and participant_id = $participantId order by answered desc"
        .map(rs =>
          Answer(
            rs.long("id"),
            rs.long("survey_id"),
            rs.long("question_id"),
            rs.long("participant_id"),
            rs.string("answers").split(",").toList,
            rs.string("typeof"),
            rs.string("answered")
          )
        )
        .list()
    }

  def addAnswer(answer: Answer): Long =
    DB localTx { implicit session =>
      sql"""
        insert into answer(survey_id, question_id, participant_id, answers, typeof, answered)
        values(${answer.surveyId}, ${answer.questionId}, ${answer.participantId},
        ${answer.answers.mkString(",")}, ${answer.typeof}, ${answer.answered})
        """
        .updateAndReturnGeneratedKey()
    }

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