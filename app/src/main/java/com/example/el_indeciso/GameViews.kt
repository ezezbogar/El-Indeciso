package com.example.el_indeciso

import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextSwitcher
import android.widget.TextView

data class GameViews(val maze_text: TextSwitcher,
                     val drop_message: TextSwitcher,
                     val lives: TextView,
                     val round: TextView,
                     val player_hand: LinearLayout,
                     val players_grid: GridLayout) {
}