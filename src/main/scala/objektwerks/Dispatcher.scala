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
              case ListQuestions(_, surveyId)              => handler.listQuestions(surveyId)
              case AddQuestion(_, question)                => Fault("TODO")
              case UpdateQuestion(_, question)             => Fault("TODO")
              case ListAnswers(_, surveyId, participantId) => Fault("TODO")
              case AddAnswer(_, answer)                    => Fault("TODO")
              case ListFaults(_)                           => handler.listFaults()
              case AddFault(_, fault)                      => handler.addFault(fault)
            val eventValidator = event.validate
            eventValidator.isValid match
              case false => handler.addFault( Fault(s"${eventValidator.asString} for: $event") )
              case true => event