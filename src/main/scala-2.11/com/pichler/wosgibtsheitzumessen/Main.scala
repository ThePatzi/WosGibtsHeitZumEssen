package com.pichler.wosgibtsheitzumessen

import com.pichler.wosgibtsheitzumessen.services.Services
import com.pichler.wosgibtsheitzumessen.update.Updater
import org.http4s.server.{Server, ServerApp}

import scalaz.concurrent.Task


/**
  * Created by ppichler on 15.09.2016.
  */
object Main extends ServerApp {
  Updater.scheduleUpdate()

  override def server(args: List[String]): Task[Server] = {
    Services.builder.start
  }
}
