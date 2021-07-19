package com.example.el_indeciso

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.navArgs
import com.example.el_indeciso.databinding.FragmentMainMenuBinding

class MainMenuFragment : BaseFragment() {
    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!
    override fun layoutId() = R.layout.fragment_main_menu

    private val args: MainMenuFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sfxManager = SFX_Manager(requireActivity().applicationContext)
        val soundOn = args.soundOn
        sfxManager.changeSoundStatus(soundOn)

        binding.profileButton.setOnClickListener {
            sfxManager.play(Sound.BUTTON_CLICK)

            goToDirection(
                MainMenuFragmentDirections.actionMainMenuFragmentToProfileMenuFragment(soundOn),
                view
            )
        }
        binding.playButton.setOnClickListener {
            sfxManager.play(Sound.BUTTON_CLICK)

            goToDirection(
                MainMenuFragmentDirections.actionMainMenuFragmentToPlayMenuFragment(soundOn),
                view
            )
        }
        binding.settingsButton.setOnClickListener {
            sfxManager.play(Sound.BUTTON_CLICK)

            goToDirection(
                MainMenuFragmentDirections.actionMainMenuFragmentToSettingsFragment(soundOn),
                view
            )
        }
        binding.rulesButton.setOnClickListener {
            sfxManager.play(Sound.BUTTON_CLICK)

            goToDirection(MainMenuFragmentDirections.actionMainMenuFragmentToRulesFragment(soundOn), view)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
