package com.example.el_indeciso

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.el_indeciso.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // Binding object instance with access to the views in the activity_main.xml layout
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout XML file and return a binding object instance
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lobby = MatchMaker("Eze","dough")
        val match = lobby.newMatch();
        match.addPlayer(MatchPlayer("Niqui","Hola"))
        match.playCard(1)
        match.playCard(3)
        match.playCard(69)


        binding.profileButton.setOnClickListener {
            val intent = Intent(this, ProfileMenu::class.java)
            startActivity(intent)
        }
    }
}