
import com.apanfilov.testtask.utility.TextHelper
import org.scalatest.{FlatSpec, Matchers}


class TextHelperSpec extends FlatSpec with Matchers {

  "TestHelper" should "split incoming text into List of sentences" in {
    val text:String = "Hello! It's a test. Okay?"

    val testFunction = TextHelper.splitBySentence
    val res = testFunction.apply(text)

    res(0) shouldEqual ("Hello!", 0)
    res(1) shouldEqual ("It's a test.", 1)
    res(2) shouldEqual ("Okay?", 2)
  }

  it should "inject id to text with pattern {id}text" in {
    val tuple:(Int, (String, Int)) = (1, ("Some sentence.", 1))
    val testFunction = TextHelper.injectReviewId
    val res = testFunction.apply(tuple)

    res shouldEqual ("{1_1}Some sentence.")
  }
  it should "parse the key from a String with pattern {id}value to tuple (id, value)" in {
    val pattern:String = "{1}Some sentence."
    val testFunction = TextHelper.pasteReviewTogether
    val res = testFunction.apply(pattern)

    res shouldEqual (1, "ABC")

  }


}
