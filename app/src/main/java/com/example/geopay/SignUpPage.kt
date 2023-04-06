package com.example.geopay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SignUpPage : AppCompatActivity() {
    var fullname: TextInputLayout? = null
    var username:TextInputLayout? = null
    var phoneno:TextInputLayout? = null
    var password:TextInputLayout? = null
    var signup: Button? = null
    var signup_login:android.widget.Button? = null

    var rootNode: FirebaseDatabase? = null
    var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_page)

        fullname = findViewById<TextInputLayout>(R.id.signup_fullname)
        username = findViewById<TextInputLayout>(R.id.signup_username)
        phoneno = findViewById<TextInputLayout>(R.id.signup_phone)
        password = findViewById<TextInputLayout>(R.id.signup_password)
        signup = findViewById<Button>(R.id.signup_signup)
        signup_login = findViewById<Button>(R.id.signup_login)
        findViewById<Button>(R.id.signup_signup).setOnClickListener(View.OnClickListener { v ->
            rootNode =
                FirebaseDatabase.getInstance("https://geopay-627c0-default-rtdb.asia-southeast1.firebasedatabase.app")
            reference = rootNode!!.getReference("users")
            registerUser(v)

            Log.d("TAG",findViewById<TextInputLayout>(R.id.signup_fullname).editText?.text.toString())

            var executor: ExecutorService? = Executors.newSingleThreadExecutor()
            executor?.execute(Runnable{
                submitData((findViewById<TextInputLayout>(R.id.signup_fullname).editText?.text.toString()),findViewById<TextInputLayout>(R.id.signup_username).editText?.text.toString(),findViewById<TextInputLayout>(R.id.signup_phone).editText?.text.toString(),findViewById<TextInputLayout>(R.id.signup_password).editText?.text.toString())

            })

        })

        findViewById<Button>(R.id.signup_login).setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SignUpPage, LoginPage::class.java)
            startActivity(intent)
            finish()
        })
    }





    fun registerUser(view: View?) {
        val regname = fullname!!.editText!!.text.toString()
        val regusername = username!!.editText!!.text.toString().lowercase()
        val regphoneno = phoneno!!.editText!!.text.toString().lowercase()
        val regpassword = password!!.editText!!.text.toString()

        val checkPhoneno = reference!!.orderByChild("phoneno").equalTo(regphoneno)
        checkPhoneno.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    phoneno!!.error = "Phone No already in use"
                    phoneno!!.requestFocus()
                    username!!.error = null
                    username!!.isErrorEnabled = false
                } else {
                    phoneno!!.error = null
                    phoneno!!.isErrorEnabled = false
                    val checkUsername = reference!!.orderByChild("username").equalTo(regusername)
                    checkUsername.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                username!!.error = "Username already exists"
                                username!!.requestFocus()
                            } else {
                                username!!.error = null
                                username!!.isErrorEnabled = false
                                val user_signup = user_signup(regname, regpassword, regusername, regphoneno)
                                reference!!.child(regphoneno).setValue(user_signup)
                                val intent = Intent(this@SignUpPage, Home_Main::class.java)
                                intent.putExtra("phoneno", regphoneno)
                                intent.putExtra("startingpage", "1")
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun submitData(NameVal: String,emailVal: String,number: String,passwor: String) = runBlocking{



        val data = Database()

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
        val tableName = "Users"
        val name = "name"
        val email = "email"
        val mobile = "mobile"
        val password = "password"
        Log.d("TAG","WORKING")
        AutoSave(number,passwor)

        data.putItemInTable2(ddb, tableName, name, NameVal, email, emailVal,mobile,number,password,passwor)
//        showToast("Item added")

        // Notify user.
//        val snsClient = SnsClient{
//            region = "ap-south-1"
//            credentialsProvider = staticCredentials
//        }

//        val sendMSG = SendMessage()
//        val mobileNum = "7021684250"
//        val message = "Item $uuid was added!"
//        sendMSG.pubTextSMS( snsClient,message, mobileNum )
    }

    private fun AutoSave(save_phoneno: String, save_password: String) {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("phoneno", save_phoneno)
        editor.putString("password", save_password)
        editor.apply()
    }


}