package objektwerks

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import java.util.concurrent.Executors

import ox.{ExitCode, never, Ox, OxApp, releaseAfterScope}

import sttp.tapir.server.jdkhttp.JdkHttpServer

object Server extends OxApp with LazyLogging:
  override def run(args: Vector[String])(using Ox): ExitCode =
    val config = ConfigFactory.load("server.conf")
    val host = config.getString("server.host")
    val port = config.getInt("server.port")
    val path = config.getString("server.path")

    val store = Store(config)
    val emailer = Emailer(config)
    val handler = Handler(store, emailer)
    val dispatcher = Dispatcher(handler)
    val endpoints = Endpoints(path, dispatcher, logger)

    val jdkHttpServer = JdkHttpServer()
      .executor( Executors.newVirtualThreadPerTaskExecutor() )
      .host(host)
      .port(port)
      .addEndpoint(endpoints.commandEndpoint)
      .addEndpoints(endpoints.swaggerEndpoints)
      .start()

    println(s"*** Survey Http Server started at: $host:$port/$path")
    logger.info(s"*** Survey Http Server started at: $host:$port/$path")

    println(s"*** Survey Command Endpoint: ${endpoints.commandEndpoint.show}")
    logger.info(s"*** Survey Command Endpoint: ${endpoints.commandEndpoint.show}")

    println(s"*** Survey Swagger Endpoint: ${endpoints.swaggerEndpoints(0).show}")
    logger.info(s"*** Survey Swagger Endpoint: ${endpoints.swaggerEndpoints(0).show}")

    println(s"*** Press Control-C to shutdown Survey Http Server at: $host:$port/$path")

    releaseAfterScope:
      println(s"*** Survey Http Server shutdown at: $host:$port")
      logger.info(s"*** Survey Http Server shutdown at: $host:$port")
      jdkHttpServer.stop(10)

    never

    ExitCode.Success