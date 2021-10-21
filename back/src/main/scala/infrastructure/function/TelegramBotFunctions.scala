package infrastructure.function

import com.fasterxml.jackson.core.{JsonFactory, JsonGenerator, JsonParser, JsonProcessingException}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.google.cloud.functions.{HttpFunction, HttpRequest, HttpResponse}
import fabric.rw.Convertible
import fabric.{Arr, Bool, Null, Num, Obj, Str, Value}
import infrastructure.Config
import infrastructure.bot.WBot
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.WebhookBot
import perfolation.long2Implicits
import scribe.*
import scribe.format.{FormatterInterpolator, mdc, message}
import scribe.json.{Record, Trace, TraceElement}
import scribe.output.format.{ASCIIOutputFormat, OutputFormat}
import scribe.output.{LogOutput, TextOutput}
import scribe.writer.{SystemOutputWriter, Writer}

import java.io.ByteArrayOutputStream
import java.util.concurrent.ConcurrentHashMap

class TelegramBotFunctions extends HttpFunction {

  info("Starting function")
  private val conf = Config.load
  private val callbacks = new ConcurrentHashMap[String, WebhookBot]
  private val objectMapper = new ObjectMapper()
  Logger.root
    .clearHandlers()
    .clearModifiers()
    .withHandler(
      formatter = formatter"[[$message$mdc]]",
      writer = JsonWriter2(SystemOutputWriter),
      minimumLevel = Some(Level.Info),
      outputFormat = ASCIIOutputFormat
    )
    .replace()
  debug("test")
  initWebhook(callbacks)
  info("Function initialized")

  override def service(request: HttpRequest, response: HttpResponse): Unit = {
    info("Service start")
    try {
      val update = objectMapper.readValue(request.getReader, classOf[Update])
      val botApiMethod = callbacks.get("/").onWebhookUpdateReceived(update)
      //objectMapper.writeValue(response.getWriter, botApiMethod)
      if (botApiMethod != null) {
        val responseBody = objectMapper.writeValueAsString(botApiMethod)
        info(s"responseBody: $responseBody")
        response.getWriter.write(responseBody)
        response.setContentType("application/json")
      }
      response.setStatusCode(200)
      info("Service end")
    } catch {
      case e: MismatchedInputException =>
        error("MismatchedInputException", e)
        response.setStatusCode(400)
      case e: JsonProcessingException =>
        error("JsonProcessingException", e)
        response.setStatusCode(400)
      case e: Throwable =>
        error("Error desconocido", e)
        response.setStatusCode(500)
    }
  }

  def initWebhook(callbacks: ConcurrentHashMap[String, WebhookBot]): WebhookBot = {
    val webhook = new FunctionWebhook
    webhook.setInternalUrl(conf.webhook.webhook)
    //webhook.setInternalUrl()
    //webhook.setKeyStore()

    //val telegramBotsApi = new TelegramBotsApi(classOf[DefaultBotSession], webhook)
    //val botSession = telegramBotsApi.registerBot(new LongPollingBot())
    val builder = SetWebhook.builder
    builder.url(conf.webhook.webhook)
    val bot = new WBot()
    //telegramBotsApi.registerBot(bot, builder.build())

    callbacks.put("/", bot)
    info("Initialiced bot")
    bot
  }

}

case class JsonWriter2(writer: Writer) extends Writer {

  override def write[M](record: LogRecord[M], output: LogOutput, outputFormat: OutputFormat): Unit = {
    val l = record.timeStamp
    val trace = record.throwable.map(throwable2Trace)
    val r = Record(
      level = record.level.name,
      levelValue = record.levelValue,
      message = record.logOutput.plainText,
      fileName = record.fileName,
      className = record.className,
      methodName = record.methodName,
      line = record.line,
      column = record.column,
      data = record.data.map {
        case (key, value) => key -> value().toString
      },
      throwable = trace,
      timeStamp = l,
      date = l.t.F,
      time = s"${l.t.T}.${l.t.L}${l.t.z}"
    )
    val json = r.toValue
    val jsonString = format(json)
    writer.write(record, new TextOutput(jsonString), outputFormat)
  }

  private def throwable2Trace(throwable: Throwable): Trace = {
    val elements = throwable.getStackTrace.toList.map { e =>
      TraceElement(e.getClassName, e.getMethodName, e.getLineNumber)
    }
    Trace(throwable.getLocalizedMessage, elements, Option(throwable.getCause).map(throwable2Trace))
  }

  private lazy val factory = new JsonFactory()
    .enable(JsonParser.Feature.ALLOW_COMMENTS)
    .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
    .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
    .enable(JsonParser.Feature.ALLOW_YAML_COMMENTS)

  def format(value: Value): String = {
    val output = new ByteArrayOutputStream
    try {
      val gen = factory.createGenerator(output)
      try {
        //gen.setPrettyPrinter(new DefaultPrettyPrinter {
        //  _objectFieldValueSeparatorWithSpaces = ": "
        //})
        format(gen, value)
        gen.flush()
      } finally {
        gen.close()
      }
      output.flush()
      output.toString("UTF-8")
    } finally {
      output.close()
    }
  }

  protected def format(gen: JsonGenerator, value: Value): Unit =
    value match {
      case Obj(map) => {
        gen.writeStartObject()
        map.foreach {
          case (key, value) => {
            gen.writeFieldName(key)
            format(gen, value)
          }
        }
        gen.writeEndObject()
      }
      case Arr(vec) => {
        gen.writeStartArray()
        vec.foreach { value =>
          format(gen, value)
        }
        gen.writeEndArray()
      }
      case Bool(b) => gen.writeBoolean(b)
      case Num(n)  => gen.writeNumber(n.underlying())
      case Str(s)  => gen.writeString(s)
      case Null    => gen.writeNull()
    }
}
