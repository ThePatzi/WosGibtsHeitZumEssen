package com.pichler.wosgibtsheitzumessen.model

import java.time.LocalDate

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.{JsonDeserialize, JsonSerialize}
import com.pichler.wosgibtsheitzumessen.util.json.{LocalDateDeserializer, LocalDateSerializer}

/**
  * Created by Patrick on 15.09.2016.
  */
case class DayMenu(@JsonProperty("date")
                   @JsonSerialize(using = classOf[LocalDateSerializer])
                   @JsonDeserialize(using = classOf[LocalDateDeserializer]) date: LocalDate,
                   @JsonProperty("soup") soup: String,
                   @JsonProperty("menu1") menu1: String,
                   @JsonProperty("menu2") menu2: String)