package com.example.el_indeciso

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.el_indeciso.databinding.ActivityMainBinding
import com.example.firebaseplayground.Move
import com.example.firebaseplayground.MovesMessenger

class MainActivity : AppCompatActivity() {
    // Binding object instance with access to the views in the activity_main.xml layout
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Inflate the layout XML file and return a binding object instance
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Set the content view of the Activity to be the root view of the layout
        setContentView(binding.root)
        setContentView(R.layout.activity_main)
        var mensajero = MovesMessenger("DesdeElRepo")
        mensajero.addMove(Move(1,1))
        mensajero.addMove(Move(1,2))
    }
}
