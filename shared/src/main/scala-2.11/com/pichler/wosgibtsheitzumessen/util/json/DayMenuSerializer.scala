package com.pichler.wosgibtsheitzumessen.util.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.pichler.wosgibtsheitzumessen.model.{DayMenu, SpecialMenu}
import com.pichler.wosgibtsheitzumessen.util.Util.DateToStr

class DayMenuSerializer extends StdSerializer[DayMenu](classOf[DayMenu]) {
  override def serialize(value: DayMenu, gen: JsonGenerator, provider: SerializerProvider): Unit = {
    gen.writeStartObject()

    gen.writeStringField("date", value.date.toString("dd.MM.yyyy"))
    gen.writeStringField("soup", value.actualSoup)
    gen.writeStringField("menu1", value.menu1)
    gen.writeStringField("menu2", value.menu2)

    if (value.specialMenu != null && value.specialMenu.menu != null) {
      gen.writeStringField("menu3", value.specialMenu.menu)
    }

    gen.writeEndObject()
  }
}