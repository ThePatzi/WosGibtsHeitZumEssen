package com.pichler.wosgibtsheitzumessen.telegrambot.bot

import java.time.temporal.ChronoField
import java.time.{DayOfWeek, LocalDate}

import com.google.firebase.database.DatabaseReference
import com.pichler.wosgibtsheitzumessen.data.{DayMenuDataStore, FirebaseDataStorage}
import com.pichler.wosgibtsheitzumessen.model.DayMenu
import com.pichler.wosgibtsheitzumessen.util.Util._
import info.mukel.telegrambot4s.api.{Commands, Polling, TelegramBot}
import info.mukel.telegrambot4s.methods.ParseMode
import info.mukel.telegrambot4s.models.Message

import scala.collection.SortedMap
import scala.io.Source

/**
  * Created by Patrick on 23.09.2016.
  */
object WosGibtsHeitZumEssenBot extends TelegramBot with Polling with Commands {
  override def token: String = Source.fromFile("../credentials/bot.token").getLines().next

  def format(dayMenu: DayMenu): String = {
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

  def replyMarkdown(msg: String)(implicit message: Message): Unit = {
    reply(msg, parseMode = Option(ParseMode.Markdown))
  }

  val help = List(
    "/menu today - todays menu",
    "/menu [date] - menu at given date",
    "/menu [from] [until] - menus from from date to until date",
    "/menu week - menu for current week",
    "/menu week [nr] - menu for week nr [nr]",
    "/menu week [nr] [year] - menu for week nr [nr] of year [year]"
  )

  on("/hello") {
    implicit msg => _ => reply("World")
  }

  on("/help") {
    implicit msg => _ => {
      reply(help mkString "\n")
    }
  }

  on("/menu") {
    implicit msg => action => {
      action toList match {
        case "today" :: Nil => {
          replyMarkdown(format(DayMenuDataStore(LocalDate.now())))
        }

        case "week" :: Nil => {
          val today: LocalDate = LocalDate.now()
          val dayMenusList = DayMenuDataStore.between(today.`with`(DayOfWeek.MONDAY), today.`with`(DayOfWeek.SUNDAY))

          val dayMenus = SortedMap(dayMenusList.map(e => e.date.toString("dd.MM.yyyy") -> e): _*)
          replyMarkdown(dayMenus.values.toStream.map(format).mkString("\n"))
        }

        case "week" :: IntegerVar(nr) :: Nil => {
          val weekOfYear: LocalDate = LocalDate.now().`with`(ChronoField.ALIGNED_WEEK_OF_YEAR, nr)
          val dayMenusList = DayMenuDataStore.between(weekOfYear.`with`(DayOfWeek.MONDAY), weekOfYear.`with`(DayOfWeek.SUNDAY))

          val dayMenus = SortedMap(dayMenusList.map(e => e.date.toString("dd.MM.yyyy") -> e): _*)

          replyMarkdown(dayMenus.values.toStream.map(format).mkString("\n"))
        }

        case "week" :: IntegerVar(nr) :: IntegerVar(year) :: Nil => {
          val weekOfYear: LocalDate = LocalDate.now().`with`(ChronoField.YEAR, year).`with`(ChronoField.ALIGNED_WEEK_OF_YEAR, nr)
          val dayMenusList = DayMenuDataStore.between(weekOfYear.`with`(DayOfWeek.MONDAY), weekOfYear.`with`(DayOfWeek.SUNDAY))

          val dayMenus = SortedMap(dayMenusList.map(e => e.date.toString("dd.MM.yyyy") -> e): _*)

          replyMarkdown(dayMenus.values.toStream.map(format).mkString("\n"))
        }

        case LocalDateVar(date) :: Nil => {
          replyMarkdown(format(DayMenuDataStore(date)))
        }

        case LocalDateVar(from) :: LocalDateVar(until) :: Nil => {
          replyMarkdown(DayMenuDataStore.between(from, until).toStream.map(format).mkString("\n"))
        }
      }
    }
  }
}
