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