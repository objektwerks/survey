package objektwerks

import com.typesafe.config.ConfigFactory

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.sys.process.Process

final class DispatcherTest extends AnyFunSuite with Matchers:
  val exitCode = Process("psql -d survey -f ddl.sql").run().exitValue()
  exitCode shouldBe 0

  val config = ConfigFactory.load("test.conf")
  val store = Store(config)
  val emailer = Emailer(config)
  val handler = Handler(store, emailer)
  val dispatcher = Dispatcher(handler)

  var testAccount = Account()

  test("dispatcher"):
    register
    login