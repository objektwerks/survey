package objektwerks

import Validators.*

final class Dispatcher(handler: Handler):
  def dispatch(command: Command): Event =
    val commandValidator = command.validate
    commandValidator.isValid match
      case false => handler.addFault( Fault(s"${commandValidator.asString} for: $command") )
      case true =>
        handler.isAuthorized(command) match
          case Unauthorized(cause) => handler.addFault( Fault(cause) )
          case Authorized =>
            val event = command match
              case Register(email)                         => handler.register(email)
              case Login(email, pin)                       => handler.login(email, pin)
              case ListParticipant(_, email)               => handler.listParticipant(email)
              case AddParticipant(_, participant)          => handler.addParticipant(participant)
              case ListSurveys(_, accountId)               => handler.listSurveys(accountId)
              case AddSurvey(_, survey)                    => handler.addSurvey(survey)
              case UpdateSurvey(_, survey)                 => handler.updateSurvey(survey)
              case ReleaseSurvey(_, surveyId, released)    => handler.releaseSurvey(surveyId, released)
              case ListQuestions(_, surveyId)              => handler.listQuestions(surveyId)
              case AddQuestion(_, question)                => handler.addQuestion(question)
              case UpdateQuestion(_, question)             => handler.updateQuestion(question)
              case ListAnswers(_, surveyId, participantId) => handler.listAnswers(surveyId, participantId)
              case AddAnswer(_, answer)                    => handler.addAnswer(answer)
              case ListFaults(_)                           => handler.listFaults()
              case AddFault(_, fault)                      => handler.addFault(fault)
            val eventValidator = event.validate
            eventValidator.isValid match
              case false => handler.addFault( Fault(s"${eventValidator.asString} for: $event") )
              case true => event