package com.pichler.wosgibtsheitzumessen.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.collection.mutable

object Util {
  val dateFormatterMap = new mutable.HashMap[String, DateTimeFormatter]()

  implicit class StrToDate(str: String) {
    def toLocalDate(pattern: String): LocalDate = {
      val formatter = dateFormatterMap.getOrElseUpdate(pattern, DateTimeFormatter.ofPattern(pattern))

      LocalDate.parse(str, formatter)
    }
  }

  implicit class DateToStr(localDate: LocalDate) {
    def toString(pattern: String): String = {
      val formatter = dateFormatterMap.getOrElseUpdate(pattern, DateTimeFormatter.ofPattern(pattern))

      localDate.format(formatter)
    }
  }

}