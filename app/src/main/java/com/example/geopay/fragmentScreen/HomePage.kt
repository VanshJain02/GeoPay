package com.example.geopay.fragmentScreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.DynamoDbException
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.geopay.Constants
import com.example.geopay.R
import com.example.geopay.fragmentScreen.miniFrag.fragment_mini_home1
import com.example.geopay.fragmentScreen.miniFrag.fragment_mini_home2
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.system.exitProcess
import com.android.volley.Response;
import com.example.geopay.fragmentScreen.miniFrag.home_myoffers_recycler_adapter
import kotlinx.coroutines.*
import java.lang.Runnable


class HomePage : Fragment() {

    private var minihome1 = fragment_mini_home1()
    private var minihome2 = fragment_mini_home2()

    private var currentSelectItemId = R.id.miHome
    private var savedStateSparseArray = SparseArray<SavedState>()


    private var PERMISSION_LOCATION=1



    private lateinit var locationManager: LocationManager

    var mFusedLocationClient: FusedLocationProviderClient? = null
    var lat=""
    var lon=""


    var firebase: FirebaseFirestore = FirebaseFirestore.getInstance()

    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference

    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 180000


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment



        val view =  inflater.inflate(R.layout.fragment_home_page, container, false)

        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())

            mFusedLocationClient = activity?.let { it1 ->
                LocationServices.getFusedLocationProviderClient(
                    it1
                )
            }
            getLastLocation()

            sendAutoLocation()

        }.also { runnable = it }, delay.toLong())

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference


        val carousel: ImageCarousel = view.findViewById(R.id.carousel)

        // Image drawable with caption
        carousel.registerLifecycle(lifecycle)


        val list = mutableListOf<CarouselItem>()
        list.add(
            CarouselItem(
                imageDrawable = R.drawable.home_carousel1
            )
        )
        list.add(
            CarouselItem(
                imageDrawable = R.drawable.home_carousel2
            )
        )
        list.add(
            CarouselItem(
                imageDrawable = R.drawable.offer_3
            )
        )

        carousel.setData(list)


        if(allPermissionGranted()) {
            mFusedLocationClient = activity?.let { it1 ->
                LocationServices.getFusedLocationProviderClient(
                    it1
                )
            }
        }
        else{
            Log.d("LOC",lon.toString()+"  "+lat.toString())

            Toast.makeText(activity,"REQUESTING PERMissions",Toast.LENGTH_SHORT)
            requestPermissions( Constants.REQUIRED_PERMISSIONS, Constants.REQUEST_CODE_PERMISSIONS)
        }


            makeCurrentFragment(minihome1,1)

        view.findViewById<Button>(R.id.btnAllOffers).setOnClickListener{
            if(currentSelectItemId!=1){
            makeCurrentFragment(minihome1,1)}
        }
        Log.d("LOC",lon.toString()+"  "+lat.toString())

        view.findViewById<Button>(R.id.btnMyOffers).setOnClickListener{



            if(allPermissionGranted()){
                mFusedLocationClient = activity?.let { it1 ->
                    LocationServices.getFusedLocationProviderClient(
                        it1
                    )
                }

                getLastLocation()
                Log.d("LOC",lon.toString()+"  "+lat.toString())
                if(currentSelectItemId!=2) {
                    makeCurrentFragment(minihome2, 2)
                }
            }
            else{
                Log.d("LOC",lon.toString()+"  "+lat.toString())

                Toast.makeText(activity,"REQUESTING permissions",Toast.LENGTH_SHORT)
                requestPermissions( Constants.REQUIRED_PERMISSIONS, Constants.REQUEST_CODE_PERMISSIONS)
            }

        }





        return view


    }

    private fun makeCurrentFragment(Fragment: Fragment, @IdRes actionId: Int) {
        val currentFragment = childFragmentManager.findFragmentById(R.id.homefrag_bottom_frame)
        if (currentFragment != null) {
            savedStateSparseArray.put(currentSelectItemId,
                childFragmentManager.saveFragmentInstanceState(currentFragment)
            )
        }
        currentSelectItemId = actionId
        Fragment.setInitialSavedState(savedStateSparseArray[actionId])
        childFragmentManager.beginTransaction().apply {
            replace(R.id.homefrag_bottom_frame,Fragment)
            commit()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.REQUEST_CODE_PERMISSIONS) {
            if (allPermissionGranted()) {
                getLastLocation()

            } else {
                Toast.makeText(activity,"Permission not granted", Toast.LENGTH_SHORT).show()

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    // method to check
    // if location is enabled

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        var location: Location
        // check if permissions are given
        if (allPermissionGranted()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient?.lastLocation?.addOnCompleteListener(OnCompleteListener { task->

                    if (task.result == null) {

                        requestNewLocationData()
                    }
                    else{

                        location = task.result


                        lat = location.latitude.toString()
                        lon = location.longitude.toString()
//
                        Log.d("LAtitude",location.latitude.toString())
                        Log.d("Longitude",location.longitude.toString())
                    }

                })?.addOnFailureListener(OnFailureListener {
                    Log.d("error",it.toString())

                })
                } else {
                Toast.makeText(activity, "Please turn on" + " your location...", Toast.LENGTH_LONG)
                    .show()
                val intent: Intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions(Constants.REQUIRED_PERMISSIONS,Constants.REQUEST_CODE_PERMISSIONS)
        }
    }
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        // Initializing LocationRequest
        Log.d("LAtitude","insidenewlocation")

        // object with appropriate methods
        val mLocationRequest = LocationRequest()
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(10000)
        mLocationRequest.setFastestInterval(1000)
        mLocationRequest.setNumUpdates(1)

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }


        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }
    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            Log.d("LAtitude","callback")

            lat = mLastLocation?.latitude.toString()
            lon = mLastLocation?.longitude.toString()
            Log.d("LAtitude",mLastLocation?.latitude.toString())
            Log.d("Longitude",mLastLocation?.longitude.toString())
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun allPermissionGranted() =
        Constants.REQUIRED_PERMISSIONS.all {
            activity?.let { it1 ->
                ContextCompat.checkSelfPermission(
                    it1, it
                )
            } == PackageManager.PERMISSION_GRANTED
        }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendAutoLocation()= runBlocking{
        val staticCredentials = StaticCredentialsProvider {
            accessKeyId = "AKIAW36QAMHQM4X5322A"
            secretAccessKey = "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"
        }
        val sharedPreferences = activity?.getSharedPreferences("shared preferences",
            AppCompatActivity.MODE_PRIVATE
        )
        val auto_phoneno = sharedPreferences?.getString("phoneno", "")



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
            var url: String  = "https://81hv4ugz0i.execute-api.ap-south-1.amazonaws.com/v1/location"
            mRequestQueue = Volley.newRequestQueue(activity)

            GlobalScope.launch(Dispatchers.Main) {
                delay(3000)
                mStringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String?>() {
                    fun onResponse(response: String) {
                        Toast.makeText(
                            activity,
                            "Response :$response", Toast.LENGTH_LONG
                        ).show() //display the response on screen
                    }
                }, Response.ErrorListener() {
                    fun onErrorResponse(error: VolleyError) {
                        Log.i("ERROR", "Error :$error")
                    }
                })
                mRequestQueue.add(mStringRequest)
            }




        } catch (ex: DynamoDbException) {
            println(ex.message)
            ddb.close()
            exitProcess(0)
        }

    }
    private fun getData() {
        // RequestQueue initialized

        // String Request initialized

    }




}