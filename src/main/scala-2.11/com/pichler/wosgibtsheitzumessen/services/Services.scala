package com.pichler.wosgibtsheitzumessen.services

import org.http4s._
import org.http4s.dsl._
import org.http4s.server.Server
import org.http4s.server.blaze._
import org.http4s.server.syntax._

/**
  * Created by Patrick on 18.09.2016.
  */
object Services {
  val helloWorldService = HttpService {
    case GET -> Root / "hello" / name => Ok(s"Hello, $name")
  }

  val builder = BlazeBuilder.bindHttp(8080, "localhost").mountService(helloWorldService, "/")

}
