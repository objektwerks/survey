package objektwerks

sealed trait Command

sealed trait License:
  val license: String

final case class Register(email: String) extends Command

final case class Login(email: String, pin: String) extends Command

final case class ListSurveys(license: String, accountId: Long) extends Command with License


final case class ListFaults(license: String) extends Command with License

final case class AddFault(license: String, fault: String) extends Command with License