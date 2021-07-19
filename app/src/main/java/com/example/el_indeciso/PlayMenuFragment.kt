package com.example.el_indeciso

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.el_indeciso.databinding.FragmentPlayMenuBinding

class PlayMenuFragment : BaseFragment() {
    private var _binding: FragmentPlayMenuBinding? = null
    private val binding get() = _binding!!
    override fun layoutId() = R.layout.fragment_join_game

    private val args: PlayMenuFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayMenuBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sfxManager = SFX_Manager(requireActivity().applicationContext)
        val soundOn = args.soundOn
        sfxManager.changeSoundStatus(soundOn)

        binding.prevPagePlayMenu.setOnClickListener {
            sfxManager.play(Sound.BUTTON_CLICK)
            goToDirection(
                PlayMenuFragmentDirections.actionPlayMenuFragmentToMainMenuFragment(soundOn),
                view
            )
        }
        binding.createButtonPlayMenu.setOnClickListener {
            sfxManager.play(Sound.BUTTON_CLICK)
            goToDirection(PlayMenuFragmentDirections.actionPlayMenuFragmentToGameView(true, "46AS", soundOn), view)
        }
        binding.joinButtonPlayMenu.setOnClickListener {
            sfxManager.play(Sound.BUTTON_CLICK)
            goToDirection(
                PlayMenuFragmentDirections.actionPlayMenuFragmentToJoinGameFragment(soundOn),
                view
            )
        }
    }
}