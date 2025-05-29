package objektwerks

import java.time.LocalDate
import java.util.UUID

sealed trait Entity:
  val id: Long

object Entity:
  def now: String = LocalDate.now.toString

  def nowMinusOneDay: String = LocalDate.now.minusDays(1).toString

  def nowPlusOneDay: String = LocalDate.now.plusDays(1).toString

final case class Participant(
  id: Long = 0,
  email: String,
  activated: String = Entity.now
) extends Entity derives CanEqual

final case class Account(
  id: Long = 0,
  license: String = UUID.randomUUID.toString,
  email: String = "",
  pin: String = Pin.newInstance,
  activated: String = Entity.now
) extends Entity derives CanEqual

final case class Survey(
  id: Long = 0,
  accountId: Long,
  title: String,
  created: String = Entity.now,
  released: String = Entity.nowMinusOneDay
) extends Entity derives CanEqual {
    def isReleased: Boolean =
      val created = LocalDate.parse(this.created)
      val released = LocalDate.parse(this.released)
      if released.isEqual(created) || released.isAfter(created) then true
      else false
}

final case class Question(
  id: Long = 0,
  surveyId: Long,
  question: String,
  choices: List[String],
  created: String = Entity.now
) extends Entity derives CanEqual

final case class Answer(
  id: Long = 0,
  surveyId: Long,
  questionId: Long,
  participantId: Long,
  answer: List[String],
  answered: String = Entity.now
) extends Entity derives CanEqual