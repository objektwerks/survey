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
  var testParticipant = Participant(email = "a@b")
  var testSurvey = Survey(accountId = 0, title = "Test")
  var testQuestion = Question(surveyId = 1, question = "What is the greatest beer style?", choices = List("Lager", "Pilsner"))
  var testAnswer = Answer(surveyId = 1, questionId = 1, participantId = 1, answer = List("IPA"))

  test("dispatcher"):
    register
    login

    addParticipant
    listParticipant

    addSurvey
    updateSurvey
    listSurveys

    addQuestion
    updateQuestion
    listQuestions

    releaseSurvey // a survey must be released before answers can be added

    addAnswer
    listAnswers

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

  def addParticipant: Unit =
    val addParticipant = AddParticipant(testAccount.license, testParticipant)
    dispatcher.dispatch(addParticipant) match
      case SurveyAdded(id) =>
        id > 0 shouldBe true
        testParticipant = testParticipant.copy(id = id)
      case fault => fail(s"Invalid participant added event: $fault")

  def listParticipant: Unit =
    val listParticipant = ListParticipant(testAccount.license, testParticipant.email)
    dispatcher.dispatch(listParticipant) match
      case SurveysListed(list) =>
        list.length shouldBe 1
        list.head shouldBe testSurvey
      case fault => fail(s"Invalid participant listed event: $fault")

  def addSurvey: Unit =
    testSurvey = testSurvey.copy(accountId = testAccount.id)
    val addSurvey = AddSurvey(testAccount.license, testSurvey)
    dispatcher.dispatch(addSurvey) match
      case SurveyAdded(id) =>
        id > 0 shouldBe true
        testSurvey = testSurvey.copy(id = id)
      case fault => fail(s"Invalid survey added event: $fault")

  def updateSurvey: Unit =
    testSurvey = testSurvey.copy(title = "Test Survey")
    val updateSurvey = UpdateSurvey(testAccount.license, testSurvey)
    dispatcher.dispatch(updateSurvey) match
      case SurveyUpdated(count) => count shouldBe 1
      case fault => fail(s"Invalid survey updated event: $fault")

  def listSurveys: Unit =
    val listSurveys = ListSurveys(testAccount.license, testSurvey.accountId)
    dispatcher.dispatch(listSurveys) match
      case SurveysListed(list) =>
        list.length shouldBe 1
        list.head shouldBe testSurvey
      case fault => fail(s"Invalid surveys listed event: $fault")

  def addQuestion: Unit =
    testQuestion = testQuestion.copy(surveyId = testSurvey.id)
    val addQuestion = AddQuestion(testAccount.license, testQuestion)
    dispatcher.dispatch(addQuestion) match
      case QuestionAdded(id) =>
        id > 0 shouldBe true
        testQuestion = testQuestion.copy(id = id)
      case fault => fail(s"Invalid question added event: $fault")

  def updateQuestion: Unit =
    testQuestion = testQuestion.copy(choices = "IPA" :: testQuestion.choices)
    val updateQuestion = UpdateQuestion(testAccount.license, testQuestion)
    dispatcher.dispatch(updateQuestion) match
      case QuestionUpdated(count) => count shouldBe 1
      case fault => fail(s"Invalid question updated event: $fault")

  def listQuestions: Unit =
    val listQuestions = ListQuestions(testAccount.license, testQuestion.surveyId)
    dispatcher.dispatch(listQuestions) match
      case QuestionsListed(list) =>
        list.length shouldBe 1
        list.head shouldBe testQuestion
      case fault => fail(s"Invalid questions listed event: $fault")

  def releaseSurvey: Unit =
    val releaseSurvey = ReleaseSurvey(testAccount.license, testSurvey.id, Entity.nowPlusOneDay)
    dispatcher.dispatch(releaseSurvey) match
      case SurveyReleased(count) => count shouldBe 1
      case fault => fail(s"Invalid survey release event: $fault")

  def addAnswer: Unit =
    testAnswer = testAnswer.copy(surveyId = testSurvey.id, questionId = testQuestion.id, participantId = testParticipant.id)
    val addAnswer = AddAnswer(testAccount.license, testAnswer)
    dispatcher.dispatch(addAnswer) match
      case AnswerAdded(id) =>
        id > 0 shouldBe true
        testAnswer = testAnswer.copy(id = id)
      case fault => fail(s"Invalid answer added event: $fault")

  def listAnswers: Unit =
    val listAnswers = ListAnswers(testAccount.license, testSurvey.id, testParticipant.id)
    dispatcher.dispatch(listAnswers) match
      case AnswersListed(list) =>
        list.length shouldBe 1
        list.head shouldBe testAnswer
      case fault => fail(s"Invalid answers listed event: $fault")

  def fault: Unit =
    val fault = Fault("test fault message")
    store.addFault(fault) shouldBe fault
    store.listFaults().length shouldBe 1