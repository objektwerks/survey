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

final case class Participant(
  id: Long = 0,
  email: String,
) extends Entity derives CanEqual

final case class Survey(
  id: Long = 0,
  accountId: Long,
  title: String,
  created: String = Entity.now
) extends Entity derives CanEqual

sealed trait Question extends Entity:
  val id: Long
  val surveyId: Long
  val question: String
  val created: String = Entity.now

final case class Choices(
  id: Long = 0,
  surveyId: Long,
  question: String,
  choices: List[String],
) extends Question derives CanEqual

final case class Ranking(
  id: Long = 0,
  surveyId: Long,
  question: String,
  rankings: List[String],
) extends Question derives CanEqual

final case class Rating(
  id: Long = 0,
  surveyId: Long,
  question: String,
  low: Int,
  high: Int,
  step: Int,
) extends Question derives CanEqual

final case class Text(
  id: Long = 0,
  surveyId: Long,
  question: String
) extends Question derives CanEqual

sealed trait Value

sealed trait Answer extends Value:
  val questionId: Long
  val participantId: Long
  val created: String = Entity.now

final case class ChoicesAnswer(
  questionId: Long,
  participantId: Long,
  answer: List[String]
) extends Answer derives CanEqual

final case class RankingAnswer(
  questionId: Long,
  participantId: Long,
  answer: List[String]
) extends Answer derives CanEqual

final case class RatingAnswer(
  questionId: Long,
  participantId: Long,
  answer: Int
) extends Answer derives CanEqual

final case class TextAnswer(
  questionId: Long,
  participantId: Long,
  answer: String
) extends Answer derives CanEqual