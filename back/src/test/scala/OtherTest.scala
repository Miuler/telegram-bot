import com.fasterxml.jackson.core.{JsonFactory, JsonGenerator, JsonParser}
import fabric.rw.*
import fabric.{Arr, Bool, Null, Num, Obj, Str, Value}
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}

import java.io.ByteArrayOutputStream

class OtherTest extends AnyFeatureSpec with Matchers with BeforeAndAfterEach with GivenWhenThen {
  Feature("OtherTest") {
    Scenario("") {
      val j = UserTest("Hector Miuler", 42).toValue
      val jsonString = format(j)
      println(jsonString)
    }
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

case class UserTest(name: String, age: Int)

object UserTest {
  //implicit val mapRW: ReaderWriter[Map[String, String]] = ReaderWriter.stringMapRW
  implicit val rw: ReaderWriter[UserTest] = ccRW
}
