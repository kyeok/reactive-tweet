package controllers

import core.Env
import play.Logger
import play.api.Play.current
import play.api.libs.iteratee.Iteratee
import play.api.libs.oauth.{ConsumerKey, OAuthCalculator, RequestToken}
import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Harley on 2016. 8. 30..
  */
object Application extends Controller {

  def index = Action.async {

    val loggingIteratee = Iteratee.foreach[Array[Byte]] { (array: Array[Byte]) =>
      Logger.info(array.map(_.toChar).mkString)
    }

    credential().map {
      case (ck: ConsumerKey, rt: RequestToken) =>
        WS
          .url("https://stream.twitter.com/1.1/statuses/filter.json")
          .sign(OAuthCalculator(ck, rt))
          .withQueryString("track" -> "지진")
          .get {
            response => Logger.info("response status:" + response.status)
              loggingIteratee
          }
          .map {
            _ => Ok("Stream closed")
          }
    } getOrElse {
      Future.successful {
        InternalServerError("Twitter credintial Error")
      }
    }
  }

  def credential(): Option[(ConsumerKey, RequestToken)] = {

    //    import core.EnvParserInstance._

    val apiKey: Option[String] = Env.as[String]("apiKey")
    val apiSecret: Option[String] = Env.as[String]("apiSecret")
    val token: Option[String] = Env.as[String]("token")
    val tokenSecret: Option[String] = Env.as[String]("tokenSecret")

    println("-----------------------")
    println("-----------------------")
    println("-----------------------")
    println("-----------------------")
    println("apiKey=" + apiKey)
    println("apiSecret=" + apiSecret)
    println("token=" + token)
    println("tokenSecret=" + tokenSecret)
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
