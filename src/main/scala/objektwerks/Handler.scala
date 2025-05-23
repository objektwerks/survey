package objektwerks

import ox.supervised
import ox.resilience.{retry, RetryConfig}

import scala.concurrent.duration.*
import scala.util.Try
import scala.util.control.NonFatal

final class Handler(store: Store, emailer: Emailer)
