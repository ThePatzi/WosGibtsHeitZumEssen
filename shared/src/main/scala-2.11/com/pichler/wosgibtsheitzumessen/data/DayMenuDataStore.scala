package com.pichler.wosgibtsheitzumessen.data

import java.time.LocalDate

import com.google.firebase.database._
import com.pichler.wosgibtsheitzumessen.model.DayMenu

import scala.collection.mutable
import scala.collection.JavaConversions._
import com.pichler.wosgibtsheitzumessen.util.FirebaseUtil.toValueEventListenerUpdate
import com.pichler.wosgibtsheitzumessen.util.Util.DateToStr

/**
  * Created by Patrick on 17.09.2016.
  */
object DayMenuDataStore {
  private val menuReference = FirebaseDataStorage.firebaseDatabase.getReference("menu")

  private val menuMap = new mutable.HashMap[LocalDate, DayMenu]()
    .withDefaultValue(null)

  menuReference.addValueEventListener((snapshot: DataSnapshot) => {
    snapshot.getChildren.iterator.toStream.map(_.getValue()).filter(_ != null).filter(_.getClass == classOf[java.util.HashMap[String, String]])
      .map(o => o.asInstanceOf[java.util.HashMap[String, String]]).map(DayMenu.parse(_)).foreach(e => menuMap(e.date) = e)
  })

  menuReference.addChildEventListener(new ChildEventListener {
    def handleUpdate(dataSnapshot: DataSnapshot): Unit = {
      if (dataSnapshot.getValue == null || dataSnapshot.getValue.getClass == classOf[java.util.HashMap[String, String]]) {
        return
      }

      val d = DayMenu.parse(dataSnapshot.getValue.asInstanceOf[java.util.HashMap[String, String]])

      menuMap(d.date) = d
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

  def +=(dayMenu: DayMenu): Unit = update(dayMenu.date, dayMenu)

  def all: List[DayMenu] = menuMap.values toList

  def between(fromDate: LocalDate, toDate: LocalDate): List[DayMenu] = {
    all.toStream.filter(m => !m.date.isBefore(fromDate) &&
      (m.date.isBefore(toDate) || m.date.isEqual(toDate)))
      .toList
      .sortWith((d, d1) => d.date.compareTo(d1.date) < 0)
  }
}
