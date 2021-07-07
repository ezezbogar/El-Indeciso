package com.example.el_indeciso

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextSwitcher
import android.widget.TextView
import com.example.el_indeciso.DoubleClickListener

class Card (value: CharSequence,
            context: Context,
            layout: LinearLayout,
            maze_top: TextSwitcher) {

    val view = LayoutInflater.from(context).inflate(R.layout.scrollable_card, layout, false)
    private val handler = Handler(Looper.getMainLooper())
    private val card_text: TextView = view.findViewById(R.id.card_title)
    private val drop_sleep: Long = 650
    private var is_clickable = true

    init {
        card_text.text = value

        view.findViewById<ImageView>(R.id.card_image).setOnClickListener(object: DoubleClickListener() {
            override fun onDoubleClick(v: View) {
                if (is_clickable) {
                    val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.drop)
                    view.startAnimation(animation)
                    maze_top.setText(card_text.text)

                    handler.postDelayed({
                        val parent: ViewGroup = view.parent as ViewGroup
                        parent.removeView(view)
                    }, drop_sleep)

                    is_clickable = false
                }
            }
        })
    }
}