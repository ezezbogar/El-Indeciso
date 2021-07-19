package com.example.el_indeciso

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MatchMaker(var playerName: String = "default", val avatar: String = "def") {
    private val matchesReference: DatabaseReference = Firebase.database.reference
        .child("matches")

    fun newMatch():Match {
       val key = generateRandomKey()
        matchesReference.child(key).removeValue()
        val match = Match(key)
        match.addPlayer(MatchPlayer(playerName,avatar))
        return match
    }

    fun joinMatch(key : String):Match {
        val match = Match (key)
        match.addPlayer( MatchPlayer(playerName, avatar) )
        return  match
    }

    private fun generateRandomKey():String {
        val allowedChars = ('A'..'Z') + ('0'..'9') //+ ('a'..'z')
        return (1..4)
            .map { allowedChars.random() }
            .joinToString("")
    }

}
