package com.pichler.wosgibtsheitzumessen

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model.Element

/**
  * Created by ppichler on 15.09.2016.
  */
object Main extends App {

  val browser = JsoupBrowser()
  val doc = browser.get("http://www.netzwerk111.at/restaurant-hartberg/mittagsmenu/")

  val elements: List[Element] = doc >> elementList(".menu1")

  println(elements)
}
