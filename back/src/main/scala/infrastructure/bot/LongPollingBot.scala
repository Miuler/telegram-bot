package infrastructure.bot

import infrastructure.Config
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import scribe.*

class LongPollingBot extends TelegramLongPollingBot {

  private val conf = Config.load

  /**
    * Esta función se invocará cuando nuestro bot reciba un mensaje
    * @param update Objeto de telegram cuando el mensaje manda un mensaje de texto
    */
  override def onUpdateReceived(update: Update): Unit = {

    val messageTextReceived = update.getMessage.getText
    val chatId = update.getMessage.getChatId
    val message = new SendMessage()
    message.setChatId(chatId.toString)
    message.setText(messageTextReceived)

    info(s"onUpdateReceived: $messageTextReceived")
//    try {
//      execute(message)
//    } catch {
//      case e: TelegramApiException => e.printStackTrace()
//      case e: Exception            => e.printStackTrace()
//    }
  }

  override def getBotUsername: String = conf.username.name

  override def getBotToken: String = conf.token.token
}
