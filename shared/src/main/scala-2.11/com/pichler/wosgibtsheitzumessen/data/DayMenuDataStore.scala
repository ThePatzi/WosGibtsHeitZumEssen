package com.pichler.wosgibtsheitzumessen.data

import java.time.LocalDate
import java.util.concurrent.CompletableFuture

import com.google.firebase.database._
import com.pichler.wosgibtsheitzumessen.model.{DayMenu, SpecialMenu}

import scala.collection.mutable
import scala.collection.JavaConversions._
import com.pichler.wosgibtsheitzumessen.util.firebase.FirebaseUtil.toValueEventListenerUpdate
import com.pichler.wosgibtsheitzumessen.util.Util.DateToStr
import com.pichler.wosgibtsheitzumessen.util.firebase.ChildEventAdapter

/**
  * Created by Patrick on 17.09.2016.
  */
object DayMenuDataStore {
  private val menuReference = FirebaseDataStorage.firebaseDatabase.getReference("menu")

  private val menuMap = new mutable.HashMap[LocalDate, DayMenu]()
    .withDefaultValue(null)

  private val specialMenuListeners: mutable.MutableList[DayMenu => Unit] = mutable.MutableList()

  private val completableFuture = new CompletableFuture[Boolean]()

  menuReference.addListenerForSingleValueEvent(toValueEventListenerUpdate((snapshot: DataSnapshot) => {
    snapshot.getChildren.iterator.toStream.map(DayMenu.parse).foreach(e => menuMap(e.date) = e)

    completableFuture.complete(true)
  }))

  private def updateMenuMap(dayMenu: DayMenu): Unit = {
    val old = menuMap(dayMenu.date)
    menuMap(dayMenu.date) = dayMenu

    if (old != null && old.specialMenu != dayMenu.specialMenu) {
      specialMenuListeners.foreach(_ (dayMenu))
    }
  }

  private def handleUpdate(dataSnapshot: DataSnapshot): Unit = {
    if (dataSnapshot.getValue == null) {
      return
    }

    val dayMenu = DayMenu.parse(dataSnapshot)
    updateMenuMap(dayMenu)
  }

  menuReference.addChildEventListener(new ChildEventAdapter(
    childAdded = (dataSnapshot, p) => {
      handleUpdate(dataSnapshot)
    },
    childChanged = (dataSnapshot, p) => {
      handleUpdate(dataSnapshot)
    })
  )


  def apply(date: LocalDate): DayMenu = menuMap(date)

  def update(date: LocalDate, dayMenu: DayMenu): Unit = {
    menuMap(date) = dayMenu
    menuReference.child(date.toString("ddMMyyyy")).setValue(dayMenu)
  }

  def hasSpecialMenu(date: LocalDate): Boolean = {
    val specialMenu = menuMap(date).specialMenu

    specialMenu != null && ((specialMenu.menu != null && !specialMenu.menu.trim.isEmpty) ||
      (specialMenu.soup != null && specialMenu.soup.trim.isEmpty))
  }

  def merge(date: LocalDate, dayMenu: DayMenu): DayMenu = {
    val menu = menuMap(date)

    if (menu == null) {
      update(date, dayMenu)
      dayMenu
    } else {
      val copy: DayMenu = dayMenu.copy(specialMenu = menu.specialMenu)
      update(date, copy)
      copy
    }
  }

  def updateSpecialMenu(date: LocalDate, specialMenu: SpecialMenu): Unit = {
    val menu: DayMenu = menuMap(date)

    if (menu == null) {
      return
    }

    update(date, menu.copy(specialMenu = specialMenu))
  }

  def +=(dayMenu: DayMenu): Unit = update(dayMenu.date, dayMenu)

  def +=(l: DayMenu => Unit): Unit = specialMenuListeners += l

  def <>(dayMenu: DayMenu): Unit = {
    merge(dayMenu.date, dayMenu)
  }

  def all: List[DayMenu] = menuMap.values toList

  def between(fromDate: LocalDate, toDate: LocalDate): List[DayMenu] = {
    all.toStream.filter(m => !m.date.isBefore(fromDate) &&
      (m.date.isBefore(toDate) || m.date.isEqual(toDate)))
      .toList
      .sortWith((d, d1) => d.date.compareTo(d1.date) < 0)
  }

  def start(): Unit = {}

  def startAndWaitForInitialized(): Unit = {
    completableFuture.get()
  }
}
