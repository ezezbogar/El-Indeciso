package com.example.el_indeciso

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import kotlin.concurrent.*

class GameView : AppCompatActivity() {

    private val drop_handler = Handler()
    private lateinit var sfx_manager: SFX_Manager

    private val args: GameViewArgs by navArgs()
    private var isHost: Boolean = true
    private lateinit var roomCode: String

    private var profileName: String = "player"
    private var profilePic: String = "0000"
    private val fileName: String = "profile_info.txt"
    private val delimiter: String = " - "

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        isHost = args.isHost
        roomCode = args.roomCode

        setContentView(R.layout.game_screen)

        val lives: TextView = findViewById(R.id.life_value)
        val round: TextView = findViewById(R.id.round_value)
        val player_hand: LinearLayout = findViewById(R.id.gallery)
        val players_grid: GridLayout = findViewById(R.id.players_grid)
        val drop_message: TextSwitcher = findViewById(R.id.drop_message)
        val maze_text: TextSwitcher = findViewById(R.id.maze_title)
        maze_text.setText("0")

        val game_views = GameViews(maze_text, drop_message, lives, round, player_hand, players_grid)
        sfx_manager = SFX_Manager(this)

        getInitialData()
        val matchMaker = MatchMaker(profileName, profilePic)

        var match: Match = if (isHost) {
            matchMaker.newMatch()
        } else {
            matchMaker.joinMatch(roomCode)
        }

        val game = Game(drop_handler, game_views, sfx_manager, this, match)

        thread { game.run() }
    }

    private fun getInitialData() {
        val fileManager: FileManager = FileManager(this)
        val readData = fileManager.readFile(fileName) as CharSequence

        //If there is information saved, it's loaded
        if (readData.isNotEmpty()) {
            val list = readData.split(delimiter)

            //Update with the file information
            profileName = list[1]
            profilePic = list[0].getDigit()
        }
    }

    /*
     * String: returns a string with all the digits of the original
     */
    private fun String.getDigit(): String {
        return substring(indexOfFirst { it.isDigit() }, indexOfLast { it.isDigit() } + 1)
            .filter { it.isDigit()}
    }

    override fun onDestroy() {
        super.onDestroy()
        sfx_manager.release()
    }
}