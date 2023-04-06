package com.example.geopay.fragmentScreen

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.example.geopay.R
import com.example.geopay.fragmentScreen.miniFrag.*


class SettingsPage : Fragment(){

    private var editProfile = fragment_mini_settings_editprofile()
    private var appPermissio = fragment_mini_settings_apppermissions()
    private var communicationCh = fragment_mini_settings_communicationch()
    private var currentSelectItemId = 1
    private var savedStateSparseArray = SparseArray<SavedState>()

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_settings_page, container, false)

        view.findViewById<LinearLayout>(R.id.settings_editprofile).setOnClickListener{
            makeCurrentFragment(editProfile,1)
        }
        view.findViewById<LinearLayout>(R.id.settings_permissions).setOnClickListener{
            makeCurrentFragment(appPermissio,2)
        }
        view.findViewById<LinearLayout>(R.id.settings_communicationchannel).setOnClickListener{
            makeCurrentFragment(communicationCh,3)
        }

        return view
    }

    private fun savedFragmentState(actionId: Int) {
        val currentFragment = parentFragmentManager.findFragmentById(R.id.fl_wrapper)
        if (currentFragment != null) {

            savedStateSparseArray.put(currentSelectItemId,
                parentFragmentManager.saveFragmentInstanceState(currentFragment)
            )
        }
        currentSelectItemId = actionId
    }

    private fun makeCurrentFragment(Fragment: Fragment, @IdRes actionId: Int) {
//        savedFragmentState(actionId)
//        createFragment(Fragment,actionId)
//        Fragment.setInitialSavedState(savedStateSparseArray[actionId])
        val Fmanager = parentFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper,Fragment)
            addToBackStack("settings")
            commit()
        }
    }



}