package com.example.geopay.fragmentScreen.miniFrag

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.geopay.R
import com.example.touchlessbiometrics.dataclass.home_myoffer_dataclass

class home_myoffers_recycler_adapter(private val offerList: ArrayList<home_myoffer_dataclass>): RecyclerView.Adapter<home_myoffers_recycler_adapter.Home_myoffers_RecyclerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Home_myoffers_RecyclerViewHolder {
        // Inflate Layout in this method which we have created.
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_myoffer_carditem, parent, false)

        return Home_myoffers_RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: Home_myoffers_RecyclerViewHolder, position: Int) {
        val currentItem = offerList[position]
        holder.offerImage.setImageResource(currentItem.offerimage)
        holder.offername.text=currentItem.offername
    }

    override fun getItemCount(): Int {
        return offerList.size
    }
    class Home_myoffers_RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var offerImage: ImageView = itemView.findViewById(R.id.myoffers_card_offerimage)
        var offername: TextView = itemView.findViewById(R.id.myoffers_card_txtOffer)

    }
}
