package com.pichler.wosgibtsheitzumessen.services

import java.time.LocalDate

import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.pichler.wosgibtsheitzumessen.data.DayMenuDataStore
import com.pichler.wosgibtsheitzumessen.model.DayMenu
import com.pichler.wosgibtsheitzumessen.util.Util.StrToDate
import org.http4s._
import org.http4s.dsl._
import org.http4s.server.blaze._

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

  object PrettyPrintQueryParamMatcher extends QueryParamDecoderMatcher[String]("pretty")

  val menuAPI = HttpService {
    case GET -> Root / "menu" / date :? queryParams => {
      val parsedDate: LocalDate = date.toLocalDate("dd.MM.yyyy")
      val dayMenu: DayMenu = DayMenuDataStore(parsedDate)

      if (dayMenu != null) {
        Ok(getObjectMapper(queryParams.contains("pretty"))
          .writeValueAsString(dayMenu))
      } else {
        Ok("{}")
      }
    }

//    case GET -> Root / "menu" / fromDate / toDate :? queryParams => {
//      val parsedFromDate: LocalDate = fromDate.toLocalDate("dd.MM.yyyy")
//      val parsedToDate: LocalDate = toDate.toLocalDate("dd.MM.yyyy")
//
//      val dayMenus = DayMenuDataStore.all.toStream
//        .filter(!_.date.isBefore(parsedFromDate) && _.date.isBefore(parsedToDate))
//        .map(_.date.toString("dd.MM.yyyy") -> _)
//
//      if (dayMenus != null) {
//        Ok(getObjectMapper(queryParams.contains("pretty"))
//          .writeValueAsString(dayMenus))
//      } else {
//        Ok("{}")
//      }
//    }
  }

  val builder = BlazeBuilder.bindHttp(8080, "localhost")
    .mountService(helloWorldService, "/")
    .mountService(menuAPI, "/api")

}
