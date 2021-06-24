package com.example.el_indeciso

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextSwitcher
import androidx.appcompat.app.AppCompatActivity
import com.example.el_indeciso.Card

class GameView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout: LinearLayout = findViewById(R.id.gallery)
        val maze_text: TextSwitcher = findViewById(R.id.maze_title)
        maze_text.setText("0")

        for (i in 1..10) {
            val card = Card(i.toString(), this, layout, maze_text)
            layout.addView(card.getView())
        }
    }
}
