package infraestructure

import pureconfig.ConfigSource
import scribe.error
import pureconfig.generic.auto.*

case class Port(number: Int) extends AnyVal
case class Url(urs: String) extends AnyVal
case class Username(name: String) extends AnyVal
case class Token(token: String) extends AnyVal

case class TelegramConfig(port: Option[Port], url: Option[Url], username: Username, token: Token)

object Config {
  lazy private val telegramConfig = ConfigSource.default.load[TelegramConfig] match {
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
