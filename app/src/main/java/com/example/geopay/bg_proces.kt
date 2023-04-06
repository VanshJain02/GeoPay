package com.example.geopay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.DynamoDbException
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi


class bg_proces: BroadcastReceiver() {
    var lat =""
    var lon = ""
    override fun onReceive(context: Context?, intent: Intent?) {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendAutoLocation()= runBlocking{
        val staticCredentials = StaticCredentialsProvider {
            accessKeyId = "AKIAW36QAMHQM4X5322A"
            secretAccessKey = "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"
        }
//        val sharedPreferences = getSharedPreferences("shared preferences", AppCompatActivity.MODE_PRIVATE
//        )
//        val auto_phoneno = sharedPreferences?.getString("phoneno", "")

        val auto_phoneno = "9876543210"

        val ddb = DynamoDbClient{
            region = "ap-south-1"
            credentialsProvider = staticCredentials
        }
        val itemValues = mutableMapOf<String, AttributeValue>()

        // Add all content to the table.
        itemValues["mobile"] = auto_phoneno?.let { AttributeValue.S(it) }!!
        itemValues["timestamp"] = AttributeValue.S(
            DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now()).toString())
        itemValues["latitude"]= AttributeValue.S(lat.toString())
        itemValues["longitude"]= AttributeValue.S(lon.toString())

        val request = PutItemRequest {
            tableName="Location"
            item = itemValues
        }

        try {
            ddb.putItem(request)
            var mRequestQueue: RequestQueue
            var mStringRequest: StringRequest
            var url: String  = "https://81hv4ugz0i.execute-api.ap-south-1.amazonaws.com/v1/location";
//            mRequestQueue = Volley.newRequestQueue()
//            mStringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String?>() {
//                fun onResponse(response: String) {
//               //display the response on screen
//                }
//            }, Response.ErrorListener() {
//                fun onErrorResponse(error: VolleyError) {
//                    Log.i("ERROR", "Error :$error")
//                }
//            })
//            mRequestQueue.add(mStringRequest)



        } catch (ex: DynamoDbException) {
            println(ex.message)
            ddb.close()
            exitProcess(0)
        }

    }



}