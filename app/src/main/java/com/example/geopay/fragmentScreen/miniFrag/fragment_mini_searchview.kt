package com.example.geopay.fragmentScreen.miniFrag

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import com.example.geopay.R
import com.example.touchlessbiometrics.dataclass.home_myoffer_dataclass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class fragment_mini_searchview : Fragment() {

    lateinit var homemyofferRecyclerView: RecyclerView

    var offerslist= arrayListOf<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_mini_searchview, container, false)

        homemyofferRecyclerView = view.findViewById<RecyclerView>(R.id.home_alloffers_recycler)
        homemyofferRecyclerView.layoutManager = LinearLayoutManager(context)
        getSpecificItem()
        GlobalScope.launch(Dispatchers.Main) {
            delay(3000)
            Log.d("TAG",offerslist.toString())
            homemyofferRecyclerView.adapter = home_searchview_recycler_adapter(offerslist)

        }


        return view

    }

    fun getSpecificItem()=
        GlobalScope.launch(Dispatchers.IO) {

            val staticCredentials = StaticCredentialsProvider {
                accessKeyId = "AKIAW36QAMHQM4X5322A"
                secretAccessKey = "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"
            }
            val myRandomValues = List(8) { Random.nextInt(0, 30) }
            for (i in myRandomValues) {
                val keyToGet = mutableMapOf<String, AttributeValue>()
//            keyToGet["mobile"] = AttributeValue.S(keyVal)
                keyToGet["offer_id"] = AttributeValue.S(i.toString())

                val request = GetItemRequest {
                    key = keyToGet
                    tableName = "offers"
                }

                DynamoDbClient {
                    region = "ap-south-1"
                    credentialsProvider = staticCredentials
                }.use { ddb ->
                    val returnedItem = ddb.getItem(request)
                    val numbersMap = returnedItem.item
                    numbersMap?.forEach { key1 ->
                        Log.d("FETCH", key1.key)
                        Log.d("FETCH", key1.value.toString())

                    }
                    offerslist.add(numbersMap?.get("Discount")?.asS().toString()+" OFF ON THIS "+ numbersMap?.get("Partner")?.asS().toString())


                }
            }
        }




}