package infraestructure

case class Port(number: Int) extends AnyVal
case class Url(urs: String) extends AnyVal
case class Username(name: String) extends AnyVal
case class Token(token: String) extends AnyVal

case class TelegramConfig(port: Option[Port], url: Option[Url], username: Username, token: Token)
