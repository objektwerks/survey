package objektwerks

import ox.supervised
import ox.resilience.{retry, RetryConfig}

import scala.concurrent.duration.*
import scala.util.Try
import scala.util.control.NonFatal

final class Handler(store: Store, emailer: Emailer):
  def isAuthorized(command: Command): Security =
    command match
      case license: License =>
        try
          supervised:
            retry( RetryConfig.delay(1, 100.millis) )(
              if store.isAuthorized(license.license) then Authorized
              else Unauthorized(s"Unauthorized: $command")
            )
        catch
          case NonFatal(error) => Unauthorized(s"Unauthorized: $command, cause: $error")
      case Register(_) | Login(_, _) => Authorized

  def sendEmail(email: String, message: String): Unit =
    val recipients = List(email)
    emailer.send(recipients, message)

  def register(email: String): Event =
    try
      supervised:
        val account = Account(email = email)
        val message = s"Your new pin is: ${account.pin}\n\nWelcome aboard!"
        retry( RetryConfig.delay(1, 600.millis) )( sendEmail(account.email, message) )
        val id = store.register(account)
        Registered( account.copy(id = id) )
    catch
      case NonFatal(error) => addFault( Fault(s"Registration failed for: $email, because: ${error.getMessage}") )

  def login(email: String, pin: String): Event =
    Try:
      supervised:
        retry( RetryConfig.delay(1, 100.millis) )( store.login(email, pin) )
    .fold(
      error => addFault( Fault(s"Login failed: ${error.getMessage}") ),
      optionalAccount =>
        if optionalAccount.isDefined then LoggedIn( optionalAccount.get )
        else addFault( Fault(s"Login failed for email address: $email and pin: $pin") ) )

  def listParticipant(email: String): Event =
    try
      ParticipantListed(
        supervised:
          retry( RetryConfig.delay(1, 100.millis) )(  store.listParticipant(email) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"List participant failed: ${error.getMessage}") )

  def addParticipant(participant: Participant): Event =
    try
      ParticipantAdded(
        supervised:
          retry( RetryConfig.delay(1, 100.millis) )(  store.addParticipant(participant) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"Add answer failed: ${error.getMessage}") )

  def listSurveys(accountId: Long): Event =
    try
      SurveysListed(
        supervised:
          retry( RetryConfig.delay(1, 100.millis) )(  store.listSurveys(accountId) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"List surveys failed: ${error.getMessage}") )

  def addSurvey(survey: Survey): Long = store.addSurvey(survey)

  def updateSurvey(survey: Survey): Int = store.updateSurvey(survey)

  def listQuestions(surveyId: Long): List[Question] = store.listQuestions(surveyId)

  def addQuestion(question: Question): Long = store.addQuestion(question)

  def updateQuestion(question: Question): Int = store.updateQuestion(question)

  def listAnswers(surveyId: Long, participantId: Long): List[Answer] = store.listAnswers(surveyId, participantId)

  def addAnswer(answer: Answer): Long = store.addAnswer(answer)

  def listFaults(): Event =
    try
      FaultsListed(
        supervised:
          retry( RetryConfig.delay(1, 100.millis) )( store.listFaults() )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"List faults failed: ${error.getMessage}") )

  def addFault(fault: String): Event =
    try
      FaultAdded(
        supervised:
          retry( RetryConfig.delay(1, 100.millis) )( store.addFault( Fault(fault) ) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"Add fault failed: ${error.getMessage}") )

  def addFault(fault: Fault): Event =
    try
      FaultAdded(
        supervised:
          retry( RetryConfig.delay(1, 100.millis) )( store.addFault(fault) )
      )
    catch
      case NonFatal(error) => addFault( Fault(s"Add fault failed: ${error.getMessage}") )