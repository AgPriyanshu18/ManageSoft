package com.example.managesoft.activities.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.example.managesoft.databinding.ActivityIntroBinding

class introActivity : BaseActivity() {

    var binding : ActivityIntroBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding?.btnSignUpIntro?.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding?.btnSignInIntro?.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}