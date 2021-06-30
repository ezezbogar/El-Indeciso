package com.example.firebaseplayground

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