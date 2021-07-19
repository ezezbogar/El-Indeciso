package com.example.el_indeciso

import android.os.Bundle
import android.text.InputFilter
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.el_indeciso.databinding.FragmentJoinGameBinding

class JoinGameFragment : BaseFragment() {
    private var _binding: FragmentJoinGameBinding? = null
    private val binding get() = _binding!!
    override fun layoutId() = R.layout.fragment_join_game

    private val args: JoinGameFragmentArgs by navArgs()

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

        val sfxManager = SFX_Manager(requireActivity().applicationContext)
        val soundOn = args.soundOn
        sfxManager.changeSoundStatus(soundOn)

        binding.edittextJoin.setFocus()
        binding.edittextJoin.filters += InputFilter.LengthFilter(4)

        binding.prevPageJoin.setOnClickListener {
            sfxManager.play(Sound.BUTTON_CLICK)
            goToDirection(
                JoinGameFragmentDirections.actionJoinGameFragmentToPlayMenuFragment(soundOn),
                view
            )
        }
        binding.joinButtonJoin.setOnClickListener {
            sfxManager.play(Sound.BUTTON_CLICK)
            if (!validRoomCode()) {
                binding.edittextJoin.setText("")
                showMessageToast("Please set a valid room code")
            } else { //Room Code is ok
                goToDirection(
                    JoinGameFragmentDirections.actionJoinGameFragmentToGameView(
                        false,
                        binding.edittextJoin.text.toString(), soundOn
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
                && isValidInput(actualText))
    }

    private fun isValidInput(string: String): Boolean {
        for (c in string)
        {
            if (c !in 'A'..'Z' && c !in 'a'..'z' && c !in '0'..'9') {
                return false
            }
        }
        return true
    }
}