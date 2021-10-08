package infrastructure

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.google.cloud.functions.{HttpFunction, HttpRequest, HttpResponse}
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.{Webhook, WebhookBot}
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import scribe.*

import java.util.concurrent.ConcurrentHashMap

class TelegramBotFunctions extends HttpFunction {

  info("Starting function")
  private val callbacks = new ConcurrentHashMap[String, WebhookBot]
  private val objectMapper = new ObjectMapper()
  info("Function initialized")

  override def service(request: HttpRequest, response: HttpResponse): Unit = {
    info("Service start")
    try {
      val update = objectMapper.readValue(request.getReader, classOf[Update])
      val botApiMethod = callbacks.get("/").onWebhookUpdateReceived(update)
      objectMapper.writeValue(response.getWriter, botApiMethod)
      response.setStatusCode(200)
      info("Service end")
    } catch {
      case e: MismatchedInputException =>
        error("MismatchedInputException", e)
        response.setStatusCode(400)
      case e: JsonProcessingException =>
        error("JsonProcessingException", e)
        response.setStatusCode(400)
      case e =>
        error("Error desconocido", e)
        response.setStatusCode(500)
    }
  }

  def initWebhook(): WebhookBot = {
//    val webhook = new DefaultWebhook
    val webhook = new FunctionWebhook
    webhook.setInternalUrl("http://localhost:8080")
    //webhook.setInternalUrl()
    //webhook.setKeyStore()

    val telegramBotsApi = new TelegramBotsApi(classOf[DefaultBotSession], webhook)
    //val botSession = telegramBotsApi.registerBot(new LongPollingBot())
    val builder = SetWebhook.builder
    builder.url("http://localhost:8080")
    val bot = new WBot()
    telegramBotsApi.registerBot(bot, builder.build())
    info("Initialiced bot")
    bot
  }

  class FunctionWebhook extends Webhook {
    var internalUrl: String = _

    override def startServer(): Unit = ???

    override def registerWebhook(callback: WebhookBot): Unit = {
      callbacks.put("/", callback)
    }

    override def setInternalUrl(internalUrl: String): Unit = this.internalUrl = internalUrl

    override def setKeyStore(keyStore: String, keyStorePassword: String): Unit = ???
  }
}
