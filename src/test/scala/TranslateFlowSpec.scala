
import com.apanfilov.testtask.flow.TranslateFlow
import org.scalatest.{FlatSpec, Matchers, PrivateMethodTester}


class TranslateFlowSpec extends FlatSpec with Matchers with PrivateMethodTester {

  "renderJson" should "should render a json" in {
    val text:String = "Some text"

    val testFunction = TranslateFlow.renderJson("en","fr")
    val res = testFunction.apply(text)

    res shouldEqual ("{\"input_lang\":\"en\",\"output_lang\":\"fr\",\"text\":\"Some text\"}")
  }

}
