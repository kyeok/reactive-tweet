package controllers

import core.Env
import play.api.mvc.{Action, Controller}

/**
  * Created by Harley on 2016. 8. 30..
  */
object Application extends Controller {

  def index = Action { implicit request =>
    Ok("hello world!!")
  }

  case class ConsumerKey(apiKey: String, apiSecret: String)

  case class RequestToken(token: String, tokenSecret: String)


  def credential(): Option[(ConsumerKey, RequestToken)] = {

    val apiKey: Option[String] = Env.as[String]("twitter.apiKey")
    val apiSecret: Option[String] = Env.as[String]("twitter.apiSecret")
    val token: Option[String] = Env.as[String]("twitter.token")
    val tokenSecret: Option[String] = Env.as[String]("twitter.tokenSecret")

    // 1개라도 값이 없을경우에는 None
    // 모두 4개의 값이 모두 다 Some => Some(ConsumerKey, RequestToken)

    //문제 : option에서 값을 꺼내서 option(tuple)로 변환해보자

    //방법1. flatMap
    val result1: Option[(ConsumerKey, RequestToken)] = apiKey.flatMap {
      (ak: String) => apiSecret.flatMap {
        (as: String) => token.flatMap {
          (t: String) => tokenSecret.flatMap {
            (ts: String) => Some(ConsumerKey(ak, as), RequestToken(t, ts))
          }
        }
      }
    }

    //방법2. for comprehension. <- 를 쓰면 flatmap을 해준다. 모든 값이 있을 때 yield를 불러 줌
    val result2: Option[(ConsumerKey, RequestToken)] = for {
      ak2 <- apiKey
      as2 <- apiSecret
      t2 <- token
      ts2 <- tokenSecret
    } yield {
      (ConsumerKey(ak2, as2), RequestToken(t2, ts2))
    }

    //방법3-2. match case 묶어쓰기
    val result4: Option[(ConsumerKey, RequestToken)] = (apiKey, apiSecret, token, tokenSecret) match {
      case (Some(ak4), Some(as4), Some(t4), Some(ts4)) => Some(ConsumerKey(ak4, as4), RequestToken(t4, ts4))
      case _ => None
    }

    result4
  }

}
