package com.example.el_indeciso

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.el_indeciso.databinding.FragmentJoinGameBinding

class JoinGameFragment : BaseFragment() {
    private var _binding: FragmentJoinGameBinding? = null
    private val binding get() = _binding!!
    override fun layoutId() = R.layout.fragment_join_game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinGameBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.edittextJoin.setFocus()

        binding.prevPageJoin.setOnClickListener {
            goToFragment(PlayMenuFragment())
        }
        /*binding.joinButtonJoin.setOnClickListener {
            goToFragment(fragment())
        }*/
        binding.edittextJoin.setOnKeyListener { v, keyCode, event ->

            when {
                //Check if it is the Enter-Key,      Check if the Enter Key was pressed down
                ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN)) -> {
                    binding.joinButtonJoin.performClick()
                    binding.edittextJoin.hideKeyboard()
                    binding.edittextJoin.clearFocus()

                    return@setOnKeyListener true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}