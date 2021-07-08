package com.example.el_indeciso

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class Match(val key: String) {

    lateinit var me : MatchPlayer
    var messenger = MovesMessenger(key)
    var playerList = mutableListOf<MatchPlayer>()
    private val playersReference = Firebase.database.reference
        .child("matches").child(key).child("players")

    init {
        playersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val auxList = mutableListOf<MatchPlayer>()
                for (postSnapshot in dataSnapshot.children) {
                    if (postSnapshot.getValue<MatchPlayer>() != null) {
                        auxList.add(postSnapshot.getValue<MatchPlayer>()!!)
                    }
                }
                playerList = auxList.toMutableList()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun addPlayer(player : MatchPlayer) {
        me = player
        val auxKey = playersReference.push().key
        if( auxKey != null) {
            me.id = auxKey
            playersReference.child(auxKey).setValue(me)
        }
    }
    fun playCard(cardNumber : Int) {
        messenger.addMove( Move(me.id, cardNumber) )
    }
    fun getMove () : Move? {
        return messenger.getMove()
    }
    fun ready () {
        playersReference.child(me.id).child("ready").setValue(true)
        me.ready=true
    }
    fun unready () {
        playersReference.child(me.id).child("ready").setValue(false)
        me.ready=false
    }
    fun getPlayers (): MutableList<MatchPlayer> {
        return playerList
    }
    fun whoAmI():String {
        return me.id
    }
    fun getID() : String{
        return key
    }
}