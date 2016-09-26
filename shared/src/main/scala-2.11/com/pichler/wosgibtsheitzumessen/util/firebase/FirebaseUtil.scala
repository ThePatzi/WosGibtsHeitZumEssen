package com.pichler.wosgibtsheitzumessen.util.firebase

import com.google.firebase.database.{DataSnapshot, DatabaseError, ValueEventListener}

/**
  * Created by Patrick on 21.09.2016.
  */
object FirebaseUtil {

  implicit def toValueEventListenerUpdate(handler: DataSnapshot => Unit): ValueEventListener = new ValueEventListener {
    override def onDataChange(dataSnapshot: DataSnapshot): Unit = handler(dataSnapshot)

    override def onCancelled(databaseError: DatabaseError): Unit = {}
  }

}
