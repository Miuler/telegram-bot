package infraestructure

import org.telegram.telegrambots.bots.TelegramWebhookBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import pureconfig.ConfigSource
import pureconfig.generic.auto.*
import scribe.{error, info}

class WBot extends TelegramWebhookBot {
  private val conf = ConfigSource.default.load[TelegramConfig] match {
    case Right(value) => value
    case Left(value) =>
      error("Configuracion mal formateada")
      error(s"$value")
      sys.exit()
    case _ =>
      error("Necesita configuracion basica")
      sys.exit()
  }

  override def onWebhookUpdateReceived(update: Update): BotApiMethod[_] = {
    val messageTextReceived = update.getMessage.getText
    val chatId = update.getMessage.getChatId
    val message = new SendMessage()
    message.setChatId(chatId.toString)
    message.setText(messageTextReceived)

    info(s"onUpdateReceived: $messageTextReceived")
    message
  }

  override def getBotPath: String = "/"

  override def getBotUsername: String = conf.username.name

  override def getBotToken: String = conf.token.token
}
