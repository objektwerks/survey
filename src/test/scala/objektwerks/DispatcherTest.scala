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
  var testSurvey = Survey(accountId = 0, title = "Test")

  test("dispatcher"):
    register
    login

    fault

  def register: Unit =
    val register = Register(config.getString("email.sender"))
    dispatcher.dispatch(register) match
      case Registered(account) =>
        testAccount = account
      case fault => fail(s"Invalid registered event: $fault")

  def login: Unit =
    val login = Login(testAccount.email, testAccount.pin)
    dispatcher.dispatch(login) match
      case LoggedIn(account) => account shouldBe testAccount
      case fault => fail(s"Invalid loggedin event: $fault")

  def addSurvey: Unit =
    testSurvey = testSurvey.copy(accountId = testAccount.id)
    val addEntity = AddSurvey(testAccount.license, testSurvey)
    dispatcher.dispatch(addEntity) match
      case SurveyAdded(id) =>
        id > 0 shouldBe true
        testSurvey = testSurvey.copy(id = id)
      case fault => fail(s"Invalid survey added event: $fault")

  def updateSurvey: Unit =
    testSurvey = testSurvey.copy(title = "Test Survey")
    val updateEntity = UpdateSurvey(testAccount.license, testSurvey)
    dispatcher.dispatch(updateEntity) match
      case SurveyUpdated(id) => id shouldBe testSurvey.id
      case fault => fail(s"Invalid survey updated event: $fault")

  def listSurveys: Unit =
    val list = ListSurveys(testAccount.license, testSurvey.accountId)
    dispatcher.dispatch(list) match
      case SurveysListed(list) =>
        list.length shouldBe 1
        list.head shouldBe testSurvey
      case fault => fail(s"Invalid surveys listed event: $fault")

  def fault: Unit =
    val fault = Fault("test fault message")
    store.addFault(fault) shouldBe fault
    store.listFaults().length shouldBe 1