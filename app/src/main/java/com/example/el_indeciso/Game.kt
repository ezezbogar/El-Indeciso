package com.example.el_indeciso

import com.example.el_indeciso.Move
import com.example.el_indeciso.Player

class Game (val id: Int) {
    lateinit var players : MutableList<Player>
    lateinit var movimientos : MutableList<Move>
    val seed = Math.random()
    fun addPlayer (jugador : Player) {
        players.add(Player());
    }
    fun addPlay (play : Move) {
        movimientos.add(play)
    }
}