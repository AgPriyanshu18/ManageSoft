package com.example.managesoft.activities.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.managesoft.R
import com.example.managesoft.databinding.ActivitySignUpBinding
import com.example.managesoft.firebase.FirestoreClass
import com.example.managesoft.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {

    var binding : ActivitySignUpBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setSupportActionBar(binding?.toolbarsignup)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Sign Up"
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_back_24dp)
        }

        binding?.toolbarsignup?.setNavigationOnClickListener{
            onBackPressed()
        }

        binding?.buttonSubmitSignup?.setOnClickListener{
            registerUser()
        }
    }

    fun userRegisteredSuccess(){
        Toast.makeText(this,"You have registered",Toast.LENGTH_SHORT).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun registerUser(){
        val name : String  = binding?.formIpName?.text.toString().trim{it <= ' '}
        val email : String = binding?.formIpEmail?.text.toString().trim{it<=' '}
        val password : String = binding?.formIpPass?.text.toString().trim{it<=' '}

        if (validateForm(name,email,password)){
            showProgressDialog("Please wait..")
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser? = task.result!!.user
                        val registeredEmail = firebaseUser?.email!!
                        val user = User(firebaseUser.uid,name,registeredEmail)
                        FirestoreClass().registerUser(this,user)
                    } else {
                        Toast.makeText(this,
                            "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateForm(name :String , email : String , password  :String ): Boolean {
        return when{
            TextUtils.isEmpty(name) ->{
                showErrorSnackBar("Please enter the name")
                false
            }

            TextUtils.isEmpty(email) ->{
                showErrorSnackBar("Please enter the email address")
                false
            }

            TextUtils.isEmpty(password) ->{
                showErrorSnackBar("Please enter the password")
                false
            }else->{
                true
            }
        }
    }

}