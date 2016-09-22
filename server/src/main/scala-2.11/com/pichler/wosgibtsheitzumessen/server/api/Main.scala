package com.pichler.wosgibtsheitzumessen.server.api

import com.pichler.wosgibtsheitzumessen.server.api.services.Services
import org.http4s.server.{Server, ServerApp}

import scalaz.concurrent.Task

/**
  * Created by ppichler on 15.09.2016.
  */
object Main extends ServerApp {
  override def server(args: List[String]): Task[Server] = {
    Services.builder.start
  }
}
