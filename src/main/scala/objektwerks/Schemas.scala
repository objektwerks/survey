package objektwerks

import sttp.tapir.Schema

object Schemas:
  given Schema[Command] = Schema.derived
  given Schema[Event] = Schema.derived
  given Schema[Entity] = Schema.derived
  given Schema[Participant] = Schema.derived
  given Schema[Account] = Schema.derived
  given Schema[Survey] = Schema.derived
  given Schema[Question] = Schema.derived
  given Schema[Answer] = Schema.derived
  given Schema[Fault] = Schema.derived