package objektwerks

import java.time.LocalDate
import java.util.UUID

sealed trait Entity:
  val id: Long

object Entity:
  def now: String = LocalDate.now.toString
  def nowMinusOneDay: String = LocalDate.now.minusDays(1).toString

final case class Participant(
  id: Long = 0,
  email: String
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
) extends Entity derives CanEqual

final case class Question(
  id: Long = 0,
  surveyId: Long,
  question: String,
  texts: List[String]
) extends Entity derives CanEqual

sealed trait Value

final case class Answer(
  questionId: Long,
  participantId: Long,
  answer: List[String]
) extends Value derives CanEqual