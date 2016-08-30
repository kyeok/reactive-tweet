package controllers

import play.api.mvc.{Action, Controller}

/**
  * Created by Harley on 2016. 8. 30..
  */
object Application extends Controller {

  def index = Action { implicit request =>
    Ok("hello world!!")
  }
}
