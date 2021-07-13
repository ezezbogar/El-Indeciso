package com.example.el_indeciso

import android.os.Bundle
import android.text.InputFilter
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.el_indeciso.databinding.FragmentProfileMenuBinding

class ProfileMenuFragment : BaseFragment() {
    private var _binding: FragmentProfileMenuBinding? = null
    private val binding get() = _binding!!
    override fun layoutId() = R.layout.fragment_join_game

    private var backIndex = 0
    private var faceIndex = 0
    private var outfitIndex = 0
    private var headIndex = 0

    private lateinit var actualText: String

    private var profileName: String = "player"
    private var profilePic: String = "0000"
    private val fileName: String = "profile_info.txt"
    private val delimiter: String = " - "


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileMenuBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val head: ImageView = binding.headProfileMenu
        val face: ImageView = binding.faceProfileMenu
        val outfit: ImageView = binding.outfitProfileMenu
        val back: ImageView = binding.backProfileMenu
        val editText: EditText = binding.playerNameEdittextProfileMenu
        val textView: TextView = binding.playerNameTextviewProfileMenu

        //TextField, EditText and Save Button Visibility are set as invisible
        editText.visibility = View.GONE
        binding.saveNameButtonProfileMenu.visibility = View.GONE
        binding.textField.visibility = View.GONE

        //Set profile name and profile pic
        setInitialData()

        //Action when wanting to change profile name
        binding.changeNameButtonProfileMenu.setOnClickListener {

            //Edittext and Save Button Visibility is set as visible
            //Textview and Change Button Visibility is set as invisible
            changeVisibilityOfButtons()

            //Set old profile name
            editText.setText(profileName)

            //Set length limit to profile name
            editText.filters += InputFilter.LengthFilter(8)

            //Focus change to EditText
            editText.setFocus()
        }

        binding.saveNameButtonProfileMenu.setOnClickListener {

            if (!validProfileName()) {
                setErrorTextField(true)
            } else { //Profile name is ok

                //Edittext and Save Button Visibility is set as invisible
                //Textview and Change Button Visibility is set as visible
                changeVisibilityOfButtons()

                //Get new profile name
                profileName = editText.text.toString()

                //Always set error as false
                setErrorTextField(false)

                //Show new profile name
                textView.text = profileName
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
            if (editText.visibility == View.VISIBLE) {
                showMessageToast("Please set a profile name")
            } else {
                val backDigit = Integer.toHexString(backIndex)
                val headDigit = Integer.toHexString(headIndex)
                val faceDigit = Integer.toHexString(faceIndex)
                val outfitDigit = Integer.toHexString(outfitIndex)

                profilePic = "${backDigit}${headDigit}${faceDigit}${outfitDigit}"

                //Internal Storage: Write info into profile_info.txt
                val textToWrite = "$profilePic$delimiter$profileName"
                writeFile(fileName, textToWrite)
                goToFragment(MainMenuFragment())
            }
        }
        binding.prevPageProfileMenu.setOnClickListener {
            if (editText.visibility == View.VISIBLE) {
                showMessageToast("Please set a profile name")
            } else {
                goToFragment(MainMenuFragment())
            }
        }

        editText.setOnKeyListener { v, keyCode, event ->
            when {
                //Check if it is the Enter-Key,      Check if the Enter Key was pressed down
                ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN)) -> {
                    binding.saveNameButtonProfileMenu.performClick()
                    editText.hideKeyboard()
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

    /*
     * Action when prev button is clicked.
     */
    private fun updateImage(back: Int, head: Int, face: Int, outfit: Int) {
        binding.backProfileMenu.setImageResource(BACKGROUNDS[back])
        binding.headProfileMenu.setImageResource(HEADS[head])
        binding.faceProfileMenu.setImageResource(FACES[face])
        binding.outfitProfileMenu.setImageResource(OUTFITS[outfit])
    }

    /*
     * Set profile name and profile pic.
     */
    private fun setInitialData() {
        val readData = readFile(fileName) as CharSequence

        //If there is information saved, it's loaded
        if (readData.isNotEmpty()) {
            val list = readData.split(delimiter)

            //Update with the file information
            profileName = list[1]
            profilePic = list[0].getDigit()
        }

        //Update profile pic information to int
        val array: Array<String> = profilePic.toCharArray().map { it.toString() }.toTypedArray()
        backIndex = array[0].toIntOrNull()!!
        headIndex = array[1].toIntOrNull()!!
        faceIndex = array[2].toIntOrNull()!!
        outfitIndex = array[3].toIntOrNull()!!

        //Set information
        updateImage(backIndex, headIndex, faceIndex, outfitIndex)
        binding.playerNameTextviewProfileMenu.text = profileName
        binding.playerNameEdittextProfileMenu.setText(profileName)
    }
}


