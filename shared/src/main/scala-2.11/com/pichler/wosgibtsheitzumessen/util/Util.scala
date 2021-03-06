package com.pichler.wosgibtsheitzumessen.util

import java.time.{LocalDate, ZoneId}
import java.time.format.DateTimeFormatter
import java.util.{Date, Locale}

import scala.collection.mutable
import scala.util.Try

object Util {
  val dateFormatterMap = new mutable.HashMap[String, DateTimeFormatter]()

  object LocalDateVar {
    def unapply(str: String): Option[LocalDate] = {
      if (!str.isEmpty) {
        Try(str.toLocalDate("dd.MM.yyyy")).toOption
      } else {
        None
      }
    }
  }

  object IntegerVar {
    def unapply(str: String): Option[Int] = {
      if (!str.isEmpty) {
        Try(str.toInt).toOption
      } else {
        None
      }
    }
  }

  implicit class StrToDate(str: String) {
    def toLocalDate(pattern: String, locale: Locale = Locale.GERMANY): LocalDate = {
      val formatter = dateFormatterMap.getOrElseUpdate(pattern, DateTimeFormatter.ofPattern(pattern).withLocale(locale))

      LocalDate.parse(str, formatter)
    }
  }

  implicit class DateToStr(localDate: LocalDate) {
    def toString(pattern: String): String = {
      val formatter = dateFormatterMap.getOrElseUpdate(pattern, DateTimeFormatter.ofPattern(pattern))

      localDate.format(formatter)
    }
  }

  implicit def funcToRunnable(func: () => Unit): Runnable = new Runnable {
    override def run(): Unit = func()
  }

  implicit class EscapedString(string: String) {
    def escapeHTML(): String = {
      string.replaceAll("<", "$lt;").replaceAll(">", "$gt;").replaceAll("&", "&amp;")
    }
  }

  implicit def dateToLocalDate(date: Date): LocalDate = {
    date.toInstant.atZone(ZoneId.systemDefault()).toLocalDate
  }
}