package com.example.el_indeciso

import java.util.*
import kotlin.random.Random

class Game() {
    private var isRunning: Boolean = true
    private var players = Vector<Player>()
    private var currentNumber: Int = 0
    private var lives: Int = 3
    private var maxCardNumber: Int = 0
    private var cardsSequence: Vector<Int> = Vector<Int>()
    private var roundNumber: Int = 1
    private val maxRounds: Int = 12

    var rnd: Random? = null

    //private var sendQueue: Queue<Move> = LinkedList<Move>()
    //private var receiveQueue: BlockingQueue<Move> = LinkedBlockingQueue<Move>()

    fun loadAllPlayers() {
        // Load all players
    }

    fun addPlayer(newPlayer: Player) {
        players.add(newPlayer)
    }

    fun run() {

        rnd = Random(123) //Leer de Firebase

        maxCardNumber = calculateMaxCardNumber(players.size)

        // Comienza el juego
        while (isRunning && roundNumber <= maxRounds) {

            createCardsSequence()
            loadPlayersCards()

            while (!allCardsPlayed() && isRunning) {

                //var newMove: Move = receiveQueue.take()
                //processMove(newMove)
            }

            roundNumber++
        }
    }



    fun showAll() {
        for (player in players) {
        }
    }

    private fun loadPlayersCards() {
        var posCard: Int = 0
        for (player in players) {
            for (i in 0 until roundNumber) {
                player.addCard(cardsSequence[posCard])
                posCard++
            }
        }
    }

    private fun allCardsPlayed(): Boolean {
        var allCardsPlayed: Boolean = true
        for (player in players) {
            if (player.getCardsAmount() > 0) {
                allCardsPlayed = false
            }
        }
        return allCardsPlayed
    }

    private fun createCardsSequence() {
        var allCards: Vector<Int> = Vector<Int>()
        cardsSequence.removeAllElements()

        for (i in 1..maxCardNumber) {
            allCards.add(i)
        }
        for (i in 1..maxCardNumber) {
            var num: Int = rnd!!.nextInt(0, maxCardNumber - i + 1)
            cardsSequence.add(allCards[num])
            allCards.removeElementAt(num)
        }
    }

    private fun calculateMaxCardNumber(playersAmount: Int): Int {
        var maxNumber: Int = 0
        when (playersAmount) {
            1, 2, 3, 4 -> maxNumber = 100
            5 -> maxNumber = 125
            6 -> maxNumber = 150
            7 -> maxNumber = 175
            8 -> maxNumber = 200
            9 -> maxNumber = 225
        }
        return maxNumber
    }

    private fun processMove(newMove: Move) {
        if (newMove.card > currentNumber) {
            currentNumber = newMove.card
        } else {
            lives--
            // Perder vidas
        }
    }
}