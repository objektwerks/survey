package objektwerks

import ox.supervised
import ox.resilience.retry
import ox.scheduling.Schedule

import scala.concurrent.duration.*
import scala.util.Try
import scala.util.control.NonFatal

final class Handler(store: Store, emailer: Emailer):
  def isAuthorized(command: Command): Security =
    command match
      case license: License =>
        try
          supervised:
            retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )(
              if store.isAuthorized(license.license) then Authorized
              else Unauthorized(s"Unauthorized: $command")
            )
        catch
          case NonFatal(error) => Unauthorized(s"Unauthorized: $command, cause: $error")
      case Register(_) | Login(_, _) => Authorized

  def sendEmail(email: String, message: String): Boolean =
    val recipients = List(email)
    emailer.send(recipients, message)

  def register(email: String): Event =
    try
      supervised:
        val account = Account(email = email)
        val message = s"Your new pin is: ${account.pin}\n\nWelcome aboard!"
        val result = retry( Schedule.fixedInterval(600.millis).maxRepeats(1) )( sendEmail(account.email, message) )
        if result then
          val id = store.register(account)
          Registered( account.copy(id = id) )
        else
          throw IllegalArgumentException("Invalid email address.")
    catch
      case NonFatal(error) => addFault( Fault(s"Registration failed for: $email, because: ${error.getMessage}") )

  def login(email: String, pin: String): Event =
    Try:
      supervised:
        retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.login(email, pin) )
    .fold(
      error => addFault( Fault(s"Login failed: ${error.getMessage}") ),
      optionalAccount =>
        if optionalAccount.isDefined then LoggedIn( optionalAccount.get )
        else addFault( Fault(s"Login failed for email address: $email and pin: $pin") ) )

  def listParticipant(email: String): Event =
    try
      ParticipantListed(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.listParticipant(email) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"List participant failed: ${error.getMessage}") )

  def addParticipant(participant: Participant): Event =
    try
      ParticipantAdded(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.addParticipant(participant) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"Add answer failed: ${error.getMessage}") )

  def listSurveys(accountId: Long): Event =
    try
      SurveysListed(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.listSurveys(accountId) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"List surveys failed: ${error.getMessage}") )

  def addSurvey(survey: Survey): Event =
    try
      SurveyAdded(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.addSurvey(survey) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"Add survey failed: ${error.getMessage}") )

  def updateSurvey(survey: Survey): Event =
    try
      SurveyUpdated(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.updateSurvey(survey) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"Update survey failed: ${error.getMessage}") )

  def releaseSurvey(surveyId: Long, released: String): Event =
    try
      SurveyReleased(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.releaseSurvey(surveyId, released) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"Release survey failed: ${error.getMessage}") )

  def listQuestions(surveyId: Long): Event =
    try
      QuestionsListed(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.listQuestions(surveyId) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"List questions failed: ${error.getMessage}") )

  def addQuestion(question: Question): Event =
    try
      QuestionAdded(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.addQuestion(question) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"Add question failed: ${error.getMessage}") )

  def updateQuestion(question: Question): Event =
    try
      QuestionUpdated(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.updateQuestion(question) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"Update question failed: ${error.getMessage}") )

  def listAnswers(surveyId: Long, participantId: Long): Event =
    try
      AnswersListed(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.listAnswers(surveyId, participantId) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"List answers failed: ${error.getMessage}") )

  def addAnswer(answer: Answer): Event =
    try
      AnswerAdded(
        supervised:
          if store.isSurveyReleased(answer.surveyId) then
            retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.addAnswer(answer) )
          else throw IllegalStateException(s"Survey [${answer.surveyId}] has not been released!")
      )
    catch
      case NonFatal(error) => addFault( Fault(s"Add answer failed: ${error.getMessage}") )

  def listFaults(): Event =
    try
      FaultsListed(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.listFaults() )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"List faults failed: ${error.getMessage}") )

  def addFault(fault: String): Event =
    try
      FaultAdded(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.addFault( Fault(fault) ) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"Add fault failed: ${error.getMessage}") )

  def addFault(fault: Fault): Event =
    try
      FaultAdded(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.addFault(fault) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"Add fault failed: ${error.getMessage}") )