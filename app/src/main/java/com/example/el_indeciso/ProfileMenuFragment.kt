package com.example.el_indeciso

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.el_indeciso.databinding.FragmentProfileMenuBinding


class ProfileMenuFragment : BaseFragment() {
    private var _binding: FragmentProfileMenuBinding? = null
    private val binding get() = _binding!!
    override fun layoutId() = R.layout.fragment_join_game

    private var back_index = 0
    private var face_index = 0
    private var outfit_index = 0
    private var head_index = 0

    var profile_pic: String = "0000"

    companion object {
        val BACKGROUNDS = listOf(
            R.drawable.back_0, R.drawable.back_1, R.drawable.back_2,
            R.drawable.back_3, R.drawable.back_4, R.drawable.back_5,
            R.drawable.back_6, R.drawable.back_7, R.drawable.back_8,
            R.drawable.back_9,
        )
        val FACES = listOf(
            R.drawable.face_0, R.drawable.face_1, R.drawable.face_2,
            R.drawable.face_3, R.drawable.face_4, R.drawable.face_5,
            R.drawable.face_6,
        )
        val OUTFITS = listOf(
            R.drawable.outfit_0, R.drawable.outfit_1,
            R.drawable.outfit_2, R.drawable.outfit_3,
        )
        val HEADS = listOf(
            R.drawable.head_0, R.drawable.head_1, R.drawable.head_2,
            R.drawable.head_3, R.drawable.head_4, R.drawable.head_5,
            R.drawable.head_6,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileMenuBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val head: ImageView = binding.headProfileMenu
        val face: ImageView = binding.faceProfileMenu
        val outfit: ImageView = binding.outfitProfileMenu
        val back: ImageView = binding.backProfileMenu

        binding.nextHead.setOnClickListener {
            head_index = nextButtonClicked(head_index, ProfileMenuFragment.HEADS, head)
        }
        binding.prevHead.setOnClickListener {
            head_index = prevButtonClicked(head_index, ProfileMenuFragment.HEADS, head)
        }
        binding.nextFace.setOnClickListener {
            face_index = nextButtonClicked(face_index, ProfileMenuFragment.FACES, face)
        }
        binding.prevFace.setOnClickListener {
            face_index = prevButtonClicked(face_index, ProfileMenuFragment.FACES, face)
        }
        binding.nextOutfit.setOnClickListener {
            outfit_index = nextButtonClicked(outfit_index, ProfileMenuFragment.OUTFITS, outfit)
        }
        binding.prevOutfit.setOnClickListener {
            outfit_index = prevButtonClicked(outfit_index, ProfileMenuFragment.OUTFITS, outfit)
        }
        binding.nextBack.setOnClickListener {
            back_index = nextButtonClicked(back_index, ProfileMenuFragment.BACKGROUNDS, back)
        }
        binding.prevBack.setOnClickListener {
            back_index = prevButtonClicked(back_index, ProfileMenuFragment.BACKGROUNDS, back)
        }
        binding.saveButtonProfileMenu.setOnClickListener {
            val back_digit = Integer.toHexString(back_index)
            val head_digit = Integer.toHexString(head_index)
            val face_digit = Integer.toHexString(face_index)
            val outfit_digit = Integer.toHexString(outfit_index)

            profile_pic = "${back_digit}${head_digit}${face_digit}${outfit_digit}"

            goToFragment(MainMenuFragment())
        }
        binding.prevPageProfileMenu.setOnClickListener {
            goToFragment(MainMenuFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun nextButtonClicked(
        _avatarPartIndex: Int,
        listOfAvatarPart: List<Int>,
        imageView: ImageView
    )
            : Int {
        var avatarPartIndex = _avatarPartIndex
        avatarPartIndex++
        if (avatarPartIndex >= listOfAvatarPart.size) avatarPartIndex = 0
        imageView.setImageResource(listOfAvatarPart[avatarPartIndex])

        return avatarPartIndex
    }

    private fun prevButtonClicked(
        _avatarPartIndex: Int,
        listOfAvatarPart: List<Int>,
        imageView: ImageView
    )
            : Int {
        var avatarPartIndex = _avatarPartIndex
        avatarPartIndex--
        if (avatarPartIndex < 0) avatarPartIndex = listOfAvatarPart.size - 1
        imageView.setImageResource(listOfAvatarPart[avatarPartIndex])

        return avatarPartIndex
    }
}