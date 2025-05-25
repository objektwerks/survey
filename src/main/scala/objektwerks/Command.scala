package objektwerks

sealed trait Command

sealed trait License:
  val license: String

final case class Register(email: String) extends Command

final case class Login(email: String, pin: String) extends Command

final case class ListSurveys(license: String, accountId: Long) extends Command with License

final case class AddSurvey(license: String, survey: Survey) extends Command with License

final case class UpdateSurvey(license: String, survey: Survey) extends Command with License

final case class ListQuestions(license: String, surveyId: Long) extends Command with License

final case class AddQuestion(license: String, question: Question) extends Command with License


final case class ListFaults(license: String) extends Command with License

final case class AddFault(license: String, fault: String) extends Command with License