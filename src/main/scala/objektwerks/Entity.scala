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

final case class Answer(
  id: Long = 0,
  accountId: Long,                  
  created: String = Entity.now
) extends Entity derives CanEqual