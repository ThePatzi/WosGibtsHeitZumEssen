package com.pichler.wosgibtsheitzumessen.telegrambot.reminder

import java.time._
import java.util.concurrent.Executors

import com.google.firebase.database.DataSnapshot
import com.pichler.wosgibtsheitzumessen.data.{DayMenuDataStore, FirebaseDataStorage}
import com.pichler.wosgibtsheitzumessen.model.DayMenu
import com.pichler.wosgibtsheitzumessen.telegrambot.bot.WosGibtsHeitZumEssenBot
import com.pichler.wosgibtsheitzumessen.util.TimeBasedExecution._
import com.pichler.wosgibtsheitzumessen.util.firebase.ChildEventAdapter
import com.pichler.wosgibtsheitzumessen.util.firebase.FirebaseUtil.toValueEventListenerUpdate
import info.mukel.telegrambot4s.methods.{ParseMode, SendMessage}

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
  * Created by Patrick on 25.09.2016.
  */
object ReminderChatDataStore {
  val telegramBotReminders = FirebaseDataStorage.firebaseDatabase.getReference("telegramBotReminders")
  val chatIds: mutable.Set[String] = mutable.HashSet()
  val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
  private var started: Boolean = false

  telegramBotReminders.addValueEventListener((snapshot: DataSnapshot) => {
    snapshot.getChildren.iterator().toStream.foreach(snapshot => chatIds += snapshot.getValue(classOf[String]))
  })

  telegramBotReminders.addChildEventListener(new ChildEventAdapter(
    childAdded = (dataSnapshot, p) => {
      val chatId: String = dataSnapshot.getValue(classOf[String])
      chatIds += chatId
    },
    childRemoved = (dataSnapshot) => {
      val chatId: String = dataSnapshot.getValue(classOf[String])
      chatIds -= chatId
    }))

  def start(): Unit = {
    if (started) {
      return
    }

    started = true

    scheduledExecutorService.scheduleAt(LocalTime.of(9, 0), () => remind())
  }

  def remind(): Unit = {
    val toSend: String = WosGibtsHeitZumEssenBot.formatHTML(DayMenuDataStore(LocalDate.now()))

    chatIds.map(chatId => SendMessage(Right(chatId), toSend, parseMode = Option(ParseMode.HTML)))
      .foreach(msg => WosGibtsHeitZumEssenBot.api.request(msg))
  }

  def contains(id: String): Boolean = {
    chatIds.contains(id)
  }

  def +=(id: String): Boolean = {
    if (contains(id))
      return false

    chatIds += id
    telegramBotReminders.child(id).setValue(id)

    true
  }

  def -=(id: String): Boolean = {
    if (contains(id)) {
      chatIds -= id
      telegramBotReminders.child(id).removeValue()

      return true
    }

    false
  }
}
