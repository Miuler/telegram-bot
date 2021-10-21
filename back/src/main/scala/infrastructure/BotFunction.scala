package infrastructure

import com.google.cloud.functions.{HttpFunction, HttpRequest, HttpResponse}

class BotFunction extends HttpFunction {
  override def service(request: HttpRequest, response: HttpResponse): Unit = {
    import java.io.PrintWriter
    val writer = new PrintWriter(response.getWriter)
    writer.printf("Hello %s!", "Test")
  }
}
