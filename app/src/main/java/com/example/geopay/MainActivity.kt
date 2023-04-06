package com.example.geopay

import android.content.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        AutoLogin()
//        intent = Intent(this@MainActivity, LoginPage::class.java)
//
//
//        startActivity(intent)
//
//        finish()


    }

    private fun AutoLogin() {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val auto_phoneno = sharedPreferences.getString("phoneno", "")
        val auto_password = sharedPreferences.getString("password", "")
        Log.d("saved phoneno", auto_phoneno!!)
        Log.d("saved password", auto_password!!)
        GlobalScope.launch(Dispatchers.IO) {
            val keyToGet = mutableMapOf<String, AttributeValue>()
            keyToGet["mobile"] = AttributeValue.S(auto_phoneno)
            val request = GetItemRequest {
                key = keyToGet
                tableName = "Users"
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
                numbersMap?.get("password")?.let { Log.d("PASS", it.asS().toString()) }
                if(numbersMap?.get("password")?.asS().equals(auto_password.toString())){
                    val intent = Intent(baseContext, Home_Main::class.java)
                    intent.putExtra("phoneno", auto_phoneno)
                    intent.putExtra("startingpage", "1")
                    startActivity(intent)
                    finish()
                }
                else{
//                    password?.setError("Incorrect Password")
//                    password?.requestFocus()
                    val intent = Intent(this@MainActivity, LoginPage::class.java)
                    startActivity(intent)
                    finish()


                    Log.d("LOG","FAILED")
                }
            }

//        val reference: DatabaseReference =
//            FirebaseDatabase.getInstance("https://geopay-627c0-default-rtdb.asia-southeast1.firebasedatabase.app")
//                .getReference("users")
//
//        val checkUsername: Query = reference.orderByChild("phoneno").equalTo(login_phoneno)

//        checkUsername.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
////                Log.d("LOGIN",reference.child.equalTo(login_phoneno).toString())
//
//                if (snapshot.exists()) {
//                    phoneno!!.error = null
//                    phoneno!!.isErrorEnabled = false
//                    val database_password: String? =
//                        snapshot.child(login_phoneno).child("password").getValue(
//                            String::class.java
//                        )
//                    Log.d("LOGIN",database_password.toString())
//                    if (database_password == login_password) {
//                        password?.setError(null)
//                        password?.setErrorEnabled(false)
//                        AutoSave(login_phoneno, login_password)
//                        val intent = Intent(this@LoginPage, Home_Main::class.java)
//                        intent.putExtra("phoneno", login_phoneno)
//                        intent.putExtra("startingpage", "1")
//                        startActivity(intent)
//                        finish()
//                    } else {
//                        password?.setError("Incorrect Password")
//                        password?.requestFocus()
//                    }
//                } else {
//                    phoneno!!.error = "User does not exists"
//                    phoneno!!.requestFocus()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {}
//        })
        }

    }


}
