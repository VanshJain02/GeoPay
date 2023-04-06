package com.example.geopay.fragmentScreen.miniFrag

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import com.example.geopay.Database
import com.example.geopay.Database1
import com.example.geopay.R
import com.example.touchlessbiometrics.dataclass.home_myoffer_dataclass
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class home_searchview_recycler_adapter(private val offerList: ArrayList<String>): RecyclerView.Adapter<home_searchview_recycler_adapter.Home_searchview_RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Home_searchview_RecyclerViewHolder {
        // Inflate Layout in this method which we have created.
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_searchview_carditem, parent, false)
        return Home_searchview_RecyclerViewHolder(view)
    }

    fun submitData(NameVal: String,emailVal: String) = runBlocking{



        val data = Database1()

        val staticCredentials = StaticCredentialsProvider {
            accessKeyId = "AKIAW36QAMHQM4X5322A"
            secretAccessKey = "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"
        }

        val ddb = DynamoDbClient{
            region = "ap-south-1"
            credentialsProvider = staticCredentials
        }

        // Set values to save in the Amazon DynamoDB table.
        val uuid: UUID = UUID.randomUUID()
        val tableName = "availed_offer"
        val name = "discount"
        val email = "partner"
        Log.d("TAG","WORKING")

        val myRandomValues = List(1) { kotlin.random.Random.nextInt(6, 10) }


        data.putItemInTable2(ddb, tableName, "offer_id",myRandomValues[0].toString(),name, NameVal, email, emailVal)
    }


    override fun onBindViewHolder(holder: Home_searchview_RecyclerViewHolder, position: Int) {
        val currentItem = offerList[position]

        holder.offerBtn.setOnClickListener{
            holder.offerBtn.text = "Availed"
            var executor: ExecutorService? = Executors.newSingleThreadExecutor()
            executor?.execute(Runnable{
                submitData("15%","Dominos")

            })
        }
        holder.offername.text=currentItem.toString()
    }

    override fun getItemCount(): Int {
        return offerList.size
    }
    class Home_searchview_RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var offername: TextView = itemView.findViewById(R.id.alloffers_card_txtOffer)
        var offerBtn: AppCompatButton = itemView.findViewById(R.id.alloffers_GetOffer)

    }
}
