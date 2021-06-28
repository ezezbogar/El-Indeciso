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
        val drop_message: TextView = findViewById(R.id.drop_message)
        val maze_text: TextSwitcher = findViewById(R.id.maze_title)
        maze_text.setText("0")





        // animación de que otro tiró una carta
        val player_dropped: Animation = AnimationUtils.loadAnimation(this, R.anim.players_drop)

        // con esta cola bloqueante en teoria los mensajes aparecen uno atras del otro, sin pisarse
        // (terminar de testear)
        val drop_message_loop: Runnable = object : Runnable {
            override fun run() {
                while (message_queue.isNotEmpty()) {
                    drop_message.text = message_queue.take()
                    drop_message.startAnimation(player_dropped)
                }
                drop_handler.postDelayed(this, DROP_MESSAGE_LOOP_SLEEP)
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
    }

    companion object {
        val DROP_MESSAGE_LOOP_SLEEP: Long = 50
    }
}
