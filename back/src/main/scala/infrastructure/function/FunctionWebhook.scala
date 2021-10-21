package infrastructure.function

import org.telegram.telegrambots.meta.generics.{Webhook, WebhookBot}

class FunctionWebhook() extends Webhook {
  var internalUrl: String = _

  override def startServer(): Unit = ()

  override def registerWebhook(callback: WebhookBot): Unit = {}

  override def setInternalUrl(internalUrl: String): Unit = this.internalUrl = internalUrl

  override def setKeyStore(keyStore: String, keyStorePassword: String): Unit = ???
}
