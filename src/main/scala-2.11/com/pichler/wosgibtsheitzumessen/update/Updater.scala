package com.pichler.wosgibtsheitzumessen.update

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import com.pichler.wosgibtsheitzumessen.data.DayMenuDataStore
import com.pichler.wosgibtsheitzumessen.model.DayMenu
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element

import scala.language.postfixOps
import scala.util.parsing.json.JSON

/**
  * Created by Patrick on 18.09.2016.
  */
object Updater {
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

    val dtf = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy").withLocale(Locale.GERMANY)

    (doc >> elementList(".menu1")).toStream.flatMap(_ >> elementList(".inner-box"))
      .filter({ e => (e >> text(".menu-title2")).matches("[A-Za-z].*, [0-9.]*") })
      .map(e => parseMenuItem(LocalDate.parse(e >> text(".menu-title2"), dtf), e))
      .foreach(DayMenuDataStore += _)
  }

  def scheduleUpdate(): Unit = {

  }
}
