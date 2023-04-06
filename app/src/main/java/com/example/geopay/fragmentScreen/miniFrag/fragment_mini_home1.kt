package com.example.geopay.fragmentScreen.miniFrag

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IdRes
import com.example.geopay.Home_Main
import com.example.geopay.R
import com.example.geopay.fragmentScreen.SearchPage
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import kotlin.random.Random


class fragment_mini_home1 : Fragment() {

    private var searchFragment = SearchPage()


    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_mini_home1, container, false)
        val bestofferimage = arrayOf(
            R.drawable.offer_1,
            R.drawable.offer_2,
            R.drawable.offer_3,
            R.drawable.offer_4,
            R.drawable.offer_5
        )
        val carouselbot: ImageCarousel = view.findViewById(R.id.carousel_bottom_homefrag)
        carouselbot.registerLifecycle(lifecycle)
        val list1 = mutableListOf<CarouselItem>()
        list1.add(
            CarouselItem(
                imageDrawable = R.drawable.img_rectangle51
            )
        )
        list1.add(
            CarouselItem(
                imageDrawable = R.drawable.img_rectangle53
            )
        )
        carouselbot.setData(list1)

        val randomIndex = Random.nextInt(bestofferimage.size);


        view.findViewById<ImageView>(R.id.homemini_bestoffer_image).setImageResource(bestofferimage[randomIndex])
        return view
    }


}