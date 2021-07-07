package com.example.el_indeciso

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.el_indeciso.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // Binding object instance with access to the views in the activity_main.xml layout
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goToFragmentFromActivity(MainMenuFragment())

        val lobby = MatchMaker("Eze","dough")
        val match = lobby.newMatch()
        match.addPlayer(MatchPlayer("Niqui","Hola"))
        match.playCard(1)
        match.playCard(3)
        match.playCard(69)
    }

    private fun goToFragmentFromActivity(fragmentToGo: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragmentToGo).commit()
    }
}