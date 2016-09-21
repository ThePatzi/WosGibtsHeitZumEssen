package com.pichler.wosgibtsheitzumessen.model

import java.time.LocalDate

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import com.fasterxml.jackson.databind.annotation.{JsonDeserialize, JsonSerialize}
import com.pichler.wosgibtsheitzumessen.util.json.{LocalDateDeserializer, LocalDateSerializer}
import com.pichler.wosgibtsheitzumessen.util.Util.StrToDate
import com.pichler.wosgibtsheitzumessen.util.Util.DateToStr

import scala.beans.BeanProperty

/**
  * Created by Patrick on 15.09.2016.
  */
case class DayMenu(@JsonProperty("date")
                   @JsonSerialize(using = classOf[LocalDateSerializer])
                   @JsonDeserialize(using = classOf[LocalDateDeserializer]) date: LocalDate,
                   @BeanProperty @JsonProperty("soup") soup: String,
                   @BeanProperty @JsonProperty("menu1") menu1: String,
                   @BeanProperty @JsonProperty("menu2") menu2: String) {
  @JsonIgnore
  def getDate: String = date.toString("dd.MM.yyyy")
}

object DayMenu {
  def parse(map: Map[String, String]): DayMenu = DayMenu(map("date").toLocalDate("dd.MM.YYYY"), map("soup"), map("menu1"), map("menu2"))

  def parse(map: scala.collection.mutable.Map[String, String]): DayMenu = DayMenu(map("date").toLocalDate("dd.MM.yyyy"), map("soup"), map("menu1"), map("menu2"))
}