package com.example.el_indeciso

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.view.*
import android.widget.ImageView
import com.example.el_indeciso.databinding.FragmentProfileMenuBinding
import java.io.FileNotFoundException
import java.io.IOException


class ProfileMenuFragment : BaseFragment() {
    private var _binding: FragmentProfileMenuBinding? = null
    private val binding get() = _binding!!
    override fun layoutId() = R.layout.fragment_join_game

    private var backIndex = 0
    private var faceIndex = 0
    private var outfitIndex = 0
    private var headIndex = 0

    private lateinit var actualText: String

    private var profileName: String = "isla_sol.8"
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

    /*
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }
     */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileMenuBinding.inflate(inflater, container, false)
        //val view = binding.root

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val head: ImageView = binding.headProfileMenu
        val face: ImageView = binding.faceProfileMenu
        val outfit: ImageView = binding.outfitProfileMenu
        val back: ImageView = binding.backProfileMenu

        //TextField, EditText and Save Button Visibility are set as invisible
        binding.playerNameEdittextProfileMenu.visibility = View.GONE
        binding.saveNameButtonProfileMenu.visibility = View.GONE
        binding.textField.visibility = View.GONE

        //The text of the Textview is updated with the profile name
        binding.playerNameTextviewProfileMenu.text = profileName

        //Action when wanting to change profile name
        binding.changeNameButtonProfileMenu.setOnClickListener {

            //Edittext and Save Button Visibility is set as visible
            //Textview and Change Button Visibility is set as invisible
            changeVisibilityOfButtons()

            //Set old profile name
            binding.playerNameEdittextProfileMenu.setText(profileName)

            //Set length limit to profile name
            binding.playerNameEdittextProfileMenu.filters += InputFilter.LengthFilter(12)

            //Focus change to EditText
            binding.playerNameEdittextProfileMenu.setFocus()
        }

        binding.saveNameButtonProfileMenu.setOnClickListener {

            if (!validProfileName()) {
                setErrorTextField(true)
            } else { //Profile name is ok

                //Edittext and Save Button Visibility is set as invisible
                //Textview and Change Button Visibility is set as visible
                changeVisibilityOfButtons()

                //Get new profile name
                profileName = binding.playerNameEdittextProfileMenu.text.toString()

                //Always set error al false
                setErrorTextField(false)

                //Show new profile name
                binding.playerNameTextviewProfileMenu.text = profileName
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
            if (binding.playerNameEdittextProfileMenu.visibility == View.VISIBLE) {
                showMessageToast("Please set a profile name")
            } else {
                val backDigit = Integer.toHexString(backIndex)
                val headDigit = Integer.toHexString(headIndex)
                val faceDigit = Integer.toHexString(faceIndex)
                val outfitDigit = Integer.toHexString(outfitIndex)

                profilePic = "${backDigit}${headDigit}${faceDigit}${outfitDigit}"

                //Internal Storage: Write info into profile_info.txt
                var textToWrite = "$profilePic - $profileName"
                writeFile("profile_info.txt", textToWrite)
                goToFragment(MainMenuFragment())
            }
        }
        binding.prevPageProfileMenu.setOnClickListener {
            goToFragment(MainMenuFragment())
        }

        binding.playerNameEdittextProfileMenu.setOnKeyListener { v, keyCode, event ->

            when {
                //Check if it is the Enter-Key,      Check if the Enter Key was pressed down
                ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN)) -> {
                    binding.saveNameButtonProfileMenu.performClick()
                    binding.playerNameEdittextProfileMenu.hideKeyboard()
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
     * Action when next button is clicked.
     */
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

    /*
     * Action when prev button is clicked.
     */
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

    /*
     * Change visibility of elements for changing profile name.
     */
    private fun changeVisibilityOfButtons() {

        //Edittext and Save Button Visibility is set as invisible
        invertVisibility(binding.playerNameEdittextProfileMenu)
        invertVisibility(binding.saveNameButtonProfileMenu)
        invertVisibility(binding.textField)

        //Textview and Change Button Visibility is set as visible
        invertVisibility(binding.playerNameTextviewProfileMenu)
        invertVisibility(binding.changeNameButtonProfileMenu)
    }

    /*
     * Returns if the profile name meets the requirements
     */
    private fun validProfileName(): Boolean {
        actualText = binding.playerNameEdittextProfileMenu.text.toString()

        return (actualText.length > 3
                && !(actualText.contains(" ")))
    }

    /*
     * Sets and resets the text field error status.
     */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.playerNameEdittextProfileMenu.setTextColor(resources.getColor(R.color.red))
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.playerNameEdittextProfileMenu.setTextColor(resources.getColor(R.color.hint_color))
            binding.playerNameEdittextProfileMenu.text = null
        }
    }
}


