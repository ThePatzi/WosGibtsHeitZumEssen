package com.pichler.wosgibtsheitzumessen.util

import java.time.{Duration, LocalDateTime, LocalTime}
import java.util.concurrent.{ScheduledExecutorService, ScheduledFuture, TimeUnit}

/**
  * Created by Patrick on 25.09.2016.
  */
object TimeBasedExecution {

  implicit class TimeBasedExecution(scheduledExecutorService: ScheduledExecutorService) {

    def timeToNextExecution(time: LocalTime): Long = {
      val executionTime = LocalDateTime.now()
        .withHour(time.getHour)
        .withMinute(time.getMinute)

         .plusDays(1)

      val duration: Duration = Duration.between(LocalDateTime.now(), executionTime)

      {
        if (duration.toDays > 0) duration.minusDays(1)
        else duration
      }.getSeconds
    }

    def scheduleAt(localTime: LocalTime, toExecute: () => Unit): ScheduledFuture[_] = {
      scheduleAt(localTime, new Runnable {
        override def run(): Unit = toExecute()
      })
    }

    def scheduleAt(localTime: LocalTime, toExecute: Runnable): ScheduledFuture[_] = {
      val delay = timeToNextExecution(localTime)
      scheduledExecutorService.scheduleAtFixedRate(toExecute, delay, TimeUnit.SECONDS.convert(1, TimeUnit.DAYS), TimeUnit.SECONDS)
    }
  }

}
