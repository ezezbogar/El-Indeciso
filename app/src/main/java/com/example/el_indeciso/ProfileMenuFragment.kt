package com.example.el_indeciso

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.el_indeciso.databinding.FragmentProfileMenuBinding


class ProfileMenuFragment : BaseFragment() {
    private var _binding: FragmentProfileMenuBinding? = null
    private val binding get() = _binding!!
    override fun layoutId() = R.layout.fragment_join_game

    private var backIndex = 0
    private var faceIndex = 0
    private var outfitIndex = 0
    private var headIndex = 0

    private var profile_name: String = "Hola"
    private var profilePic: String = "0000"

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
    ): View {
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

        //Edittext and Save Button Visibility is set as invisible
        binding.playerNameEdittextProfileMenu.visibility = View.GONE
        binding.saveNameButtonProfileMenu.visibility = View.GONE
        binding.textField.visibility = View.GONE

        binding.playerNameTextviewProfileMenu.text = profile_name

        binding.changeNameButtonProfileMenu.setOnClickListener {

            //Edittext and Save Button Visibility is set as visible
            //Textview and Change Button Visibility is set as invisible
            changeVisibilityOfButtons()

            //Set old profile name
            binding.playerNameEdittextProfileMenu.setText(profile_name)

            //Set length limit to profile name
            binding.playerNameEdittextProfileMenu.filters += InputFilter.LengthFilter(12)
        }

        binding.saveNameButtonProfileMenu.setOnClickListener {
            var actual_text = binding.playerNameEdittextProfileMenu.text.toString()

            if(actual_text.length < 4){
                setErrorTextField(true)
            }
            else{
                //Edittext and Save Button Visibility is set as invisible
                //Textview and Change Button Visibility is set as visible
                changeVisibilityOfButtons()

                //Get new profile name
                profile_name = binding.playerNameEdittextProfileMenu.text.toString()

                setErrorTextField(false)

                //Show new profile name
                binding.playerNameTextviewProfileMenu.text = profile_name
            }
        }

        binding.nextHead.setOnClickListener {
            headIndex = nextButtonClicked(headIndex, HEADS, head)
        }
        binding.prevHead.setOnClickListener {
            headIndex = prevButtonClicked(headIndex, HEADS, head)
        }
        binding.nextFace.setOnClickListener {
            faceIndex = nextButtonClicked(faceIndex, FACES, face)
        }
        binding.prevFace.setOnClickListener {
            faceIndex = prevButtonClicked(faceIndex, FACES, face)
        }
        binding.nextOutfit.setOnClickListener {
            outfitIndex = nextButtonClicked(outfitIndex, OUTFITS, outfit)
        }
        binding.prevOutfit.setOnClickListener {
            outfitIndex = prevButtonClicked(outfitIndex, OUTFITS, outfit)
        }
        binding.nextBack.setOnClickListener {
            backIndex = nextButtonClicked(backIndex, BACKGROUNDS, back)
        }
        binding.prevBack.setOnClickListener {
            backIndex = prevButtonClicked(backIndex, BACKGROUNDS, back)
        }
        binding.saveButtonProfileMenu.setOnClickListener {
            val backDigit = Integer.toHexString(backIndex)
            val headDigit = Integer.toHexString(headIndex)
            val faceDigit = Integer.toHexString(faceIndex)
            val outfitDigit = Integer.toHexString(outfitIndex)

            profilePic = "${backDigit}${headDigit}${faceDigit}${outfitDigit}"

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

    fun changeVisibilityOfButtons() {

        //Edittext and Save Button Visibility is set as invisible
        invertVisibility(binding.playerNameEdittextProfileMenu)
        invertVisibility(binding.saveNameButtonProfileMenu)
        invertVisibility(binding.textField)

        //Textview and Change Button Visibility is set as visible
        invertVisibility(binding.playerNameTextviewProfileMenu)
        invertVisibility(binding.changeNameButtonProfileMenu)
    }

    fun invertVisibility(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    /*
* Sets and resets the text field error status.
*/
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.playerNameEdittextProfileMenu.text = null
        }
    }
}


