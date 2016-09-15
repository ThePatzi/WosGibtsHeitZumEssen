package com.pichler.wosgibtsheitzumessen

import com.pichler.wosgibtsheitzumessen.model.DayMenu
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element


/**
  * Created by ppichler on 15.09.2016.
  */
object Main extends App {

  def getMenuItem(date: String, element: Element): DayMenu = element.innerHtml.replaceAll("<[^<>]*>", "")
    .replaceAll("<br>", "\n").replaceAll("\n{2,}", "")
    .split("\n")
    .toStream
    .filterNot(_.trim.isEmpty) filter (_.contains(":")) map (_.split(":")(1).trim) toList match {
    case soup :: menu1 :: menu2 :: r => DayMenu(date, soup, menu1, menu2)
    case _ => null
  }

  val browser = JsoupBrowser()
  val doc = browser.get("http://www.netzwerk111.at/restaurant-hartberg/mittagsmenu/")

  val elements: List[Element] = doc >> elementList(".menu1")

  if (elements.size < 2)
    System.exit(0)

  val menu = elements(1)

  val boxes = menu >> elementList(".inner-box")

  val test = boxes map (e => getMenuItem(e >> text(".menu-title2"), e >> element(".menu-description2")))

  test.foreach(println(_))
}
