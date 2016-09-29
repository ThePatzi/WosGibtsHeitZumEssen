package com.pichler.wosgibtsheitzumessen.model

import java.time.LocalDate

import com.fasterxml.jackson.annotation.{JsonCreator, JsonIgnore, JsonProperty}
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.{JsonDeserialize, JsonSerialize}
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.google.firebase.database.DataSnapshot
import com.pichler.wosgibtsheitzumessen.util.json.{DayMenuSerializer, LocalDateDeserializer, LocalDateSerializer}
import com.pichler.wosgibtsheitzumessen.util.Util.StrToDate
import com.pichler.wosgibtsheitzumessen.util.Util.DateToStr
import com.pichler.wosgibtsheitzumessen.util.firebase.FirebaseUtil._

import scala.beans.BeanProperty

/**
  * Created by Patrick on 15.09.2016.
  */
@JsonSerialize(using = classOf[DayMenuSerializer])
case class DayMenu @JsonCreator()(@JsonProperty("date")
                                  @JsonSerialize(using = classOf[LocalDateSerializer])
                                  @JsonDeserialize(using = classOf[LocalDateDeserializer]) date: LocalDate,
                                  @BeanProperty @JsonProperty("soup") soup: String,
                                  @BeanProperty @JsonProperty("menu1") menu1: String,
                                  @BeanProperty @JsonProperty("menu2") menu2: String,
                                  @BeanProperty @JsonProperty("specialMenu") specialMenu: SpecialMenu = SpecialMenu()) {

  @JsonIgnore
  def getDate: String = date.toString("dd.MM.yyyy")

  def actualSoup: String = {
    if (specialMenu != null && specialMenu.soup != null && !specialMenu.soup.trim.isEmpty)
      specialMenu.soup
    else
      soup
  }

  def hasMenu3: Boolean = {
    specialMenu != null && specialMenu.menu != null && !specialMenu.menu.trim.isEmpty
  }
}

case class SpecialMenu(@BeanProperty soup: String = null,
                       @BeanProperty menu: String = null)

object DayMenu {
  def parse(snapshot: DataSnapshot): DayMenu = {
    val date = snapshot.child("date").getStringValue.toLocalDate("dd.MM.yyyy")
    val soup = snapshot.child("soup").getStringValue
    val menu1 = snapshot.child("menu1").getStringValue
    val menu2 = snapshot.child("menu2").getStringValue

    val specialMenu = snapshot.child("specialMenu")

    DayMenu(date, soup, menu1, menu2, if (specialMenu.exists()) SpecialMenu.parse(specialMenu) else SpecialMenu())
  }
}

object SpecialMenu {
  def parse(snapshot: DataSnapshot): SpecialMenu = {
    val soup = snapshot.child("soup").getStringValue
    val menu = snapshot.child("menu").getStringValue

    SpecialMenu(soup, menu)
  }

}