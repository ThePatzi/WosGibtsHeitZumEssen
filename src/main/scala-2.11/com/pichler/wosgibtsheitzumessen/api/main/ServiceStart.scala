package com.pichler.wosgibtsheitzumessen.api.main

import com.pichler.wosgibtsheitzumessen.api.services.Services
import com.pichler.wosgibtsheitzumessen.update.Updater
import org.http4s.server.{Server, ServerApp}

import scalaz.concurrent.Task

/**
  * Created by ppichler on 15.09.2016.
  */
object ServiceStart extends ServerApp {
  override def server(args: List[String]): Task[Server] = {
    Services.builder.start
  }
}
