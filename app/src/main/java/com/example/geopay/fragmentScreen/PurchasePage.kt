package com.example.geopay.fragmentScreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.BatchGetItemRequest
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import aws.sdk.kotlin.services.dynamodb.model.KeysAndAttributes
import com.example.geopay.R
import com.example.geopay.fragmentScreen.miniFrag.home_myoffers_recycler_adapter
import com.example.geopay.fragmentScreen.miniFrag.payment_recycler_adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PurchasePage : Fragment() {

    lateinit var purchaseRecyclerView: RecyclerView
    lateinit var purchaseRecyclerAdapter: payment_recycler_adapter

    var result = arrayListOf<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_purchase_page, container, false)

        purchaseRecyclerView = view.findViewById(R.id.payment_recycler)
        purchaseRecyclerView.layoutManager = LinearLayoutManager(context)

        for (i in 1..10) {
            run {
                getSpecificItem("", i.toString())
                Log.d("tag",result.toString())

            }
        }
        GlobalScope.launch(Dispatchers.Main) {
            delay(2500)
            Log.d("tag",result.toString())
            purchaseRecyclerView.adapter = payment_recycler_adapter(result)

            result= arrayListOf<String>()

        }

        return view
    }
    fun getSpecificItem(keyVal: String,offerId: String)=
        GlobalScope.launch(Dispatchers.IO) {
            val keyToGet = mutableMapOf<String, AttributeValue>()
//            keyToGet["mobile"] = AttributeValue.S(keyVal)
                keyToGet["offer_id"] = AttributeValue.S(offerId)

            val request = GetItemRequest {
                key = keyToGet
                tableName = "availed_offer"
            }

//
//            forumTableKeysAndAttributes.addHashOnlyPrimaryKeys(
//                "Title",
//                "Updates",
//                "Product Line 1"
//            )

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
                if(numbersMap?.get("discount")?.asS()!=null) {
                    val str = numbersMap?.get("discount")
                        ?.asS() + " off availed at " + numbersMap?.get("partner")?.asS()

                    result.add(str)
                }
            }
        }


}