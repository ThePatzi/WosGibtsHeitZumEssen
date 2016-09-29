package com.pichler.wosgibtsheitzumessen.data

import java.time.LocalDate
import java.util.concurrent.CompletableFuture

import com.google.firebase.database._
import com.pichler.wosgibtsheitzumessen.model.{DayMenu, SpecialMenu}

import scala.collection.mutable
import scala.collection.JavaConversions._
import com.pichler.wosgibtsheitzumessen.util.firebase.FirebaseUtil.toValueEventListenerUpdate
import com.pichler.wosgibtsheitzumessen.util.Util.DateToStr

/**
  * Created by Patrick on 17.09.2016.
  */
object DayMenuDataStore {
  private val menuReference = FirebaseDataStorage.firebaseDatabase.getReference("menu")

  private val menuMap = new mutable.HashMap[LocalDate, DayMenu]()
    .withDefaultValue(null)

  private val completeableFuture = new CompletableFuture[Boolean]()

  menuReference.addListenerForSingleValueEvent((snapshot: DataSnapshot) => {
    snapshot.getChildren.iterator.toStream.map(DayMenu.parse).foreach(e => menuMap(e.date) = e)

    completeableFuture.complete(true)

    menuReference.addListenerForSingleValueEvent((snapshot: DataSnapshot) => {
      snapshot.getChildren.iterator.toStream.map(DayMenu.parse).foreach(e => menuMap(e.date) = e)
    })

  })


  menuReference.addChildEventListener(new ChildEventListener {
    def handleUpdate(dataSnapshot: DataSnapshot): Unit = {
      if (dataSnapshot.getValue == null) {
        return
      }

      val dayMenu = DayMenu.parse(dataSnapshot)
      menuMap(dayMenu.date) = dayMenu
    }

    override def onChildRemoved(dataSnapshot: DataSnapshot): Unit = {}

    override def onChildMoved(dataSnapshot: DataSnapshot, s: String): Unit = {}

    override def onChildChanged(dataSnapshot: DataSnapshot, s: String): Unit = {
      handleUpdate(dataSnapshot)
    }

    override def onCancelled(databaseError: DatabaseError): Unit = {}

    override def onChildAdded(dataSnapshot: DataSnapshot, s: String): Unit = {
      handleUpdate(dataSnapshot)
    }
  })

  def apply(date: LocalDate): DayMenu = menuMap(date)

  def update(date: LocalDate, dayMenu: DayMenu): Unit = {
    menuMap(date) = dayMenu
    menuReference.child(date.toString("ddMMyyyy")).setValue(dayMenu)
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
    completeableFuture.get()
  }
}
