package com.example.el_indeciso

import android.os.Bundle
import android.view.*
import com.example.el_indeciso.databinding.FragmentMainMenuBinding

class MainMenuFragment : BaseFragment() {
    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!
    override fun layoutId() = R.layout.fragment_main_menu

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var mensajero = MovesMessenger("DesdeElRepo")
        mensajero.addMove(Move("Niqui", 1))
        mensajero.addMove(Move("Eze", 2))

        binding.profileButton.setOnClickListener { goToFragment(ProfileMenuFragment()) }
        binding.playButton.setOnClickListener { goToFragment(PlayMenuFragment()) }
        binding.settingsButton.setOnClickListener { goToFragment(SettingsFragment()) }
        binding.rulesButton.setOnClickListener { goToFragment(RulesFragment()) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
