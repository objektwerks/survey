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
  created: String = Entity.now
) extends Entity derives CanEqual

final case class AnswerItem(
  id: Long = 0,
  answerId: Long,
  text: String,
  isCorrect: Boolean = false
)

sealed trait Answer extends Entity derives CanEqual:
  val id: Long
  val accountId: Long              
  val created: String = Entity.now

final case class SingleChoiceAnswer(
  id: Long = 0,
  accountId: Long,
  answers: List[String]
) extends Answer