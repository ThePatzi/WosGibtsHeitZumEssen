package com.pichler.wosgibtsheitzumessen.util.firebase

import com.google.firebase.database.{ChildEventListener, DataSnapshot, DatabaseError}

/**
  * Created by Patrick on 26.09.2016.
  */
class ChildEventAdapter(childRemoved: DataSnapshot => Unit = { _ => {} },
                        childMoved: (DataSnapshot, String) => Unit = { (_, _) => {} },
                        childChanged: (DataSnapshot, String) => Unit = { (_, _) => {} },
                        cancelled: DatabaseError => Unit = { _ => {} },
                        childAdded: (DataSnapshot, String) => Unit = { (_, _) => {} }) extends ChildEventListener {
  override def onChildRemoved(dataSnapshot: DataSnapshot): Unit = {

  }

  override def onChildMoved(dataSnapshot: DataSnapshot, s: String): Unit = {

  }

  override def onChildChanged(dataSnapshot: DataSnapshot, s: String): Unit = {

  }

  override def onCancelled(databaseError: DatabaseError): Unit = {

  }

  override def onChildAdded(dataSnapshot: DataSnapshot, s: String): Unit = {

  }
}
