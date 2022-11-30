package com.example.managesoft.activities.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.managesoft.R
import com.example.managesoft.databinding.ActivitySignInBinding
import com.example.managesoft.firebase.FirestoreClass
import com.example.managesoft.model.User
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {

    var binding : ActivitySignInBinding ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setSupportActionBar(binding?.toolbarsignin)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Sign In"
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_back_24dp)
        }

        binding?.toolbarsignin?.setNavigationOnClickListener{
            onBackPressed()
        }

        binding?.btnSignIn?.setOnClickListener{
            signInRegistered()
        }

    }

    fun signInSuccess(user : User){
        hideProgressDialog()
        startActivity(Intent(this , MainActivity::class.java))
        finish()
    }

    private fun signInRegistered(){
        val email : String = binding?.etEmail?.text.toString().trim{it<=' '}
        val password : String = binding?.etPass?.text.toString().trim{it<=' '}
        if (validateForm(email,password)){
            showProgressDialog("Please Wait..")
            val auth : FirebaseAuth = FirebaseAuth.getInstance()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Sign In", "createUserWithEmail:success")
                        FirestoreClass().loadUserData(this@SignInActivity)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sign In", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateForm(email : String , password  :String ): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter the email address")
                false
            }

            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter the password")
                false
            }
            else -> {
                true
            }
        }
    }
}