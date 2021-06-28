package com.example.el_indeciso

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ProfileMenu: AppCompatActivity() {

    private var back_index = 0
    private var face_index = 0
    private var outfit_index = 0
    private var head_index = 0

    var profile_pic: String = "0000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_menu)

        val head: ImageView = findViewById(R.id.head)
        val face: ImageView = findViewById(R.id.face)
        val outfit: ImageView = findViewById(R.id.outfit)
        val back: ImageView = findViewById(R.id.back)

        findViewById<Button>(R.id.next_head).setOnClickListener{
            head_index++
            if (head_index >= HEADS.size) head_index = 0
            head.setImageResource(HEADS[head_index])
        }
        findViewById<Button>(R.id.prev_head).setOnClickListener{
            head_index--
            if (head_index < 0) head_index = HEADS.size - 1
            head.setImageResource(HEADS[head_index])
        }
        findViewById<Button>(R.id.next_face).setOnClickListener{
            face_index++
            if (face_index >= FACES.size) face_index = 0
            face.setImageResource(FACES[face_index])
        }
        findViewById<Button>(R.id.prev_face).setOnClickListener{
            face_index--
            if (face_index < 0) face_index = FACES.size - 1
            face.setImageResource(FACES[face_index])
        }
        findViewById<Button>(R.id.next_outfit).setOnClickListener{
            outfit_index++
            if (outfit_index >= OUTFITS.size) outfit_index = 0
            outfit.setImageResource(OUTFITS[outfit_index])
        }
        findViewById<Button>(R.id.prev_outfit).setOnClickListener{
            outfit_index--
            if (outfit_index < 0) outfit_index = OUTFITS.size - 1
            outfit.setImageResource(OUTFITS[outfit_index])
        }
        findViewById<Button>(R.id.next_back).setOnClickListener{
            back_index++
            if (back_index >= BACKGROUNDS.size) back_index = 0
            back.setImageResource(BACKGROUNDS[back_index])
        }
        findViewById<Button>(R.id.prev_back).setOnClickListener{
            back_index--
            if (back_index < 0) back_index = BACKGROUNDS.size - 1
            back.setImageResource(BACKGROUNDS[back_index])
        }
        findViewById<Button>(R.id.save_button).setOnClickListener {
            val back_digit = Integer.toHexString(back_index)
            val head_digit = Integer.toHexString(head_index)
            val face_digit = Integer.toHexString(face_index)
            val outfit_digit = Integer.toHexString(outfit_index)

            profile_pic = "${back_digit}${head_digit}${face_digit}${outfit_digit}"
        }

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