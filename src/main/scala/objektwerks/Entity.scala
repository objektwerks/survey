package objektwerks

import java.util.UUID

sealed trait Entity:
  val id: Long

final case class Account(
    id: Long = 0,
    license: String = UUID.randomUUID.toString,
    email: String = "",
    pin: String = Pin.newInstance,
    activated: String = Entity.now
) extends Entity derives CanEqual