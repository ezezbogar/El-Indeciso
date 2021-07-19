package com.example.el_indeciso

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.el_indeciso.databinding.FragmentSettingsBinding


class SettingsFragment : BaseFragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    override fun layoutId() = R.layout.fragment_join_game

    private lateinit var sfxManager: SFX_Manager
    private val args: SettingsFragmentArgs by navArgs()
    private var soundOn = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sfxManager = SFX_Manager(requireActivity().applicationContext)
        soundOn = args.soundOn
        sfxManager.changeSoundStatus(soundOn)

        setMusicTextState()

        binding.musicButtonSettings.setOnClickListener {
            changeMusicState()
            sfxManager.play(Sound.BUTTON_CLICK)
        }
        binding.contactUsButton.setOnClickListener {
            sfxManager.play(Sound.BUTTON_CLICK)
            val queryUrl: Uri =
                Uri.parse("https://discord.com/channels/865976186643021854/865976186643021856")
            val intent = Intent(Intent.ACTION_VIEW, queryUrl)
            context?.startActivity(intent)
        }
        binding.prevPageSettings.setOnClickListener {
            sfxManager.play(Sound.BUTTON_CLICK)
            goToDirection(
                SettingsFragmentDirections.actionSettingsFragmentToMainMenuFragment(soundOn),
                view
            )
        }
    }

    private fun changeMusicState() {

        soundOn = !soundOn
        sfxManager.changeSoundStatus(soundOn)

        if (sfxManager.soundOn) {
            binding.musicButtonSettings.text = getString(R.string.music_on_text)
        } else {
            binding.musicButtonSettings.text = getString(R.string.music_off_text)
        }

    }

    private fun setMusicTextState() {
        if (sfxManager.soundOn) {
            binding.musicButtonSettings.text = getString(R.string.music_on_text)
        } else {
            binding.musicButtonSettings.text = getString(R.string.music_off_text)
        }
    }
    /*private fun changeLanguageState(){
        if(binding.languageButtonSettings.text == "ESPAÑOL X2"){
            binding.languageButtonSettings.text = "ESPAÑOL"
        }
        else{
            binding.languageButtonSettings.text = "ESPAÑOL X2"
        }

    }*/
}