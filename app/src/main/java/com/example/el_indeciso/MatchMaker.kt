package com.example.el_indeciso

import android.util.Log

#### Debe recibir los datos del player
class MatchMaker(var playerName: String = "default", val avatar: String = "def") {
    var me = MatchPlayer(playerName, avatar, "0")

    fun newMatch():Match {
        var key = movesReference.push().key
        if (key != null) {
            Log.d("FIREBASE_INFO", "Se agrega un elemetento : $newMove")
            movesReference.child(key).setValue(newMove)
            return true
        }
}
