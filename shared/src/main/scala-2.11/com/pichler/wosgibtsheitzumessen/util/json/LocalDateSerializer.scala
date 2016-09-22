package com.pichler.wosgibtsheitzumessen.util.json

import java.time.LocalDate

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.pichler.wosgibtsheitzumessen.util.Util.DateToStr

/**
  * Created by Patrick on 19.09.2016.
  */
class LocalDateSerializer extends StdSerializer[LocalDate](classOf[LocalDate]) {
  override def serialize(value: LocalDate, gen: JsonGenerator, provider: SerializerProvider): Unit = {
    gen.writeString(value.toString("dd.MM.yyyy"))
  }
}
