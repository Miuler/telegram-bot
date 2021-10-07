import com.google.cloud.functions.{HttpFunction, HttpRequest, HttpResponse}
import infraestructure.WBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook
import org.telegram.telegrambots.updatesreceivers.{DefaultBotSession, DefaultWebhook}
import scopt.OParser
import scribe.*

import scala.io.StdIn

class BotFunction extends HttpFunction {
  override def service(request: HttpRequest, response: HttpResponse): Unit = {
    import java.io.PrintWriter
    val writer = new PrintWriter(response.getWriter)
    writer.printf("Hello %s!", "Test")
  }
}

object Main {
  def main(args: Array[String]): Unit = {

    val builder = OParser.builder[Config]
    val oparser = {
      import builder.*
      OParser.sequence(
        programName("Telegram Bot"),
        help('h', "help").text("Help"),
        opt[Unit]('d', "daemon").action((_, c) => c.copy(daemon = true)).text("Run Telegram Bot as daemon"),
        opt[Unit]('v', "verbose").action((_, c) => c.copy(verbose = true)).text("Logging with log in debug")
      )
    }
    OParser.parse(oparser, args, Config()) match {
      case Some(config) =>
        info("initializing  bot")
        if (config.verbose) Logger.root.withMinimumLevel(Level.Debug).replace()
        if (config.daemon) {
          val webhook = new DefaultWebhook
          //webhook.setInternalUrl()
          //webhook.setKeyStore()

          val telegramBotsApi = new TelegramBotsApi(classOf[DefaultBotSession])
          //telegramBotsApi.registerBot(new LongPollingBot())
          val builder = SetWebhook.builder
          builder.url("http://localhost:8080")
          telegramBotsApi.registerBot(new WBot(), builder.build())
          info("Initialiced bot")
        } else {
          val webhook = new DefaultWebhook
          webhook.setInternalUrl("http://localhost:8080")
          //webhook.setInternalUrl()
          //webhook.setKeyStore()

          val telegramBotsApi = new TelegramBotsApi(classOf[DefaultBotSession], webhook)
          //val botSession = telegramBotsApi.registerBot(new LongPollingBot())
          val builder = SetWebhook.builder
          builder.url("http://localhost:8080")
          val botSession = telegramBotsApi.registerBot(new WBot(), builder.build())
          info("Initialiced bot")

          info(s"Press RETURN to stop...")
          StdIn.readLine() // let it run until user presses return
          info(s"stopping...")

          //botSession.stop()
          info("Stoped bot")
        }
      case _ => info("Faltan argumentos")
    }

  }

}

case class Config(daemon: Boolean = false, verbose: Boolean = false)
