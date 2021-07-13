package com.example.el_indeciso

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextSwitcher
import android.widget.TextView
import com.example.el_indeciso.DoubleClickListener
import java.lang.Long.parseLong
import java.util.concurrent.BlockingQueue

class UI_Player (val name: CharSequence,
                 var cards: Int,
                 val profile_pic: String,
                 val drop_notifier: BlockingQueue<String>,
                 val context: Context,
                 layout: GridLayout) {

    val view = LayoutInflater.from(context).inflate(R.layout.player, layout, false)

    private val back: ImageView = view.findViewById(R.id.profile_back_drop)
    private val player_cards: TextView = view.findViewById(R.id.player_cards)
    private val player_drop: Animation = AnimationUtils.loadAnimation(context, R.anim.players_drop)

    init{
        view.findViewById<TextView>(R.id.player_name).text = name
        player_cards.text = cards.toString()
        loadProfilePic()
    }

    fun dropCard(card: Int?, maze: TextSwitcher, validMove: Boolean) {
        cards--
        if (validMove) {
            maze.setText(card.toString())
        }
        player_cards.text = cards.toString()
        drop_notifier.put(context.getString(R.string.drop_message, name, card))
        drop_notifier.put(context.getString(R.string.drop_spacing))
        back.startAnimation(player_drop)
    }

    fun updateCards(new_value:Int) {
        cards = new_value
        player_cards.text = cards.toString()
    }

    private fun loadProfilePic(){
        val back_index = parseLong(profile_pic[0].toString(),16).toInt()
        val head_index = parseLong(profile_pic[1].toString(),16).toInt()
        val face_index = parseLong(profile_pic[2].toString(),16).toInt()
        val outfit_index = parseLong(profile_pic[3].toString(),16).toInt()

        view.findViewById<ImageView>(R.id.profile_back).setImageResource(BACKGROUNDS[back_index])
        view.findViewById<ImageView>(R.id.profile_head).setImageResource(HEADS[head_index])
        view.findViewById<ImageView>(R.id.profile_face).setImageResource(FACES[face_index])
        view.findViewById<ImageView>(R.id.profile_outfit).setImageResource(OUTFITS[outfit_index])

    }

    companion object {
        val BACKGROUNDS = listOf(R.drawable.back_0,R.drawable.back_1,R.drawable.back_2,
            R.drawable.back_3,R.drawable.back_4,R.drawable.back_5,
            R.drawable.back_6,R.drawable.back_7,R.drawable.back_8,
            R.drawable.back_9,)
        val FACES = listOf(R.drawable.face_0,R.drawable.face_1,R.drawable.face_2,
            R.drawable.face_3,R.drawable.face_4,R.drawable.face_5,
            R.drawable.face_6,)
        val OUTFITS = listOf(R.drawable.outfit_0,R.drawable.outfit_1,
            R.drawable.outfit_2,R.drawable.outfit_3,)
        val HEADS = listOf(R.drawable.head_0,R.drawable.head_1,R.drawable.head_2,
            R.drawable.head_3,R.drawable.head_4,R.drawable.head_5,
            R.drawable.head_6,)
    }
}