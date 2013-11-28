package playcms.util

import play.api.Logger

trait Logging {
  private val log = Logger(getClass)
  def info(message: => String) = log.info(message)
  def info(message: => String, ex: Throwable) = log.info(message, ex)
  def warn(message: => String) = log.warn(message)
  def warn(message: => String, ex: Throwable) = log.warn(message, ex)
  def debug(message: => String) = log.debug(message)
  def debug(message: => String, ex: Throwable) = log.debug(message, ex)
  def error(message: String) = log.error(message)
  def error(message: => String, ex: Throwable) = log.error(message, ex)
}
