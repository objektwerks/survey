package objektwerks

sealed trait Event

final case class Registered(account: Account) extends Event

final case class LoggedIn(account: Account) extends Event

final case class ParticipantListed(participant: Participant) extends Event

final case class ParticipantAdded(id: Long) extends Event

final case class SurveysListed(surveys: List[Survey]) extends Event

final case class SurveyAdded(id: Long) extends Event

final case class SurveyUpdated(count: Int) extends Event

final case class Fault(cause: String, occurred: String = Entity.now) extends Event

final case class FaultsListed(faults: List[Fault]) extends Event

final case class FaultAdded(fault: Fault) extends Event