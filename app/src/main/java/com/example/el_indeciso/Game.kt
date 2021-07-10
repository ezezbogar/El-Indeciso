package com.example.el_indeciso

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.util.Log

import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random
import kotlin.concurrent.*

import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.graphics.drawable.ColorDrawable
import android.graphics.Color

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
    private var gameStarted: AtomicBoolean = AtomicBoolean(false)

    private var rnd: Random? = null

    /* - - - - - - - UI - - - - - - - */
    private val messageQueue: BlockingQueue<String> = LinkedBlockingQueue()
    lateinit var dialogBuilderCommon: AlertDialog.Builder
    lateinit var alertDialogCommon: AlertDialog

    lateinit var dialogBuilderWrongMove: AlertDialog.Builder
    lateinit var alertDialogWrongMove: AlertDialog
    /* - - - - - - - - - - - - - - - - */

    fun run(): Boolean {

        thread { messageLoop() }

        /*thread {
            Thread.sleep(10000)
            match.ready()
        }*/


        handler.post { showStartPopUp() }

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

            if (roundNumber % 2 == 0) {
                lives++
            }

            var roundNumberCopy = roundNumber
            var livesCopy = lives
            handler.post { showCompletedRoundPopUp(livesCopy, roundNumberCopy, maxRounds) }

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
        var discardedCards = mutableMapOf<String, List<String>>()
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

        handler.post { showWrongDropPopUp(players.find { player -> player.playerId == move.playerId }!!.name,
                        move.card.toString(),
                        discardedCards) }
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

    /* Muestra el PopUp que indica que la ronda fue completada.
       Recibe la cantidad de vidas restantes, la ronda que se acaba de terminar y la cantidad
       de rondas totales. */
    private fun showCompletedRoundPopUp(lives:Int, round:Int, total_rounds: Int) {
        dialogBuilderCommon = AlertDialog.Builder(context)
        val pop_up_layout: View =  LayoutInflater.from(context).inflate(R.layout.completed_round_pop_up, null)

        setCompletedRoundPopUpData(pop_up_layout, lives, round, total_rounds)
        initializeCloseButton(pop_up_layout.findViewById(R.id.close_button))
        showPopUp(pop_up_layout)
    }


    // Muestra el PopUp que indica que un jugador tiró mal una carta
    // Recibe el nombre del jugador que tiró dicha carta, la carta que tiró y un map de las cartas
    // que descartó cada jugador.
    // El map es de la forma ("gonza" to ["1","2","5"], "eze" to ["69", "420"], "chuchu" to [])
    private fun showWrongDropPopUp(drop_responsible: String, card_dropped: String, discarded_cards: Map<String, List<String>>) {
        dialogBuilderWrongMove = AlertDialog.Builder(context)
        val pop_up_layout: View = LayoutInflater.from(context).inflate(R.layout.drop_pop_up, null)

        setWrongDropPopUpData(pop_up_layout, drop_responsible, card_dropped, discarded_cards)
        initializeCloseButtonWrongMove(pop_up_layout.findViewById(R.id.close_button))
        showPopUpWrongMove(pop_up_layout)
    }


    // Muestra el PopUp que aparece al inicio de la partida.
    // El jugador toca "COMENZAR" y queda bloqueando hasta que todos hayan hecho lo mismo.
    // Los parametros manejalos vos, le estoy pasando un handler pero capaz que vos tenes un handler como
    // atributo y podes borrar este parametro.
    // Además no tengo ni la más remota idea de cómo chequeas que estén todos ready, lo dejo a tu criterio
    // mi rey. De ultima sabes donde encontrarme ;)
    private fun showStartPopUp() {
        dialogBuilderCommon = AlertDialog.Builder(context)
        val pop_up_layout: View = LayoutInflater.from(context).inflate(R.layout.start_pop_up, null)

        // Lo mismo con este metodo, los parametros manejalos vos
        initializeStartButton(pop_up_layout.findViewById(R.id.start_button), handler)
        showPopUp(pop_up_layout)
    }

    // Setea la información que muestra el PopUp de ronda completada.
    private fun setCompletedRoundPopUpData(pop_up_layout: View, lives:Int, round:Int, total_rounds: Int) {
        val reward = if (round % 2 == 0) "1 vida" else "-"
        val title: TextView = pop_up_layout.findViewById(R.id.completed_round_title)
        val content: TextView = pop_up_layout.findViewById(R.id.completed_round_content)

        title.text = context.getString(R.string.round_completed_title, round.toString())
        content.text = context.getString(R.string.round_completed_text, reward,
            lives.toString(), (total_rounds-round).toString())
    }

    // Setea la información que muestra el PopUp de cuando alguien tira el numero equivocado.
    // El subtitulo dice quién pifió, qué carta tiró y que perdieron una vida.
    // Después muestra una lista scrolleable con las cartas de cada jugador que fueron descartadas.
    private fun setWrongDropPopUpData(pop_up_layout: View,
                                      drop_responsible: String,
                                      card_dropped: String,
                                      discarded_cards: Map<String, List<String>>) {

        val discarded_cards_messages: LinearLayout = pop_up_layout.findViewById(R.id.discarded_cards_messages)

        val subtitle: TextView = pop_up_layout.findViewById(R.id.drop_responsible)
        subtitle.text = context.getString(R.string.drop_responsible, drop_responsible, card_dropped)

        for (player: Map.Entry<String, List<String>> in discarded_cards) {
            for (card: String in player.value) {
                val discarded_message_layout =  LayoutInflater.from(context).inflate(R.layout.pop_up_line, discarded_cards_messages, false)

                val discarded_message: TextView = discarded_message_layout.findViewById(R.id.discarded_message)
                discarded_message.text = context.getString(R.string.discard_message, player.key, card)

                discarded_cards_messages.addView(discarded_message_layout)
            }
        }
    }

    private fun showPopUp(pop_up_layout: View) {
        // Magia de inicializacion
        dialogBuilderCommon.setView(pop_up_layout);
        alertDialogCommon = dialogBuilderCommon.create()
        alertDialogCommon.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialogCommon.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialogCommon.setCanceledOnTouchOutside(false)
        alertDialogCommon.show()
    }

    private fun showPopUpWrongMove(pop_up_layout: View) {
        // Magia de inicializacion
        dialogBuilderWrongMove.setView(pop_up_layout);
        alertDialogWrongMove = dialogBuilderWrongMove.create()
        alertDialogWrongMove.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialogWrongMove.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialogWrongMove.setCanceledOnTouchOutside(false)
        alertDialogWrongMove.show()
    }

    private fun initializeCloseButton(button: Button) {
        button.setOnClickListener { alertDialogCommon.dismiss() }
    }

    private fun initializeCloseButtonWrongMove(button: Button) {
        button.setOnClickListener { alertDialogWrongMove.dismiss() }
    }

    private fun initializeStartButton(button: Button, handler: Handler) {
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                var waiting = true
                button.isClickable = false
                button.setBackgroundResource(R.drawable.popup_button_pressed)
                button.setText(R.string.wait_button)
                match.ready()

                val waitConnections: Runnable = object : Runnable {
                    override fun run() {
                        waiting = gameStarted.get()
                        if (waiting) {
                            handler.postDelayed(this, 50)
                        } else {
                            alertDialogCommon.dismiss()
                        }
                    }
                }
                handler.post(waitConnections)
            }
        })
    }
}
