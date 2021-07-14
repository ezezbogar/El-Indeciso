package com.example.el_indeciso

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.el_indeciso.databinding.FragmentPlayMenuBinding

class PlayMenuFragment : BaseFragment() {
    private var _binding: FragmentPlayMenuBinding? = null
    private val binding get() = _binding!!
    override fun layoutId() = R.layout.fragment_join_game

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayMenuBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.prevPagePlayMenu.setOnClickListener {
            goToFragment(MainMenuFragment())
        }
        binding.createButtonPlayMenu.setOnClickListener {
            startGameIntent(true, "46AS")
        }
        binding.joinButtonPlayMenu.setOnClickListener {
            goToFragment(JoinGameFragment())
        }
    }
}