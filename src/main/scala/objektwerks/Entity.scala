package objektwerks

import java.time.LocalDate
import java.util.UUID

sealed trait Entity:
  val id: Long

object Entity:
  def now: String = LocalDate.now.toString

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
  created: String = Entity.now
) extends Entity derives CanEqual

sealed trait Question extends Entity derives CanEqual:
  val id: Long
  val surveyId: Long
  val question: String
  val created: String = Entity.now

final case class Choices(
  id: Long = 0,
  surveyId: Long,
  question: String,
  choices: List[String],
) extends Question

final case class Ranking(
  id: Long = 0,
  surveyId: Long,
  question: String,
  rankings: List[String],
) extends Question

final case class Rating(
  id: Long = 0,
  surveyId: Long,
  question: String,
  low: Int,
  high: Int,
  step: Int,
) extends Question

final case class Text(
  id: Long = 0,
  surveyId: Long,
  question: String
) extends Question

sealed trait Answer extends Entity derives CanEqual:
  val id: Long
  val questionId: Long
  val created: String = Entity.now

final case class ChoicesAnswer(
  id: Long = 0,
  questionId: Long,
  answer: List[String]
) extends Answer