package objektwerks

import java.time.LocalDate
import java.util.UUID

sealed trait Entity:
  val id: Long
  def now(): String = LocalDate.now.toString

final case class Account(
  id: Long = 0,
  license: String = UUID.randomUUID.toString,
  email: String = "",
  pin: String = Pin.newInstance,
  activated: String = now()
) extends Entity derives CanEqual