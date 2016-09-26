package com.pichler.wosgibtsheitzumessen.refresher

import java.time.{LocalDate, LocalTime}
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

import com.pichler.wosgibtsheitzumessen.data.DayMenuDataStore
import com.pichler.wosgibtsheitzumessen.model.DayMenu
import com.pichler.wosgibtsheitzumessen.util.Util.{StrToDate, funcToRunnable}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import com.pichler.wosgibtsheitzumessen.util.Util._
import com.pichler.wosgibtsheitzumessen.util.TimeBasedExecution.TimeBasedExecution

import scala.language.postfixOps

/**
  * Created by Patrick on 18.09.2016.
  */
object Updater {
  var executorService: ScheduledExecutorService = Executors.newScheduledThreadPool(2)

  private def parseMenuItem(date: LocalDate, element: Element): DayMenu = element.innerHtml.replaceAll("<[^<>]*>", "")
    .replaceAll("<br>*", "\n")
    .split("\n")
    .toStream
    .filterNot(_.trim.isEmpty) filter (_.contains(":")) map (_.split(":")(1).trim) toList match {
    case soup :: menu1 :: menu2 :: r => DayMenu(date, soup, menu1, menu2)
    case _ => null
  }

  def doUpdate(): Unit = {
    val browser = JsoupBrowser()
    val doc = browser.get("http://www.netzwerk111.at/restaurant-hartberg/mittagsmenu/")

    (doc >> elementList(".menu1")).toStream.flatMap(_ >> elementList(".inner-box"))
      .filter({ e => (e >> text(".menu-title2")).matches("[A-Za-z].*, [0-9.]*") })
      .map(e => parseMenuItem((e >> text(".menu-title2")).toLocalDate("eeee, dd.MM.yyyy"), e))
      .foreach(DayMenuDataStore += _)
  }

  def scheduleUpdate(): Unit = {
    doUpdate()

    executorService.scheduleAt(LocalTime.of(8, 0), () => doUpdate())

    println("update")
  }
}
