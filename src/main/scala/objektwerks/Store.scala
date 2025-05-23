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