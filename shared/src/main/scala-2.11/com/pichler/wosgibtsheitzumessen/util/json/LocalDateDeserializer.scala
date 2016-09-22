package com.pichler.wosgibtsheitzumessen.util.json

import java.time.LocalDate

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.pichler.wosgibtsheitzumessen.util.Util.StrToDate

/**
  * Created by Patrick on 19.09.2016.
  */
class LocalDateDeserializer extends StdDeserializer[LocalDate](classOf[LocalDate]) {
  override def deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDate = {
    p.readValueAs(classOf[String]).toLocalDate("dd.MM.yyyy");
  }
}
