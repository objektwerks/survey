package objektwerks

import ox.supervised
import ox.resilience.{retry, RetryConfig}

import scala.concurrent.duration.*
import scala.util.Try
import scala.util.control.NonFatal

final class Handler(store: Store, emailer: Emailer):
  def isAuthorized(command: Command): Security =
    command match
      case license: License =>
        try
          supervised:
            retry( RetryConfig.delay(1, 100.millis) )(
              if store.isAuthorized(license.license) then Authorized
              else Unauthorized(s"Unauthorized: $command")
            )
        catch
          case NonFatal(error) => Unauthorized(s"Unauthorized: $command, cause: $error")
      case Register(_) | Login(_, _) => Authorized

  def sendEmail(email: String, message: String): Unit =
    val recipients = List(email)
    emailer.send(recipients, message)