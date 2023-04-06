package com.example.geopay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LoginPage : AppCompatActivity() {

    var phoneno: TextInputLayout? = null
    var password:TextInputLayout? = null
    var login: Button? = null
    var login_forgetpassword:android.widget.Button? = null
    var login_signup:android.widget.Button? = null
    var autologin = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_login_page)
        //HOOKS
        phoneno = findViewById(R.id.login_phoneno)
        password = findViewById<TextInputLayout>(R.id.login_password)
        login = findViewById(R.id.login_login)
        login_forgetpassword = findViewById<Button>(R.id.login_forgetpassword)
        login_signup = findViewById<Button>(R.id.login_signup)
        findViewById<Button>(R.id.login_login).setOnClickListener(View.OnClickListener { v -> loginUser(v) })
        findViewById<Button>(R.id.login_signup).setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LoginPage, SignUpPage::class.java)
            startActivity(intent)
            finish()
        })
    }

    private fun AutoSave(save_phoneno: String, save_password: String) {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("phoneno", save_phoneno)
        editor.putString("password", save_password)
        editor.apply()
    }

    fun validatePhoneNo(): Boolean? {
        val `val` = phoneno!!.editText!!.text.toString()
        return if (`val`.isEmpty()) {
            phoneno!!.error = "Field cannot be empty"
            false
        } else if (`val`.length != 10) {
            //Log.d("LENGTH",Integer.toString(val.length()));
            phoneno!!.error = "Incorrect Phone No"
            false
        } else {
            phoneno!!.error = null
            phoneno!!.isErrorEnabled = false
            true
        }
    }


    fun loginUser(view: View?) {
        if (!validatePhoneNo()!!) {
            return
        } else {
            val login_phoneno = phoneno!!.editText!!.text.toString().trim { it <= ' ' }
            val login_password: String =
                password?.getEditText()?.getText().toString().trim { it <= ' ' }
            checkUser(login_phoneno, login_password)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun checkUser(login_phoneno: String, login_password: String) {

        GlobalScope.launch(Dispatchers.IO) {
            val keyToGet = mutableMapOf<String, AttributeValue>()
            keyToGet["mobile"] = AttributeValue.S(login_phoneno)
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
                if(numbersMap?.get("password")?.asS().equals(password?.editText?.text.toString())){
                    val intent = Intent(this@LoginPage, Home_Main::class.java)
                        intent.putExtra("phoneno", login_phoneno)
                        intent.putExtra("startingpage", "1")
                        AutoSave(login_phoneno, login_password)

                         startActivity(intent)
                        finish()
            }
                else{
//                    password?.setError("Incorrect Password")
//                    password?.requestFocus()


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

    fun getSpecificItem(keyVal: String)=
        GlobalScope.launch(Dispatchers.IO) {
            val keyToGet = mutableMapOf<String, AttributeValue>()
            keyToGet["mobile"] = AttributeValue.S(keyVal)
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
            }
        }
    }



}