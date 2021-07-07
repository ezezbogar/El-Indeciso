package com.example.el_indeciso

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

abstract class BaseFragment : Fragment() {

    abstract fun layoutId(): Int

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(layoutId(), container, false)

    /*
     * Set new fragment in fragment_container.
     */
    fun goToFragment(fragment_to_go: Fragment) {
        val fragment: Fragment = fragment_to_go
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    /*
     * Change visibility of an element.
     */
    fun invertVisibility(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    /*
     * Shows a message in a toast
     */
    fun showMessageToast(message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.show()
    }
}