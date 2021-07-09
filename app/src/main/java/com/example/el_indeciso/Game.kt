package com.example.el_indeciso

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.random.Random
import kotlin.concurrent.*

class Game(private var handler: Handler,
           private var gameViews: GameViews,
           private var context: Context,
           private var match: Match) {

    private var stillPlaying: Boolean = true
    private var players = Vector<Player>()
    private var playerCards = mutableListOf<Card>()
    private var currentNumber: Int = 0
    private var lives: Int = 3
    private var maxCardNumber: Int = 0
    private var cardsSequence: Vector<Int> = Vector<Int>()
    private var roundNumber: Int = 3
    private val maxRounds: Int = 12

    private var rnd: Random? = null

    /* - - - - - - - UI - - - - - - - */
    private val messageQueue: BlockingQueue<String> = LinkedBlockingQueue()
    lateinit var dialogBuilder: AlertDialog.Builder
    lateinit var alertDialog: AlertDialog
    /* - - - - - - - - - - - - - - - - */

    fun run(): Boolean {

        thread { messageLoop() }

        thread {
            Thread.sleep(10000)
            match.ready()
        }

        waitTillPlayersReady()

        rnd = Random(123) //Leer de Firebase

        maxCardNumber = calculateMaxCardNumber(players.size)

        // Game Starts
        while (stillPlaying && roundNumber <= maxRounds) {

            /* - - - */
            updateRoundUI()
            updateLivesUI()
            /* - - - */

            createCardsSequence()
            loadPlayersCards()

            for (player in players) {
                for (card in player.getCards()) {
                    Log.v("Cartas", card.toString()) // Borrar
                }
            }

            while (!allCardsPlayed() && stillPlaying) {

                val newMove = match.getMove()

                if (newMove == null) {
                    Thread.sleep(500)
                } else {
                    processMove(newMove)
                }

                if (lives < 0) {
                    stillPlaying = false
                }
            }

            currentNumber = 0
            roundNumber++
        }

        return stillPlaying
    }

    private fun loadPlayersCards() {
        var posCard = 0
        for (player in players) {
            for (i in 0 until roundNumber) {
                player.addCard(cardsSequence[posCard])

                if (player.playerId == match.whoAmI()) {

                    /* - - - */
                    addHandCardUI(cardsSequence[posCard])
                    /* - - - */
                }

                posCard++
            }
        }
    }

    private fun allCardsPlayed(): Boolean {
        var allCardsPlayed = true
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
            val num: Int = rnd!!.nextInt(0, maxCardNumber - i + 1)
            cardsSequence.add(allCards[num])
            allCards.removeElementAt(num)
        }
    }

    private fun calculateMaxCardNumber(playersAmount: Int): Int {
        var maxNumber = 0
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
        playerUsesCard(newMove.card)
        currentNumber = newMove.card

        /* - - - */
        updateDeckUI()
        /* - - - */

        if (newMove.playerId == match.whoAmI()) {
            playerCards = playerCards.filterNot{ it.value.toInt() == newMove.card } as MutableList<Card>
        }

        if (!isValidMove(newMove)) {

            for (card in playerCards) {
                if (card.value.toInt() < newMove.card) {
                    val parent: ViewGroup = card.view.parent as ViewGroup
                    handler.post{ parent.removeView(card.view) }
                }
            }

            playerCards = playerCards.filterNot{ it.value.toInt() < newMove.card } as MutableList<Card>
            dropLowerCardsThan(newMove.card)
            lives--

            /* - - - */
            updateLivesUI()
            /* - - - */
        }
    }

    private fun isValidMove(move: Move): Boolean {
        var isValid: Boolean = true
        for (player in players) {
            if (player.hasLowerCardThan(move.card)) {
                isValid = false
            }
        }
        return isValid
    }

    private fun playerUsesCard(card: Int) {
        //Cambiarlo checkeando el id
        for (player in players) {
            if (player.hasCard(card)) {
                player.useCard(card)

                if (player.playerId != match.whoAmI()) {

                    if (currentNumber < card) {
                        playerDropUI(player.getUI(), card, true)
                    } else {
                        playerDropUI(player.getUI(), card, false)
                    }
                }
            }
        }
    }

    private fun dropLowerCardsThan(thrownCard: Int) {
        for (player in players) {
            val cardsCopy = Vector<Int>()
            cardsCopy.addAll(player.getCards())
            for (card in cardsCopy) {

                if (card < thrownCard) {
                    playerUsesCard(card)
                }
            }
        }
    }

    private fun waitTillPlayersReady() {
        var playersReady = false

        while (!playersReady) {

            val playersFireBase = match.getPlayers()
            Log.d("PLAYERS", "${playersFireBase}") // Borrar
            if (playersReady(playersFireBase) && playersFireBase.size > 0) {
                playersReady = true

                loadPlayers(playersFireBase)

            } else {
                Thread.sleep(1000)
            }
        }
    }

    private fun loadPlayers(playersFireBase: MutableList<MatchPlayer>) {
        for (player in playersFireBase) {
            if (player.id == match.whoAmI()) {
                players.add(Player(player.id, null))
            } else {
                val playerUi = UI_Player(
                    player.nombre,
                    roundNumber,
                    player.profilePic,
                    messageQueue,
                    context,
                    gameViews.players_grid
                )
                players.add(Player(player.id, playerUi))
                addPlayerUI(playerUi)
            }
        }
    }

    private fun playersReady(playersFireBase: MutableList<MatchPlayer>): Boolean {
        var allReady = true
        for (player in playersFireBase) {
            if (!player.ready) {
                allReady = false
            }
        }
        return allReady
    }

    private fun messageLoop() {
        val dropMessageLoop: Runnable = object : Runnable {
            override fun run() {
                var sleep = DROP_MESSAGE_DURATION
                if (messageQueue.isNotEmpty()) {
                    val message = messageQueue.take()
                    gameViews.drop_message.setText(message)
                    if (message == "") sleep = DROP_MESSAGE_DURATION / 2
                }
                handler.postDelayed(this, sleep)
            }
        }
        handler.postDelayed(dropMessageLoop, DROP_MESSAGE_LOOP_SLEEP)

    }

    companion object {
        val DROP_MESSAGE_LOOP_SLEEP: Long = 50
        val DROP_MESSAGE_DURATION: Long = 1000
    }

    private fun updateLivesUI() {
        val livesWriter: Runnable = object : Runnable {
            override fun run() {
                gameViews.lives.text = lives.toString()
            }
        }
        handler.post(livesWriter)
    }

    private fun updateDeckUI() {
        val deckWriter: Runnable = object : Runnable {
            override fun run() {
                gameViews.maze_text.setText(currentNumber.toString())
            }
        }
        handler.post(deckWriter)
    }

    private fun updateRoundUI() {
        val roundWriter: Runnable = object : Runnable {
            override fun run() {
                gameViews.round.text = roundNumber.toString()
            }
        }
        handler.post(roundWriter)
    }

    private fun playerDropUI(UI: UI_Player?, card: Int?, validMove: Boolean) {
        val playerDrop: Runnable = object : Runnable {
            override fun run() {
                UI!!.dropCard(card, gameViews.maze_text, validMove)
            }
        }
        handler.post(playerDrop)
    }

    private fun addPlayerUI(UI: UI_Player) {
        val playerAdder: Runnable = object : Runnable {
            override fun run() {
                gameViews.players_grid.addView(UI.view)
            }
        }
        handler.post(playerAdder)
    }

    private fun addHandCardUI(card: Int) {
        val cardUI = Card(card.toString(), context, gameViews.player_hand, gameViews.maze_text, match)
        playerCards.add(cardUI)

        val handCardAdder: Runnable = object : Runnable {
            override fun run() {
                gameViews.player_hand.addView(cardUI.view)
            }
        }
        handler.post(handCardAdder)
    }
}