package objektwerks

import Validator.*

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

  extension (question: TextQuestion)
    def validate: Validator =
      Validator()
        .validate(question.id >= 0)(Field("Id"), Message("must be greater than or equal to 0."))
        .validate(question.surveyId > 0)(Field("SurveyId"), Message("must be greater than 0."))
        .validate(question.question.nonEmpty)(Field("Question"), Message("must be non empty."))
        .validate(question.created.nonEmpty)(Field("Created"), Message("must be non empty."))

  extension (question: TextsQuestion)
    def validate: Validator =
      Validator()
        .validate(question.id >= 0)(Field("Id"), Message("must be greater than or equal to 0."))
        .validate(question.surveyId > 0)(Field("SurveyId"), Message("must be greater than 0."))
        .validate(question.question.nonEmpty)(Field("Question"), Message("must be non empty."))
        .validate(question.texts.nonEmpty)(Field("Texts"), Message("must be non empty."))
        .validate(question.created.nonEmpty)(Field("Created"), Message("must be non empty."))

  extension (answer: TextAnswer)
    def validate: Validator =
      Validator()
        .validate(answer.questionId >= 0)(Field("QuestionId"), Message("must be greater than or equal to 0."))
        .validate(answer.participantId > 0)(Field("ParticipantId"), Message("must be greater than 0."))
        .validate(answer.answer.nonEmpty)(Field("Answer"), Message("must be non empty."))

  extension (answer: TextsAnswer)
    def validate: Validator =
      Validator()
        .validate(answer.questionId >= 0)(Field("QuestionId"), Message("must be greater than or equal to 0."))
        .validate(answer.participantId > 0)(Field("ParticipantId"), Message("must be greater than 0."))
        .validate(answer.answer.nonEmpty)(Field("Answer"), Message("must be non empty."))

  extension (register: Register)
    def validate: Validator =
      Validator()
        .validate(register.email.isEmail)(Field("Email"), Message("must be at least 3 characters in length and contain 1 @ symbol."))

  extension (command: Command)
    def validate: Validator =
      command match
        case register: Register => register.validate
        case login: Login => login.validate

  extension (event: Event)
    def validate: Validator =
      event match
        case registered: Registered => registered.validate
        case loggedIn: LoggedIn => loggedIn.validate