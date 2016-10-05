package com.pichler.wosgibtsheitzumessen.refresher.mail

import java.nio.file.Paths
import java.time.temporal.ChronoField
import java.time.{LocalDate, ZoneId}
import java.util.{Date, Properties}
import java.util.concurrent.Executors
import javax.mail._
import javax.mail.event._

import com.pichler.wosgibtsheitzumessen.data.DayMenuDataStore
import com.pichler.wosgibtsheitzumessen.model.SpecialMenu
import com.pichler.wosgibtsheitzumessen.util.Util._
import com.sun.mail.imap.IdleManager

import scala.io.Source

/**
  * Created by Patrick on 26.09.2016.
  */
object SpecialMenuParser {
  def getMailTextContent(message: Message): String = {
    message.getContent match {
      case multiPart: Multipart => multiPart.getBodyPart(0).getContent.toString
      case _ => null
    }
  }

  def parseDailyMenu(message: Message): SpecialMenu = {
    val text = getMailTextContent(message)

    if (text == null) {
      return null
    }

    val lines = text.split("\n").toStream
      .map(_.replaceAll("\r", ""))
      .filter(_.matches("^[ ]+.+$"))
      .map(_.trim.replaceAll("€.*", ""))
      .toList

    val partition: (List[String], List[String]) = lines.partition(_.toLowerCase().contains("suppe"))

    val soup = partition._1.mkString(" ").trim
    val menu = partition._2.mkString(" ").trim

    SpecialMenu(soup, menu)
  }

  def handleMessages(messages: Array[Message]): Unit = {
    messages.toStream
      .filter(_.getSubject.toLowerCase.contains("tagesempfehlung"))
      .filterNot(m => DayMenuDataStore.hasSpecialMenu(m.getSentDate))
      .map(m => (m, parseDailyMenu(m)))
      .filter(_._2 != null)
      .foreach(pair => {
        val (message, specialMenu) = pair

        val date = LocalDate.from(message.getSentDate.toInstant.atZone(ZoneId.systemDefault()))

        println(s"${date.toString("dd.MM.yyyy")} $specialMenu")

        DayMenuDataStore.updateSpecialMenu(date, specialMenu)
      })
  }

  val properties = new Properties()
  properties.load(Source.fromFile(Paths.get("..", "credentials", "email.properties").toFile).bufferedReader())

  val mailSession = Session.getDefaultInstance(properties)
  val store: Store = mailSession.getStore("imaps")
  val idleManager = new IdleManager(mailSession, Executors.newSingleThreadExecutor())

  def start(): Unit = {
    store.connect(properties.getProperty("host"), properties.getProperty("user"), properties.getProperty("password"))

    val folder = store.getFolder("INBOX")
    folder.open(Folder.READ_WRITE)

    folder.addMessageCountListener(new MessageCountAdapter {
      override def messagesAdded(e: MessageCountEvent): Unit = {
        handleMessages(e.getMessages)
      }
    })

    handleMessages(folder.getMessages)


    idleManager.watch(folder)
  }

  def close(): Unit = {
    idleManager.stop()
    store.close()
  }


}
