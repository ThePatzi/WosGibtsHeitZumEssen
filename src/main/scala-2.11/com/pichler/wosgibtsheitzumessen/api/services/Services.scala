package com.pichler.wosgibtsheitzumessen.api.services

import java.time.temporal.ChronoField
import java.time.{DayOfWeek, LocalDate}

import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.pichler.wosgibtsheitzumessen.data.DayMenuDataStore
import com.pichler.wosgibtsheitzumessen.model.DayMenu
import com.pichler.wosgibtsheitzumessen.util.Util.{DateToStr, StrToDate}
import org.http4s._
import org.http4s.dsl._
import org.http4s.server.blaze._

import scala.collection.immutable.SortedMap
import scala.util.Try

/**
  * Created by Patrick on 18.09.2016.
  */
object Services {
  val objectMapper = new ObjectMapper()
  objectMapper.registerModule(DefaultScalaModule)

  val prettyObjectMapper = new ObjectMapper()
  prettyObjectMapper.registerModule(DefaultScalaModule)
  prettyObjectMapper.enable(SerializationFeature.INDENT_OUTPUT)

  def getObjectMapper(pretty: Boolean): ObjectMapper = if (pretty) prettyObjectMapper else objectMapper

  val helloWorldService = HttpService {
    case GET -> Root / "hello" / name => Ok(s"Hello, $name")
  }

  object LocalDateVar {
    def unapply(str: String): Option[LocalDate] = {
      if (!str.isEmpty) {
        Try(str.toLocalDate("dd.MM.yyyy")).toOption
      } else {
        None
      }
    }
  }

  object IntVar {
    def unapply(str: String): Option[Int] = {
      if (!str.isEmpty) {
        Try(str.toInt).toOption
      } else {
        None
      }
    }
  }

  def objectToJSON(o: Any, queryParams: Map[String, _]): String = {
    getObjectMapper(if (queryParams != null) queryParams.contains("pretty") else false)
      .writeValueAsString(o)
  }

  val menuAPI = HttpService {
    case GET -> Root / "menu" / LocalDateVar(fromDate) / LocalDateVar(toDate) :? queryParams => {

      val dayMenusList = DayMenuDataStore.between(fromDate, toDate)

      val dayMenus = SortedMap(dayMenusList.map(e => e.date.toString("dd.MM.yyyy") -> e): _*)

      if (dayMenus != null) {
        Ok(objectToJSON(dayMenus, queryParams))
      } else {
        Ok("{}")
      }
    }

    case GET -> Root / "menu" / "today" :? queryParams => {
      val dayMenu = DayMenuDataStore(LocalDate.now())

      if (dayMenu != null) {
        Ok(objectToJSON(dayMenu, queryParams))
      } else {
        Ok("{}")
      }
    }

    case GET -> Root / "menu" / "week" :? queryParams => {
      val today: LocalDate = LocalDate.now()
      val dayMenusList = DayMenuDataStore.between(today.`with`(DayOfWeek.MONDAY), today.`with`(DayOfWeek.SUNDAY))

      val dayMenus = SortedMap(dayMenusList.map(e => e.date.toString("dd.MM.yyyy") -> e): _*)

      if (dayMenus != null) {
        Ok(objectToJSON(dayMenus, queryParams))
      } else {
        Ok("{}")
      }
    }

    case GET -> Root / "menu" / "week" / IntVar(nr) :? queryParams => {
      val weekOfYear: LocalDate = LocalDate.now().`with`(ChronoField.ALIGNED_WEEK_OF_YEAR, nr)
      val dayMenusList = DayMenuDataStore.between(weekOfYear.`with`(DayOfWeek.MONDAY), weekOfYear.`with`(DayOfWeek.SUNDAY))

      val dayMenus = SortedMap(dayMenusList.map(e => e.date.toString("dd.MM.yyyy") -> e): _*)

      if (dayMenus != null) {
        Ok(objectToJSON(dayMenus, queryParams))
      } else {
        Ok("{}")
      }
    }

    case GET -> Root / "menu" / "week" / IntVar(nr) / IntVar(year) :? queryParams => {
      val weekOfYear: LocalDate = LocalDate.now().`with`(ChronoField.YEAR, year).`with`(ChronoField.ALIGNED_WEEK_OF_YEAR, nr)
      val dayMenusList = DayMenuDataStore.between(weekOfYear.`with`(DayOfWeek.MONDAY), weekOfYear.`with`(DayOfWeek.SUNDAY))

      val dayMenus = SortedMap(dayMenusList.map(e => e.date.toString("dd.MM.yyyy") -> e): _*)

      if (dayMenus != null) {
        Ok(objectToJSON(dayMenus, queryParams))
      } else {
        Ok("{}")
      }
    }

    case GET -> Root / "menu" / LocalDateVar(date) :? queryParams => {
      val dayMenu: DayMenu = DayMenuDataStore(date)

      if (dayMenu != null) {
        Ok(objectToJSON(dayMenu, queryParams))
      } else {
        Ok("{}")
      }
    }

  }

  val builder = BlazeBuilder.bindHttp(8080, "localhost")
    .mountService(helloWorldService, "/")
    .mountService(menuAPI, "/api")

}
