package com.example.el_indeciso

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextSwitcher
import android.widget.TextView
import com.example.el_indeciso.DoubleClickListener

class Card (value: CharSequence,
            context: Context,
            layout: LinearLayout,
            maze_top: TextSwitcher) {
    val view = LayoutInflater.from(context).inflate(R.layout.scrollable_card, layout, false)
    val card_text: TextView = view.findViewById(R.id.card_title)

    init {
        card_text.text = value

        view.setOnClickListener(object: DoubleClickListener() {
            override fun onDoubleClick(v: View) {
                val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.drop)
                v.startAnimation(animation)
                maze_top.setText(card_text.text)

                Handler(Looper.getMainLooper()).postDelayed({
                    val parent: ViewGroup = view.parent as ViewGroup
                    parent.removeView(v)
                }, 650)
            }
        })
    }

    @JvmName("getView1")
    fun getView(): View = view
}