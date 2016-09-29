package com.pichler.wosgibtsheitzumessen.refresher

import com.pichler.wosgibtsheitzumessen.data.DayMenuDataStore
import com.pichler.wosgibtsheitzumessen.refresher.mail.SpecialMenuParser

/**
  * Created by Patrick on 21.09.2016.
  */
object Main extends App {
  DayMenuDataStore.startAndWaitForInitialized()
  Updater.scheduleUpdate()
  SpecialMenuParser.start()
}
