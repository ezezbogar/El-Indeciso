package com.example.el_indeciso

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.el_indeciso.databinding.FragmentSettingsBinding


class SettingsFragment : BaseFragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    override fun layoutId() = R.layout.fragment_join_game

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.languageButtonSettings.setOnClickListener {
            changeLanguageState()
        }*/
        binding.musicButtonSettings.setOnClickListener {
            changeMusicState()
        }
        binding.contactUsButton.setOnClickListener {
            val queryUrl: Uri = Uri.parse("https://discord.com/channels/865976186643021854/865976186643021856")
            val intent = Intent(Intent.ACTION_VIEW, queryUrl)
            context?.startActivity(intent)
        }
        binding.prevPageSettings.setOnClickListener {
            goToDirection(
                SettingsFragmentDirections.actionSettingsFragmentToMainMenuFragment(),
                view
            )
        }
    }

    private fun changeMusicState(){
        if(binding.musicButtonSettings.text == getString(R.string.music_on_text)){
            binding.musicButtonSettings.text = getString(R.string.music_off_text)
        }
        else{
            binding.musicButtonSettings.text = getString(R.string.music_on_text)
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