package com.example.el_indeciso

import android.os.Bundle
import android.text.InputFilter
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.el_indeciso.databinding.FragmentJoinGameBinding

class JoinGameFragment : BaseFragment() {
    private var _binding: FragmentJoinGameBinding? = null
    private val binding get() = _binding!!
    override fun layoutId() = R.layout.fragment_join_game

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinGameBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.edittextJoin.setFocus()
        binding.edittextJoin.filters += InputFilter.LengthFilter(4)

        binding.prevPageJoin.setOnClickListener {
            goToDirection(
                JoinGameFragmentDirections.actionJoinGameFragmentToPlayMenuFragment(),
                view
            )
        }
        binding.joinButtonJoin.setOnClickListener {
            if (!validRoomCode()) {
                binding.edittextJoin.setText("")
                showMessageToast("Please set a valid room code")
            } else { //Room Code is ok
                goToDirection(
                    JoinGameFragmentDirections.actionJoinGameFragmentToGameView(
                        false,
                        binding.edittextJoin.text.toString()
                    ), view
                )
            }
        }

        binding.edittextJoin.setOnKeyListener { _, keyCode, event ->

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

    /*
     * Returns if the room code meets the requirements
     */
    private fun validRoomCode(): Boolean {
        val actualText = binding.edittextJoin.text.toString()

        return (actualText.length == 4
                && !(actualText.contains(" "))
                && isValidImput(actualText))
    }

    private fun isValidImput(string: String): Boolean {
        for (c in string)
        {
            if (c !in 'A'..'Z' && c !in 'a'..'z' && c !in '0'..'9') {
                return false
            }
        }
        return true
    }
}