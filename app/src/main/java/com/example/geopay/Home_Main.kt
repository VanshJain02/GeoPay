package com.example.geopay

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import aws.sdk.kotlin.services.sns.SnsClient
import com.example.geopay.fragmentScreen.HomePage
import com.example.geopay.fragmentScreen.PurchasePage
import com.example.geopay.fragmentScreen.SearchPage
import com.example.geopay.fragmentScreen.SettingsPage
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Home_Main : AppCompatActivity() {

    private var homeFragment = HomePage()
    private var searchFragment = SearchPage()
    private var purchaseFragment = PurchasePage()
    private var settingFragment = SettingsPage()

    private var currentSelectItemId = R.id.miHome
    private var savedStateSparseArray = SparseArray<Fragment.SavedState>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_main)

        val bottom_navigation = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottom_navigation.background = null

        makeCurrentFragment(homeFragment,R.id.miHome)

        FirebaseMessaging.getInstance().token.addOnCompleteListener (OnCompleteListener { task ->
            if(!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task. result

//
        })

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.miHome -> {
                    if(currentSelectItemId!=R.id.miHome) {
                        currentSelectItemId=R.id.miHome
                        makeCurrentFragment(homeFragment, R.id.miHome)
                    }
                }
                R.id.miSearch ->{
                    if(currentSelectItemId!=R.id.miSearch){
                        currentSelectItemId=R.id.miSearch
                        makeCurrentFragment(searchFragment,R.id.miSearch)}
                }
                R.id.miPurchases ->{
                    if(currentSelectItemId!=R.id.miPurchases){
                        currentSelectItemId=R.id.miPurchases
                        makeCurrentFragment(purchaseFragment,R.id.miPurchases)}
                }
                R.id.miSetting ->{
                    if(currentSelectItemId!=R.id.miSetting){
                        currentSelectItemId=R.id.miSetting
                        makeCurrentFragment(settingFragment,R.id.miSetting)}
                }
            }
            true
        }
        CoroutineScope(Dispatchers.Default).apply {
            runBlocking {
                var executor: ExecutorService? = Executors.newSingleThreadExecutor()
                executor?.execute(Runnable {
                    getSpecificItem("vivek")
                })
            }
        }


        findViewById<ImageView>(R.id.mainHome_Notifications).setOnClickListener{
            intent = Intent(this, Notification::class.java)
            startActivity(intent)
        }



    }

    private fun savedFragmentState(actionId: Int) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fl_wrapper)
        if (currentFragment != null) {
            savedStateSparseArray.put(currentSelectItemId,
                supportFragmentManager.saveFragmentInstanceState(currentFragment)
            )
        }
        currentSelectItemId = actionId
    }
    private fun makeCurrentFragment(Fragment: Fragment, @IdRes actionId: Int) {
        savedFragmentState(actionId)
//        createFragment(Fragment,actionId)
        Fragment.setInitialSavedState(savedStateSparseArray[actionId])

        for (i: Int in 1..supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper,Fragment)
            commit()
        }
    }


    fun getSpecificItem(keyVal: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val keyToGet = mutableMapOf<String, AttributeValue>()
            keyToGet["mobile"] = AttributeValue.S("9876543210")
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