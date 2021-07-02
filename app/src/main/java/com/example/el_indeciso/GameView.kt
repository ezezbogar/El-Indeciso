package com.example.el_indeciso

import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.el_indeciso.Card
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class GameView : AppCompatActivity() {

    private val drop_handler = Handler()
    private val message_queue: BlockingQueue<String> = LinkedBlockingQueue()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val player_hand: LinearLayout = findViewById(R.id.gallery)
        val players_grid: GridLayout = findViewById(R.id.players_grid)
        val drop_message: TextSwitcher = findViewById(R.id.drop_message)
        val maze_text: TextSwitcher = findViewById(R.id.maze_title)
        maze_text.setText("0")






        // con esta cola bloqueante los mensajes aparecen uno atras del otro, sin pisarse
        val drop_message_loop: Runnable = object : Runnable {
            override fun run() {
                var sleep = DROP_MESSAGE_DURATION
                if (message_queue.isNotEmpty()) {
                    val message = message_queue.take()
                    drop_message.setText(message)
                    if (message == "") sleep = DROP_MESSAGE_DURATION/2
                }
                drop_handler.postDelayed(this, sleep)
            }
        }
        drop_handler.postDelayed(drop_message_loop, DROP_MESSAGE_LOOP_SLEEP)





        // agrega a la mano del jugador cartas del 1 al 10
        for (i in 1..10) {
            val card = Card(i.toString(), this, player_hand, maze_text)
            player_hand.addView(card.view)
        }




        // agrega 3 jugadores al grid
        val players = mutableListOf<UI_Player>()
        for (i in 1..3) {
            val player_name = "Player $i"
            val player = UI_Player(player_name, i, "0101", message_queue,this, players_grid)
            players.add(player)
            players_grid.addView(player.view)
        }

        // pruebita para ver que funca jiji, borrar
        val player_dropped: Runnable = object : Runnable {
            override fun run() {
                players[0].dropCard(5, maze_text)
            }
        }
        drop_handler.postDelayed(player_dropped, 5000)
    }

    // Actualiza la view de la cantidad de vidas del jugador
    private fun updateLives(lives: Int) {
        findViewById<TextView>(R.id.life_value).text = lives.toString()
    }

    // Actualiza la view de la ronda en la que se encuentra el jugador
    private fun updateRound(current: Int, total_rounds: Int) {
        findViewById<TextView>(R.id.round_value).text = getString(R.string.round_counter, current, total_rounds)
    }

    companion object {
        val DROP_MESSAGE_LOOP_SLEEP: Long = 50
        val DROP_MESSAGE_DURATION: Long = 1000
    }
}
