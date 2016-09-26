package com.pichler.wosgibtsheitzumessen.telegrambot.bot

import java.time.temporal.ChronoField
import java.time.{DayOfWeek, LocalDate}

import com.pichler.wosgibtsheitzumessen.data.DayMenuDataStore
import com.pichler.wosgibtsheitzumessen.model.DayMenu
import com.pichler.wosgibtsheitzumessen.telegrambot.reminder.ReminderChatDataStore
import com.pichler.wosgibtsheitzumessen.util.Util._
import info.mukel.telegrambot4s.api.{Commands, Polling, TelegramApiException, TelegramBot}
import info.mukel.telegrambot4s.methods.ParseMode
import info.mukel.telegrambot4s.models.Message

import scala.collection.SortedMap
import scala.io.Source
import scala.util.Failure

/**
  * Created by Patrick on 23.09.2016.
  */
object WosGibtsHeitZumEssenBot extends TelegramBot with Polling with Commands {
  override def token: String = Source.fromFile("../credentials/bot.token").getLines().next

  def formatMarkdown(dayMenu: DayMenu): String = {
    if (dayMenu == null)
      "-"
    else
      s"""
         |*${dayMenu.date.toString("dd.MM.yyyy")}*
         |_Suppe:_ ${dayMenu.soup}
         |_Menu 1:_ ${dayMenu.menu1}
         |_Menu 2:_ ${dayMenu.menu2}
    """.stripMargin
  }

  def formatHTML(dayMenu: DayMenu): String = {
    if (dayMenu == null)
      s"<b>-</b>"
    else
      s"""
         |<b>${dayMenu.date.toString("dd.MM.yyyy")}</b>
         |<i>Suppe:</i> ${dayMenu.soup.escapeHTML()}
         |<i>Menu 1:</i> ${dayMenu.menu1.escapeHTML()}
         |<i>Menu 2:</i> ${dayMenu.menu2.escapeHTML()}
      """.stripMargin
  }

  def replyMarkdown(msg: String)(implicit message: Message): Unit = {
    reply(msg, parseMode = Option(ParseMode.Markdown)) onComplete {
      case Failure(tex: TelegramApiException) => println(tex)

      case _ =>
    }
  }

  def replyHtml(msg: String)(implicit message: Message): Unit = {
    if (msg.trim.isEmpty)
      reply("-")
    else
      reply(msg.trim, parseMode = Option(ParseMode.HTML)) onComplete {
        case Failure(tex: TelegramApiException) => println(tex)

        case _ =>
      }
  }

  val help = List(
    "/menu today - todays menu",
    "/menu [date] - menu at given date",
    "/menu [from] [until] - menus from from date to until date",
    "/menu week - menu for current week",
    "/menu week [nr] - menu for week nr [nr]",
    "/menu week [nr] [year] - menu for week nr [nr] of year [year]",
    "/subscribe - subscribe for daily updates",
    "/unsubscribe - unsubscribe from daily updates"
  )

  on("/hello") {
    implicit msg => _ => reply("World")
  }

  on("/help") {
    implicit msg => _ => {
      reply(help mkString "\n")
    }
  }

  on("/subscribe") {
    implicit msg => _ => {
      val id = msg.chat.id + ""

      if (ReminderChatDataStore.contains(id)) {
        reply("already subscribed")
      } else {
        ReminderChatDataStore += id
        reply("subscribed")
      }
    }
  }

  on("/unsubscribe") {
    implicit msg => _ => {
      val id = msg.chat.id + ""

      if (!ReminderChatDataStore.contains(id)) {
        reply("already subscribed")
      } else {
        ReminderChatDataStore -= id
        reply("unsubscribed")
      }
    }
  }

  on("/check") {
    implicit msg => _ => {
      println("test")
      val text: String = if (ReminderChatDataStore.contains(msg.chat.id + "")) "yes" else "no"
      println(text + " asdfasdf")
      reply(text) onComplete {
        case Failure(ex) => println(ex)
        case _ =>
      }
    }
  }

  on("/trigger") {
    implicit msg => _ => {
      ReminderChatDataStore.remind()
    }
  }

  on("/menu") {
    implicit msg => action => {
      action toList match {
        case "today" :: Nil => {
          replyHtml(formatHTML(DayMenuDataStore(LocalDate.now())))
        }

        case "week" :: Nil => {
          val today: LocalDate = LocalDate.now()
          val dayMenusList = DayMenuDataStore.between(today.`with`(DayOfWeek.MONDAY), today.`with`(DayOfWeek.SUNDAY))

          val dayMenus = SortedMap(dayMenusList.map(e => e.date.toString("dd.MM.yyyy") -> e): _*)
          val string: String = dayMenus.values.toStream.map(formatHTML).mkString("\n")
          replyHtml(string)
        }

        case "week" :: IntegerVar(nr) :: Nil => {
          val weekOfYear: LocalDate = LocalDate.now().`with`(ChronoField.ALIGNED_WEEK_OF_YEAR, nr)
          val dayMenusList = DayMenuDataStore.between(weekOfYear.`with`(DayOfWeek.MONDAY), weekOfYear.`with`(DayOfWeek.SUNDAY))

          val dayMenus = SortedMap(dayMenusList.map(e => e.date.toString("dd.MM.yyyy") -> e): _*)

          replyHtml(dayMenus.values.toStream.map(formatHTML).mkString("\n"))
        }

        case "week" :: IntegerVar(nr) :: IntegerVar(year) :: Nil => {
          val weekOfYear: LocalDate = LocalDate.now().`with`(ChronoField.YEAR, year).`with`(ChronoField.ALIGNED_WEEK_OF_YEAR, nr)
          val dayMenusList = DayMenuDataStore.between(weekOfYear.`with`(DayOfWeek.MONDAY), weekOfYear.`with`(DayOfWeek.SUNDAY))

          val dayMenus = SortedMap(dayMenusList.map(e => e.date.toString("dd.MM.yyyy") -> e): _*)

          replyHtml(dayMenus.values.toStream.map(formatHTML).mkString("\n"))
        }

        case LocalDateVar(date) :: Nil => {
          replyHtml(formatHTML(DayMenuDataStore(date)))
        }

        case LocalDateVar(from) :: LocalDateVar(until) :: Nil => {
          replyHtml(DayMenuDataStore.between(from, until).toStream.map(formatHTML).mkString("\n"))
        }
      }
    }
  }

  def start(): Unit = {
    run()
    DayMenuDataStore.start()
  }
}
