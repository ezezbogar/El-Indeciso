package com.example.el_indeciso

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.reflect.KFunction1

sealed class PopUp (val context: Context, val sfx_manager: SFX_Manager) {

    private var dialogBuilder = AlertDialog.Builder(context)
    lateinit var alertDialog: AlertDialog
    lateinit var popUpLayout: View
    private var initialized = false

    fun show() {
        // magia de inicializacion
        dialogBuilder.setView(popUpLayout)
        alertDialog = if (!initialized) dialogBuilder.create() else alertDialog
        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()

        initialized = true
    }
}


class WrongDropPopUp (var onDisplay: AtomicBoolean,
                      sfx_manager: SFX_Manager, 
                      context: Context): PopUp (context, sfx_manager) {

    init {
        popUpLayout = LayoutInflater.from(context).inflate(R.layout.drop_pop_up, null)
    }

    // Muestra el PopUp que indica que un jugador tiró mal una carta
    // Recibe el nombre del jugador que tiró dicha carta, la carta que tiró y un map de las cartas
    // que descartó cada jugador.
    // El map es de la forma ("gonza" to ["1","2","5"], "eze" to ["47", "414"], "chuchu" to [])

    fun launch (drop_responsible: String, card_dropped: String, discarded_cards: Map<String, List<String>>) {
        onDisplay.set(true)
        setData(drop_responsible, card_dropped, discarded_cards)
        initializeButton(popUpLayout.findViewById(R.id.close_button))
        sfx_manager.play(Sound.SAD_POP_UP)
        show()
    }

    private fun setData(drop_responsible: String, card_dropped: String, discarded_cards: Map<String, List<String>>) {
        val discardedCardsMessages: LinearLayout = popUpLayout.findViewById(R.id.discarded_cards_messages)

        val subtitle: TextView = popUpLayout.findViewById(R.id.drop_responsible)
        subtitle.text = context.getString(R.string.drop_responsible, drop_responsible, card_dropped)

        for (player: Map.Entry<String, List<String>> in discarded_cards) {
            for (card: String in player.value) {
                val discardedMessageLayout =  LayoutInflater.from(context).inflate(R.layout.drop_pop_up_line,
                    discardedCardsMessages, false)

                val discardedMessage: TextView = discardedMessageLayout.findViewById(R.id.discarded_message)
                discardedMessage.text = context.getString(R.string.discard_message, player.key, card)

                discardedCardsMessages.addView(discardedMessageLayout)
            }
        }
    }
    
    private fun initializeButton(button: Button) {
        button.setOnClickListener {
            sfx_manager.play(Sound.BUTTON_CLICK)
            alertDialog.dismiss()
            onDisplay.set(false)
        }
    }
    
}

class CompletedRoundPopUp (val onDisplay: AtomicBoolean,
                           val handler: Handler,
                           sfx_manager: SFX_Manager,
                           context: Context): PopUp (context, sfx_manager) {
    init {
        popUpLayout= LayoutInflater.from(context).inflate(R.layout.completed_round_pop_up, null)
    }

    // Muestra el PopUp que indica que la ronda fue completada.
    // Recibe la cantidad de vidas restantes, la ronda que se acaba de terminar y la cantidad
    // de rondas totales.

    fun launch (lives:Int, round:Int, total_rounds: Int, match: Match, playersReady: KFunction1<MutableList<MatchPlayer>, Boolean>) {
        match.unready()
        
        val launchPopUp: Runnable = object : Runnable {
            override fun run() {
                if (!onDisplay.get()) {
                    setData(lives, round, total_rounds)
                    initializeButton(popUpLayout.findViewById(R.id.close_button), match, playersReady)
                    sfx_manager.play(Sound.HAPPY_POP_UP)
                    show()
                } else {
                    handler.postDelayed(this, 50)
                }
            }
        }
        handler.post(launchPopUp)
    }

    private fun setData(lives:Int, round:Int, total_rounds: Int) {
        val reward = if (round % 2 == 0) "1 vida" else "-"
        val title: TextView = popUpLayout.findViewById(R.id.completed_round_title)
        val content: TextView = popUpLayout.findViewById(R.id.completed_round_content)

        title.text = context.getString(R.string.round_completed_title, round.toString())
        content.text = context.getString(R.string.round_completed_text, reward,
                            lives.toString(), (total_rounds-round).toString())
    }

    private fun initializeButton(button: Button, match: Match, playersReady: (MutableList<MatchPlayer>) -> Boolean) {
        button.setOnClickListener {
            match.ready()

            val waitForAllReady: Runnable = object : Runnable {
                override fun run () {
                    if (playersReady(match.getPlayers())) {
                        alertDialog.dismiss()
                    } else {
                        handler.postDelayed(this, 100)
                    }
                }
            }
            sfx_manager.play(Sound.BUTTON_CLICK)
            handler.post(waitForAllReady)
        }
    }
}

class StartPopUp (val handler: Handler,
                  val mutex: ReentrantLock,
                  sfx_manager: SFX_Manager,
                  context: Context): PopUp (context, sfx_manager) {

    init {
        popUpLayout = LayoutInflater.from(context).inflate(R.layout.start_pop_up, null)
    }

    fun launch (room_code: String,
                gameStarted: AtomicBoolean,
                match: Match) {
        setData(room_code, match.getPlayers())
        initializeButton(popUpLayout.findViewById(R.id.start_button), gameStarted, match)
        sfx_manager.play(Sound.NORMAL_POP_UP)
        show()
    }

    private fun setData (room_code: String, firebase_players:List<MatchPlayer>) {
        val players_state = mutableMapOf<String, Boolean>()
        val players_state_view = mutableMapOf<String, TextView>()
        val players_state_container: LinearLayout = popUpLayout.findViewById(R.id.players_state)

        val room_text: TextView = popUpLayout.findViewById(R.id.room_code)
        room_text.text = context.getString(R.string.room_message, room_code)

        val view_connections: Runnable = object : Runnable {
            override fun run() {

                mutex.lock()
                for (player in firebase_players) {
                    if (!players_state.contains(player.id)) {
                        val connected_player = LayoutInflater.from(context).inflate(R.layout.players_pop_up_line, players_state_container, false)

                        val player_name: TextView = connected_player.findViewById(R.id.connected_player_name)
                        val player_state: TextView = connected_player.findViewById(R.id.connected_player_state)
                        player_name.text = player.nombre

                        players_state_container.addView(connected_player)
                        players_state.put(player.id, false)
                        players_state_view.put(player.id, player_state)

                    } else if (players_state[player.id] != player.ready) {
                        players_state_view[player.id]?.text = context.getString(R.string.ready)
                        players_state_view[player.id]?.setTextColor((Color.parseColor("#056266")))
                    }
                }
                mutex.unlock()

                val unready = players_state.containsValue(false) || firebase_players.isEmpty()
                if (unready) {
                    handler.postDelayed(this, 100)
                }

            }
        }
        handler.post(view_connections)
    }

    private fun initializeButton(button: Button, gameStarted: AtomicBoolean, match: Match) {
        button.setOnClickListener {
            button.isClickable = false
            sfx_manager.play(Sound.BUTTON_CLICK)
            button.setBackgroundResource(R.drawable.popup_button_pressed)
            button.setText(R.string.wait_button)
            match.ready()

            val waitConnections: Runnable = object : Runnable {
                override fun run() {
                    val waiting = gameStarted.get()
                    if (!waiting) {
                        handler.postDelayed(this, 50)
                    } else {
                        alertDialog.dismiss()
                    }
                }
            }
            handler.post(waitConnections)
        }
    }
}