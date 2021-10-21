package infrastructure.bot

import infrastructure.Config
import org.telegram.telegrambots.bots.TelegramWebhookBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import scribe.*

class WBot extends TelegramWebhookBot {
  private val conf = Config.load

  override def onWebhookUpdateReceived(update: Update): BotApiMethod[?] = {
    info(s"update: ${update.toString}")

    if (update.hasMessage) {
      val message = new SendMessage()
      message.setChatId(update.getMessage.getChatId.toString)
      if (update.getMessage.hasText) {
        val messageTextReceived = update.getMessage.getText
        info(s"onUpdateReceived: $messageTextReceived")
        message.setText(messageTextReceived)
      } else {
        message.setText("Disculpa aun no soporte este tipo de mensaje")
      }
      message
    } else if (update.hasEditedMessage) {
      SendMessage
        .builder()
        .chatId(update.getEditedMessage.getChatId.toString)
        .text("Porque editaste el mensaje?")
        .build()
    } else if (update.hasMyChatMember) {
      notSupported(update.getMyChatMember.getChat.getId.toString)
    } else if (update.hasChatMember) {
      notSupported(update.getChatMember.getChat.getId.toString)
    } else {
      update.getUpdateId
      warn("Disculpa aun no soporte este tipo de accion")
      null
    }
  }

  private def notSupported(chatId: String) = {
    SendMessage
      .builder()
      .chatId(chatId)
      .text("Disculpa aun no soporte este tipo de accion")
      .build()
  }

  override def getBotPath: String = conf.webhook.webhook

  override def getBotUsername: String = conf.username.name

  override def getBotToken: String = conf.token.token
}
