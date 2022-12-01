package com.example.managesoft.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.managesoft.R
import com.example.managesoft.activities.activities.BaseActivity
import com.example.managesoft.databinding.ActivityCreateBoardBinding
import com.example.managesoft.firebase.FirestoreClass
import com.example.managesoft.model.Board
import com.example.managesoft.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class createBoardActivity : BaseActivity() {

    var binding : ActivityCreateBoardBinding ?= null
    private var mSelectedImageFileUri: Uri?= null
    private var mBoardImageURI : String = ""
    private lateinit var mUserName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }

        setUpActionBar()
        binding?.ivBoardImage?.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this@createBoardActivity)
            }else{
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding?.btnCreate?.setOnClickListener {
            if (mSelectedImageFileUri != null){
                uploadBoardImage()
            }else{
                registerBoard()
            }
        }

    }

    private fun registerBoard(){
        val boardName = binding?.etBoardName?.text.toString().trim{it <= ' '}
        val assignedUsers : ArrayList<String> = ArrayList()
        assignedUsers.add(getCurrentUserId())
        var boardInfo = Board(boardName,mBoardImageURI!!,mUserName, assignedUsers)
        showProgressDialog("Creating...")
        FirestoreClass().createBoard(this@createBoardActivity,boardInfo!!)
    }

    private fun updateBoard(){
        val boardName = binding?.etBoardName?.text.toString().trim{it <= ' '}

        val boardHashMap = HashMap<String , Any>()

        if (mBoardImageURI!=null && mBoardImageURI!!.isNotEmpty() ){
            boardHashMap[Constants.IMAGE] = mBoardImageURI!!
        }

        if (boardName.isNotEmpty()){
            boardHashMap[Constants.NAME] = boardName
        }

        if (mUserName.isNotEmpty()){
            boardHashMap[Constants.CREATED_BY] = mUserName
        }

        boardHashMap[Constants.ASSIGNED_TO] = arrayListOf(mUserName)


        FirestoreClass().updateBoardData(this@createBoardActivity,boardHashMap)
    }

    private fun uploadBoardImage(){
        showProgressDialog("Uploading")

        if (mSelectedImageFileUri != null){
            val sRef : StorageReference = FirebaseStorage.
            getInstance().reference.child("BOARD_IMAGE" +
                    System.currentTimeMillis() + "." +
                    Constants.getFileExtension(this@createBoardActivity,mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener{
                    taskSnapshot ->
                Log.i("Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.e("Downloadable Image Uri",uri.toString())
                    mBoardImageURI = uri.toString()
                    registerBoard()
                }
            }.addOnFailureListener{
                    exception ->
                Toast.makeText(this@createBoardActivity,
                    exception.message,Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setUpActionBar(){

        setSupportActionBar(binding?.toolbarCreateBoardActivity)
        val actionbar = supportActionBar
        if (actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp)
            actionbar.title = "My Profile"
        }
        binding?.toolbarCreateBoardActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        actionbar?.title = "Create Board"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this@createBoardActivity)
            }else{
                Toast.makeText(this,"you denied permissions for storage you can allow" +
                        "them in settings", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK &&
            requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            mSelectedImageFileUri = data.data
            try {
                Glide.with(this@createBoardActivity).
                load(mSelectedImageFileUri.toString()).centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding?.ivBoardImage!!)
            }catch (e : IOException){
                e.printStackTrace()
            }

        }
    }


}