package com.pichler.wosgibtsheitzumessen.data

import java.time.LocalDate

import com.pichler.wosgibtsheitzumessen.model.DayMenu

import scala.collection.mutable

/**
  * Created by Patrick on 17.09.2016.
  */
object DayMenuDataStore {
  private val menuMap = new mutable.HashMap[LocalDate, DayMenu]()
    .withDefaultValue(null)

  def apply(date: LocalDate): DayMenu = menuMap(date)

  def update(date: LocalDate, dayMenu: DayMenu): Unit = menuMap(date) = dayMenu

  def +=(dayMenu: DayMenu): Unit = update(dayMenu.date, dayMenu)

  def all: List[DayMenu] = menuMap.values toList

}
