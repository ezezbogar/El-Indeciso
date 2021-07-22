package com.example.el_indeciso

import android.content.Context
import android.os.Handler

import android.view.ViewGroup

import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.random.Random

class Game(private var handler: Handler,
           private var gameViews: GameViews,
           private var sfxManager: SFX_Manager,
           private var context: Context,
           private var match: Match) {

    private var stillPlaying: Boolean = true
    private var players = Vector<Player>()
    private var playerCards = mutableListOf<Card>()
    private var currentNumber: Int = 0
    private var lives: Int = 3
    private var maxCardNumber: Int = 100
    private var cardsSequence: Vector<Int> = Vector<Int>()
    private var roundNumber: Int = 1
    private var maxRounds: Int = 12
    private var gameStarted: AtomicBoolean = AtomicBoolean(false)

    private var rnd: Random? = null

    /* - - - - - - - UI - - - - - - - */
    private val messageQueue: BlockingQueue<String> = LinkedBlockingQueue()
    private var fireBasePlayers = mutableListOf<MatchPlayer>()
    private var popUpOnDisplay: AtomicBoolean = AtomicBoolean(false)
    private val mtx = ReentrantLock()

    private val startPopUp = StartPopUp(handler, mtx, sfxManager, context)
    private val completedRoundPopUp = CompletedRoundPopUp(popUpOnDisplay, handler, sfxManager, context)
    private val wrongDropPopUp = WrongDropPopUp(popUpOnDisplay, sfxManager, context)
    private val gameEndedPopUp = GameEndedPopUp(popUpOnDisplay, handler, sfxManager, context)
    /* - - - - - - - - - - - - - - - - */

    fun run(): Boolean {

        thread { messageLoop() }

        handler.post { startPopUp.launch(match.key, gameStarted, match) }

        waitTillPlayersReady()

        rnd = Random(match.key.hashCode())

        maxCardNumber = calculateMaxCardNumber(players.size)
        maxRounds = calculateLevelsAmount(players.size)
        lives = players.size

        // Game Starts
        while (stillPlaying && roundNumber <= maxRounds) {

            /* - - - */
            updateRoundUI()
            updateLivesUI()
            /* - - - */

            createCardsSequence()
            loadPlayersCards()

            for (player in players) {
                if (player.playerId != match.whoAmI()) {
                    handler.post { player.getUI()!!.updateCards(roundNumber) }
                }
            }

            while (!allCardsPlayed() && stillPlaying) {
                val newMove = match.getMove()

                if (newMove == null) {
                    Thread.sleep(500)
                } else {
                    processMove(newMove)
                }

                if (lives <= 0) {
                    stillPlaying = false
                }
            }

            if (roundNumber % 2 == 0) {
                lives++
            }

            val roundNumberCopy = roundNumber
            val livesCopy = lives

            if (roundNumber == maxRounds && stillPlaying) {
                gameEndedPopUp.launch(stillPlaying)
            } else if (!stillPlaying) {
                gameEndedPopUp.launch(stillPlaying)
            } else {
                handler.postDelayed({ completedRoundPopUp.launch(livesCopy, roundNumberCopy, maxRounds, match, this::playersReady ) }, 50)
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
                posCard++
            }
            player.getCards().sort()

            if (player.playerId == match.whoAmI()) {
                for (card in player.getCards()) {
                    /* - - - */
                    addHandCardUI(card)
                    /* - - - */
                }
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
        val allCards: Vector<Int> = Vector<Int>()
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

    private fun calculateLevelsAmount(playersAmount: Int): Int {
        var maxLevel = 0
        when (playersAmount) {
            1, 2, 3 -> maxLevel = 12
            4, 5 -> maxLevel = 10
            6, 7 -> maxLevel = 8
            8, 9 -> maxLevel = 6
        }
        return maxLevel
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
            dropLowerCardsThan(newMove)
            lives--

            /* - - - */
            updateLivesUI()
            /* - - - */
        }
    }

    private fun isValidMove(move: Move): Boolean {
        var isValid = true
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

    private fun dropLowerCardsThan(move: Move) {
        val thrownCard = move.card
        val discardedCards = mutableMapOf<String, List<String>>()
        for (player in players) {
            val cardsCopy = Vector<Int>()
            val discardedCardsList = mutableListOf<String>()
            cardsCopy.addAll(player.getCards())

            for (card in cardsCopy) {
                if (card < thrownCard) {
                    discardedCardsList.add(card.toString())
                    playerUsesCard(card)
                }
            }
            discardedCards[player.name] = discardedCardsList as List<String>
        }

        handler.post { wrongDropPopUp.launch(players.find { player -> player.playerId == move.playerId }!!.name,
                                move.card.toString(),
                                discardedCards) }
    }

    private fun waitTillPlayersReady() {
        var playersReady = false

        while (!playersReady) {

            mtx.lock()
            fireBasePlayers = match.getPlayers()
            mtx.unlock()

            if (playersReady(fireBasePlayers) && fireBasePlayers.size > 0) {
                playersReady = true

                loadPlayers(fireBasePlayers)

            } else {
                Thread.sleep(500)
            }
        }
        gameStarted.set(true)
    }

    private fun loadPlayers(playersFireBase: MutableList<MatchPlayer>) {
        for (player in playersFireBase) {
            if (player.id == match.whoAmI()) {
                players.add(Player(player.id, player.nombre, null))
            } else {
                val playerUi = UI_Player(
                    player.nombre,
                    roundNumber,
                    player.profilePic,
                    messageQueue,
                    context,
                    gameViews.players_grid
                )
                players.add(Player(player.id, player.nombre, playerUi))
                addPlayerUI(playerUi)
            }
        }
    }

    fun playersReady(playersFireBase: MutableList<MatchPlayer>): Boolean {
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
        const val DROP_MESSAGE_LOOP_SLEEP: Long = 50
        const val DROP_MESSAGE_DURATION: Long = 1000
    }

    private fun updateLivesUI() {
        val livesWriter = Runnable { gameViews.lives.text = lives.toString() }
        handler.post(livesWriter)
    }

    private fun updateDeckUI() {
        val deckWriter = Runnable { gameViews.maze_text.setText(currentNumber.toString()) }
        handler.post(deckWriter)
    }

    private fun updateRoundUI() {
        val roundWriter = Runnable { gameViews.round.text = roundNumber.toString() }
        handler.post(roundWriter)
    }

    private fun playerDropUI(UI: UI_Player?, card: Int?, validMove: Boolean) {
        val playerDrop = Runnable { UI!!.dropCard(card, gameViews.maze_text, validMove) }
        handler.post(playerDrop)
    }

    private fun addPlayerUI(UI: UI_Player) {
        val playerAdder = Runnable { gameViews.players_grid.addView(UI.view) }
        handler.post(playerAdder)
    }

    private fun addHandCardUI(card: Int) {
        val cardUI = Card(card.toString(), context, gameViews.player_hand, gameViews.maze_text, sfxManager, match)
        playerCards.add(cardUI)

        val handCardAdder = Runnable { gameViews.player_hand.addView(cardUI.view) }
        handler.post(handCardAdder)
    }
}
