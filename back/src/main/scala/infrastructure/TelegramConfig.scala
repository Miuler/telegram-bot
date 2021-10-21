package infrastructure

import pureconfig.ConfigSource
import pureconfig.generic.auto.*
import scribe.*

case class Port(number: Int) extends AnyVal
case class Url(urs: String) extends AnyVal
case class Username(name: String) extends AnyVal

case class Token(token: String) extends AnyVal

case class Webhook(webhook: String) extends AnyVal

case class TelegramConfig(port: Option[Port], url: Option[Url], username: Username, token: Token, webhook: Webhook)

object Config {
  val url = getClass.getResource("/application.conf").toURI.toURL
  info(s"Config file: ${url}")

  lazy private val telegramConfig = ConfigSource.url(url).load[TelegramConfig] match {
    case Right(value) => value
    case Left(value) =>
      error("Configuracion mal formateada")
      error(s"$value")
      sys.exit()
    case _ =>
      error("Necesita configuracion basica")
      sys.exit()
  }

  def load: TelegramConfig = telegramConfig
}
