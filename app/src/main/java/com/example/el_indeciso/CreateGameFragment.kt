package com.example.el_indeciso

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.el_indeciso.databinding.FragmentCreateGameBinding


class CreateGameFragment : BaseFragment() {
    private var _binding: FragmentCreateGameBinding? = null
    private val binding get() = _binding!!
    override fun layoutId() = R.layout.fragment_create_game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGameBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.prevPageCreateGame.setOnClickListener {
            goToFragment(PlayMenuFragment())
        }

        // Setting of TextView ScrollBars
        binding.textviewPlayersWaitingCreateGame.movementMethod = ScrollingMovementMethod()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}