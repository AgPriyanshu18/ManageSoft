 package com.example.managesoft.activities.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.managesoft.R
import com.example.managesoft.activities.ProfileActivity
import com.example.managesoft.activities.TaskListActivity
import com.example.managesoft.activities.createBoardActivity
import com.example.managesoft.adapters.BoardItemsAdapter
import com.example.managesoft.databinding.ActivityMainBinding
import com.example.managesoft.firebase.FirestoreClass
import com.example.managesoft.model.Board
import com.example.managesoft.model.User
import com.example.managesoft.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

 class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener{

    var binding : ActivityMainBinding ?= null
     private lateinit var mUserName : String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        binding?.navView?.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this,true)

        findViewById<FloatingActionButton>(R.id.fab_create_board).setOnClickListener{
            val intent = Intent(this, createBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            startActivityForResult(intent, CREATE_BOARD_UPDATE)
        }
    }

     // learning


     fun boardsListInUI(boardList : ArrayList<Board>){
         hideProgressDialog()
         var rvBoardList = findViewById<RecyclerView>(R.id.RV_Boards_list)
         var noBoardList = findViewById<TextView>(R.id.tv_noBoards_available)
         if (boardList.size>0){
             rvBoardList.visibility = View.VISIBLE
             noBoardList.visibility = View.GONE
             rvBoardList.layoutManager = LinearLayoutManager(this)
             rvBoardList.setHasFixedSize(true)

             val adapter = BoardItemsAdapter(this,boardList)
             rvBoardList.adapter = adapter

             adapter.setOnClickListener(object: BoardItemsAdapter.OnClickListener{
                 override fun onClick(position: Int, modal: Board) {
                     var intent = Intent(this@MainActivity,TaskListActivity::class.java)
                     intent.putExtra(Constants.DOCUMENT_ID,modal.documentId)
                     startActivity(intent)

                 }
             })

         }else{
             rvBoardList.visibility = View.GONE
             noBoardList.visibility = View.VISIBLE
         }
     }

//To check about pushing

     private fun setupActionBar(){
         setSupportActionBar(findViewById(R.id.toolbar_main_activity))
         findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main_activity)
             .setNavigationIcon(R.drawable.ic_action_navigation_menu)
         findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main_activity).setNavigationOnClickListener{
             toggleDrawer()
         }
     }

     private fun toggleDrawer(){
         if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)){
             binding?.drawerLayout!!.closeDrawer((GravityCompat.START))
         }else{
             binding?.drawerLayout!!.openDrawer(GravityCompat.START)
         }
     }

     override fun onBackPressed() {
         if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)){
             binding?.drawerLayout!!.closeDrawer((GravityCompat.START))
         }else{
             doubleBackToExit()
         }
     }

     fun updateNavigationUserDetails (user: User,readBoardList : Boolean){

         mUserName = user.name

         Glide.with(this).load(user.image).centerCrop()
             .placeholder(R.drawable.ic_user_place_holder )
             .into(findViewById(R.id.nav_user_image))

         findViewById<TextView>(R.id.nav_username).text = user.name

         if (readBoardList){
             showProgressDialog("Loading..")
             FirestoreClass().getBoardsList(this)

         }
     }

     override fun onNavigationItemSelected(item: MenuItem): Boolean {
         when(item.itemId){
             R.id.nav_myprofile->{
                 startActivityForResult(Intent(this,ProfileActivity::class.java), UPDATED_USER_PROFILE_DATA)
             }

             R.id.nav_signout->{
                 FirebaseAuth.getInstance().signOut()
                 val intent = Intent(this,introActivity::class.java)
                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                 startActivity(intent)
                 finish()
             }
         }
         binding?.drawerLayout?.closeDrawer(GravityCompat.START)
         return true
     }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if (resultCode == Activity.RESULT_OK){
             if (requestCode == UPDATED_USER_PROFILE_DATA){
                 FirestoreClass().loadUserData(this)
             }else if (requestCode == CREATE_BOARD_UPDATE){
                 FirestoreClass().getBoardsList(this)
             }else{
                 Log.e("Cancelled" , "Activity to get data from profile get cancelled")
             }
         }
     }

     companion object {
        private const val UPDATED_USER_PROFILE_DATA : Int = 11
         private const val CREATE_BOARD_UPDATE : Int = 12
     }

 }