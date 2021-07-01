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

        // Set the content view of the Activity to be the root view of the layout
        var mensajero = MovesMessenger("DesdeElRepo")
        mensajero.addMove(Move("Niqui", 1))
        mensajero.addMove(Move("Eze", 2))

        binding.profileButton.setOnClickListener {
            val intent = Intent(this, ProfileMenu::class.java)
            startActivity(intent)
        }
    }
}