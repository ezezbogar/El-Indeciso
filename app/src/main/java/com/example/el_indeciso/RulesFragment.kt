package com.example.el_indeciso

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.el_indeciso.databinding.FragmentRulesBinding


class RulesFragment : BaseFragment() {
    private var _binding: FragmentRulesBinding? = null
    private val binding get() = _binding!!
    override fun layoutId() = R.layout.fragment_join_game

    private val args: RulesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRulesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sfxManager = SFX_Manager(requireActivity().applicationContext)
        val soundOn = args.soundOn
        sfxManager.changeSoundStatus(soundOn)

        binding.prevPageRules.setOnClickListener {
            sfxManager.play(Sound.BUTTON_CLICK)
            goToDirection(
                RulesFragmentDirections.actionRulesFragmentToMainMenuFragment(soundOn),
                view
            )
        }
    }
}