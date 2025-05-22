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

final case class Question(
  id: Long = 0,
  surveyId: Long,
  question: String,
  answer: Answer,
  created: String = Entity.now
) extends Entity derives CanEqual

sealed trait Answer extends Entity derives CanEqual:
  val id: Long
  val questionId: Long
  val created: String = Entity.now

final case class Choices(
  id: Long = 0,
  questionId: Long,
  choices: List[String],
  answer: List[String] = List.empty[String]
) extends Answer

final case class Ranking(
  id: Long = 0,
  questionId: Long,
  rankings: List[String],
  answer: List[String] = List.empty[String]
) extends Answer

final case class Rating(
  id: Long = 0,
  questionId: Long,
  low: Int,
  high: Int,
  step: Int,
  answer: Int = 0
) extends Answer

final case class Text(
  id: Long = 0,
  questionId: Long,
  answer: String = ""
) extends Answer