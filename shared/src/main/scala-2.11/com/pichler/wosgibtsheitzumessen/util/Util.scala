package com.pichler.wosgibtsheitzumessen.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import scala.collection.mutable

object Util {
  val dateFormatterMap = new mutable.HashMap[String, DateTimeFormatter]()

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

}