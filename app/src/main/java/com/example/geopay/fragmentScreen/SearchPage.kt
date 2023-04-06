package com.example.geopay.fragmentScreen

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.example.geopay.R
import com.example.geopay.fragmentScreen.miniFrag.fragment_mini_searchview
import java.util.*


class SearchPage : Fragment() {


    @SuppressLint("MissingInflatedId", "ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var geocoder: Geocoder
        var addresses: List<Address>?

        val view= inflater.inflate(R.layout.fragment_search_page, container, false)

        view.findViewById<androidx.appcompat.widget.SearchView>(R.id.search_page_searchbar).setOnClickListener{
            makeCurrentFragment(fragment_mini_searchview(),1)
        }
        // Inflate the layout for this fragment
        return view
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