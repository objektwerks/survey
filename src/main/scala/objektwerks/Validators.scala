package objektwerks

import Validator.*
import objektwerks.ParticipantAdded

object Validators:
  extension (value: String)
    def isEmptyOrNonEmpty: Boolean = value.isEmpty || value.nonEmpty
    def isLicense: Boolean = value.length == 36
    def isPin: Boolean = value.length == 7
    def isEmail: Boolean = value.length >= 3 && value.contains("@")

  extension (participant: Participant)
    def validate: Validator =
      Validator()
        .validate(participant.id >= 0)(Field("Id"), Message("must be greater than or equal to 0."))
        .validate(participant.email.isEmail)(Field("Email"), Message("must be at least 3 characters in length and contain 1 @ symbol."))
        .validate(participant.activated.nonEmpty)(Field("Activated"), Message("must be non empty."))

  extension (account: Account)
    def validate: Validator =
      Validator()
        .validate(account.id >= 0)(Field("Id"), Message("must be greater than or equal to 0."))
        .validate(account.license.isLicense)(Field("License"), Message("must be 36 characters in length."))
        .validate(account.email.isEmail)(Field("Email"), Message("must be at least 3 characters in length and contain 1 @ symbol."))
        .validate(account.pin.isPin)(Field("Pin"), Message("must be 7 characters in length."))
        .validate(account.activated.nonEmpty)(Field("Activated"), Message("must be non empty."))

  extension (survey: Survey)
    def validate: Validator =
      Validator()
        .validate(survey.id >= 0)(Field("Id"), Message("must be greater than or equal to 0."))
        .validate(survey.accountId > 0)(Field("AccountId"), Message("must be greater than 0."))
        .validate(survey.title.nonEmpty)(Field("Title"), Message("must be non empty."))
        .validate(survey.created.nonEmpty)(Field("Created"), Message("must be non empty."))
        .validate(survey.released.nonEmpty)(Field("Released"), Message("must be non empty."))

  extension (question: Question)
    def validate: Validator =
      Validator()
        .validate(question.id >= 0)(Field("Id"), Message("must be greater than or equal to 0."))
        .validate(question.surveyId > 0)(Field("SurveyId"), Message("must be greater than 0."))
        .validate(question.question.nonEmpty)(Field("Question"), Message("must be non empty."))
        .validate(question.choices.nonEmpty)(Field("Choices"), Message("must be non empty."))
        .validate(question.created.nonEmpty)(Field("Created"), Message("must be non empty."))

  extension (answer: Answer)
    def validate: Validator =
      Validator()
        .validate(answer.surveyId > 0)(Field("SurveyId"), Message("must be greater than 0."))
        .validate(answer.questionId > 0)(Field("QuestionId"), Message("must be greater than or equal to 0."))
        .validate(answer.participantId > 0)(Field("ParticipantId"), Message("must be greater than 0."))
        .validate(answer.answer.nonEmpty)(Field("Answer"), Message("must be non empty."))
        .validate(answer.answered.nonEmpty)(Field("Answered"), Message("must be non empty."))

  extension (register: Register)
    def validate: Validator =
      Validator()
        .validate(register.email.isEmail)(Field("Email"), Message("must be at least 3 characters in length and contain 1 @ symbol."))

  extension (login: Login)
    def validate: Validator =
      Validator()
        .validate(login.email.isEmail)(Field("Email"), Message("must be at least 3 characters in length and contain 1 @ symbol."))
        .validate(login.pin.isPin)(Field("Pin"), Message("must be 7 characters in length."))

  extension (listParticipant: ListParticipant)
    def validate: Validator =
      Validator()
        .validate(listParticipant.license.isLicense)(Field("License"), Message("must be 36 characters in length."))
        .validate(listParticipant.email.isEmail)(Field("Email"), Message("must be at least 3 characters in length and contain 1 @ symbol."))

  extension (addParticipant: AddParticipant)
    def validate: Validator =
      Validator()
        .validate(addParticipant.license.isLicense)(Field("License"), Message("must be 36 characters in length."))
        .validate(addParticipant.participant.validate)

  extension (listSurveys: ListSurveys)
    def validate: Validator =
      Validator()
        .validate(listSurveys.license.isLicense)(Field("License"), Message("must be 36 characters in length."))
        .validate(listSurveys.accountId > 0)(Field("AccountId"), Message("must be greater than 0."))

  extension (addSurvey: AddSurvey)
    def validate: Validator =
      Validator()
        .validate(addSurvey.license.isLicense)(Field("License"), Message("must be 36 characters in length."))
        .validate(addSurvey.survey.validate)

  extension (updateSurvey: UpdateSurvey)
    def validate: Validator =
      Validator()
        .validate(updateSurvey.license.isLicense)(Field("License"), Message("must be 36 characters in length."))
        .validate(updateSurvey.survey.validate)

  extension (listQuestions: ListQuestions)
    def validate: Validator =
      Validator()
        .validate(listQuestions.license.isLicense)(Field("License"), Message("must be 36 characters in length."))
        .validate(listQuestions.surveyId > 0)(Field("SuveyId"), Message("must be greater than 0."))

  extension (addQuestion: AddQuestion)
    def validate: Validator =
      Validator()
        .validate(addQuestion.license.isLicense)(Field("License"), Message("must be 36 characters in length."))
        .validate(addQuestion.question.validate)

  extension (updateQuestion: UpdateQuestion)
    def validate: Validator =
      Validator()
        .validate(updateQuestion.license.isLicense)(Field("License"), Message("must be 36 characters in length."))
        .validate(updateQuestion.question.validate)

  extension (listAnswers: ListAnswers)
    def validate: Validator =
      Validator()
        .validate(listAnswers.license.isLicense)(Field("License"), Message("must be 36 characters in length."))
        .validate(listAnswers.surveyId > 0)(Field("SuveyId"), Message("must be greater than 0."))
        .validate(listAnswers.participantId > 0)(Field("ParticipantId"), Message("must be greater than 0."))

  extension (addAnswer: AddAnswer)
    def validate: Validator =
      Validator()
        .validate(addAnswer.license.isLicense)(Field("License"), Message("must be 36 characters in length."))
        .validate(addAnswer.answer.validate)

  extension (listFaults: ListFaults)
    def validate: Validator =
      Validator()
        .validate(listFaults.license.isLicense)(Field("License"), Message("must be 36 characters in length."))

  extension (addFault: AddFault)
    def validate: Validator =
      Validator()
        .validate(addFault.license.isLicense)(Field("License"), Message("must be 36 characters in length."))
        .validate(addFault.fault.nonEmpty)(Field("Fault"), Message("must be non empty."))

  extension (command: Command)
    def validate: Validator =
      command match
        case register: Register               => register.validate
        case login: Login                     => login.validate
        case listParticipant: ListParticipant => listParticipant.validate
        case addParticipant: AddParticipant   => addParticipant.validate
        case listSurveys: ListSurveys         => listSurveys.validate
        case addSurvey: AddSurvey             => addSurvey.validate
        case updateSurvey: UpdateSurvey       => updateSurvey.validate
        case listQuestions: ListQuestions     => listQuestions.validate
        case addQuestion: AddQuestion         => addQuestion.validate
        case updateQuestion: UpdateQuestion   => updateQuestion.validate
        case listAnswers: ListAnswers         => listAnswers.validate
        case addAnswer: AddAnswer             => addAnswer.validate
        case listFaults: ListFaults           => listFaults.validate
        case addFault: AddFault               => addFault.validate

  extension (registered: Registered)
    def validate: Validator =
      Validator()
        .validate(registered.account.validate)

  extension (loggedIn: LoggedIn)
    def validate: Validator =
      Validator()
        .validate(loggedIn.account.validate)

  extension (participantListed: ParticipantListed)
    def validate: Validator =
      if participantListed.participant.isDefined then
        Validator()
          .validate(participantListed.participant.get.validate)
      else
        Validator()
          .validate(Participant(id = -1, email = "", activated = "").validate)

  extension (participantAdded: ParticipantAdded)
    def validate: Validator =
      Validator()
        .validate(participantAdded.id > 0)(Field("Id"), Message("must be greater than 0."))

  extension (surveysListed: SurveysListed)
    def validate: Validator =
      Validator()
        .validate(surveysListed.surveys.length >= 0)(Field("Surveys"), Message("length must be greater than or equal to 0."))

  extension (surveyAdded: SurveyAdded)
    def validate: Validator =
      Validator()
        .validate(surveyAdded.id > 0)(Field("Id"), Message("must be greater than 0."))

  extension (surveyUpdated: SurveyUpdated)
    def validate: Validator =
      Validator()
        .validate(surveyUpdated.count == 1)(Field("Count"), Message("must equal 1."))

  extension (surveyReleased: SurveyReleased)
    def validate: Validator =
      Validator()
        .validate(surveyReleased.count == 1)(Field("Released"), Message("must equal 1."))

  extension (questionsListed: QuestionsListed)
    def validate: Validator =
      Validator()
        .validate(questionsListed.questions.length >= 0)(Field("Questions"), Message("length must be greater than or equal to 0."))

  extension (questionAdded: QuestionAdded)
    def validate: Validator =
      Validator()
        .validate(questionAdded.id > 0)(Field("Id"), Message("must be greater than 0."))

  extension (questionUpdated: QuestionUpdated)
    def validate: Validator =
      Validator()
        .validate(questionUpdated.count == 1)(Field("Count"), Message("must equal 1."))

  extension (answersListed: AnswersListed)
    def validate: Validator =
      Validator()
        .validate(answersListed.answers.length >= 0)(Field("Answers"), Message("length must be greater than or equal to 0."))

  extension (answerAdded: AnswerAdded)
    def validate: Validator =
      Validator()
        .validate(answerAdded.id > 0)(Field("Id"), Message("must be greater than 0."))

  extension (fault: Fault)
    def validate: Validator =
      Validator()
        .validate(fault.cause.nonEmpty)(Field("Fault"), Message("must be non empty"))

  extension (FaultAdded: FaultAdded)
    def validate: Validator =
      Validator()
        .validate(FaultAdded.fault.validate)

  extension (event: Event)
    def validate: Validator =
      event match
        case registered: Registered               => registered.validate
        case loggedIn: LoggedIn                   => loggedIn.validate
        case participantListed: ParticipantListed => participantListed.validate
        case participantAdded: ParticipantAdded   => participantAdded.validate
        case surveysListed: SurveysListed         => surveysListed.validate
        case surveyAdded: SurveyAdded             => surveyAdded.validate
        case surveyUpdated: SurveyUpdated         => surveyUpdated.validate
        case surveyReleased: SurveyReleased       => surveyReleased.validate
        case questionsListed: QuestionsListed     => questionsListed.validate
        case questionAdded: QuestionAdded         => questionAdded.validate
        case questionUpdated: QuestionUpdated     => questionUpdated.validate
        case answsersListed: AnswersListed        => answsersListed.validate
        case answerAdded: AnswerAdded             => answerAdded.validate
        case faultsListed: FaultsListed           => faultsListed.validate
        case faultAdded: FaultAdded               => faultAdded.validate
        case fault: Fault                         => fault.validate