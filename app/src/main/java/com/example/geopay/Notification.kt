package com.example.geopay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Notification : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        supportActionBar?.hide()

    }
}