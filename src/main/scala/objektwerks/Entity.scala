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
  accountId: Long,
  text: String,
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
  items: List[String]
) extends Answer

final case class Ranking(
  id: Long = 0,
  questionId: Long,
  items: List[String]
) extends Answer

final case class Rating(
  id: Long = 0,
  questionId: Long,
  low: Int,
  high: Int,
  step: Int
) extends Answer

final case class Text(
  id: Long = 0,
  questionId: Long,
  text: String
) extends Answer