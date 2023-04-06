package com.example.geopay.fragmentScreen.miniFrag

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.BatchGetItemRequest
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import com.example.geopay.R
import com.example.touchlessbiometrics.dataclass.home_myoffer_dataclass
import kotlinx.coroutines.*
import kotlin.random.Random


class fragment_mini_home2 : Fragment() {
    val sharedPreferences = activity?.getSharedPreferences("shared preferences",
        AppCompatActivity.MODE_PRIVATE
    )
    val auto_phoneno = sharedPreferences?.getString("phoneno", "")
    var result= (arrayListOf <home_myoffer_dataclass>())

    lateinit var myofferRecyclerAdapter: home_myoffers_recycler_adapter
    val offerimage = arrayOf(R.drawable.img_image40,
        R.drawable.img_image42,
        R.drawable.offer_1,
        R.drawable.offer_2,
        R.drawable.offer_3,
        R.drawable.offer_4,
        R.drawable.offer_5

    )
    val offername = arrayOf(R.string.msg_15_off_on_purc,
        R.string.msg_15_off_on_purc2
    )
    lateinit var homemyofferRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mini_home2, container, false)
        homemyofferRecyclerView = view.findViewById<RecyclerView>(R.id.home_myoffer_recycler)

        homemyofferRecyclerView.layoutManager = LinearLayoutManager(context)
        for (i in 1..3) {
            run {
                getSpecificItem("", i.toString())
                Log.d("tag",result.toString())

            }
        }
        GlobalScope.launch(Dispatchers.Main) {
            delay(2000)
            homemyofferRecyclerView.adapter = home_myoffers_recycler_adapter(result)

        }


//        val myofferlist = arrayListOf<home_myoffer_dataclass>()
        return view
    }

    fun getSpecificItem(keyVal: String,offerId: String)=
        GlobalScope.launch(Dispatchers.IO) {
            val keyToGet = mutableMapOf<String, AttributeValue>()
//            keyToGet["mobile"] = AttributeValue.S(keyVal)
            keyToGet["offer_id"] = AttributeValue.S(offerId)

            val request = GetItemRequest {
                key = keyToGet
                tableName = "cust_offer"
            }

            val staticCredentials = StaticCredentialsProvider {
                accessKeyId = "AKIAW36QAMHQM4X5322A"
                secretAccessKey = "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"
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
                val randomIndex = Random.nextInt(offerimage.size);

                val offer = home_myoffer_dataclass(offerimage[randomIndex],numbersMap?.get("discount")?.asS().toString()+" off at " +numbersMap?.get("partner")?.asS().toString())
                result.add(offer)



            }
        }


}