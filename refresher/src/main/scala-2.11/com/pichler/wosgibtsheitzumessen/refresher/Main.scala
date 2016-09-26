package com.pichler.wosgibtsheitzumessen.refresher

import com.pichler.wosgibtsheitzumessen.data.DayMenuDataStore

/**
  * Created by Patrick on 21.09.2016.
  */
object Main extends App {
  DayMenuDataStore.start()
  Updater.scheduleUpdate()
}
