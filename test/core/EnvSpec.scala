package core

import org.scalatest.{FunSuite, Matchers}

/**
  * Created by Harley on 2016. 9. 8..
  */
class EnvSpec extends FunSuite with Matchers {
  test("apiKey") {
    Env.as[Int]("apiKey") shouldBe Some(123)
    Env.as[String]("apiToken") shouldBe Some("abcd")
    Env.as[Boolean]("hasKey") shouldBe Some(true)
  }
  //-DapiKey=123

}
