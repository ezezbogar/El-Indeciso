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
import kotlin.concurrent.*

class GameView : AppCompatActivity() {

    private val drop_handler = Handler()
    private val message_queue: BlockingQueue<String> = LinkedBlockingQueue() // Borrar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.game_screen)

        val lives: TextView = findViewById(R.id.life_value)
        val round: TextView = findViewById(R.id.round_value)
        val player_hand: LinearLayout = findViewById(R.id.gallery)
        val players_grid: GridLayout = findViewById(R.id.players_grid)
        val drop_message: TextSwitcher = findViewById(R.id.drop_message)
        val maze_text: TextSwitcher = findViewById(R.id.maze_title)
        maze_text.setText("0")

        val game_views = GameViews(maze_text, drop_message, lives, round, player_hand, players_grid)

        var matchMaker = MatchMaker("Messi", "1010")

        //var match = matchMaker.newMatch()
        var match = matchMaker.joinMatch("46AS")

        var game: Game = Game(drop_handler, game_views, this, match)

        thread { game.run() }
    }
}