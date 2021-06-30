package com.example.el_indeciso

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.concurrent.ConcurrentLinkedQueue

// Se debe garantizar que la partida con este ID ya fue creada y existe.
class MovesMessenger (private val matchID:String) {
    private val movesReference: DatabaseReference = Firebase.database.reference
                            .child("matches").child(matchID).child("moves")
    val moves : ConcurrentLinkedQueue<Move> = ConcurrentLinkedQueue()
    init {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("FIREBASE_INFO", "Se recibio correctamente un elemento")
                if(dataSnapshot != null) {
                    moves.add(dataSnapshot.getValue<Move>())
                    Log.d("FIREBASE_INFO", "Hasta este momento el dataBase tiene: $moves")
                }
            }
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        movesReference.addChildEventListener(childEventListener)
    }
    fun addMove( newMove : Move):Boolean {
        var key = movesReference.push().key
        if (key != null) {
            Log.d("FIREBASE_INFO", "Se agrega un elemetento : $newMove")
            movesReference.child(key).setValue(newMove)
            return true
        }
        return false
    }
    // Si no hay movimientos en la cola devuelve null.
    fun getMove(): Move? {
        return moves.poll();
    }
}