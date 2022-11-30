package com.example.managesoft.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.managesoft.activities.activities.MainActivity
import com.example.managesoft.activities.activities.introActivity
import com.example.managesoft.databinding.ActivitySplashBinding
import com.example.managesoft.firebase.FirestoreClass

class splashActivity : AppCompatActivity() {
    var binding : ActivitySplashBinding ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val typeface : Typeface = Typeface.createFromAsset(assets,"carbon bl.ttf")
        binding?.tvAppname?.typeface = typeface


        Handler().postDelayed({

            var currentUserID = FirestoreClass().getCurrentUserId()

            if (currentUserID.isNotEmpty()){
                startActivity(Intent(this,MainActivity::class.java))
            }else{
                startActivity(Intent(this, introActivity::class.java))
            }
            finish()
        },2500)
    }
}