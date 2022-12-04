package com.example.managesoft.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.managesoft.R
import com.example.managesoft.activities.activities.BaseActivity
import com.example.managesoft.adapters.MembersListItemAdapter
import com.example.managesoft.databinding.ActivityMembersBinding
import com.example.managesoft.firebase.FirestoreClass
import com.example.managesoft.model.Board
import com.example.managesoft.model.User
import com.example.managesoft.utils.Constants

class MembersActivity : BaseActivity() {

    var binding : ActivityMembersBinding ?= null
    private lateinit var mBoardDetails : Board
    private lateinit var mAssignedToMembersList : ArrayList<User>
    private var anyChangesMade : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (intent.hasExtra(Constants.BOARD_DETAILS)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAILS)!!
        }

        setUpActionBar()

        showProgressDialog("Loading The List..")
        FirestoreClass().getAssignedMembersListDetails(this@MembersActivity,mBoardDetails.assignedTo)
    }

    private fun setUpActionBar(){

        setSupportActionBar(binding?.toolbarMembersActivity)
        val actionbar = supportActionBar
        if (actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp)
            actionbar.title = resources.getString(R.string.members)
        }
        binding?.toolbarMembersActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    fun  setUpMembersList(list : ArrayList<User>){
        hideProgressDialog()
        mAssignedToMembersList = list
        binding?.rvMembersList?.layoutManager = LinearLayoutManager(this)
        binding?.rvMembersList?.setHasFixedSize(true)

        val adapter = MembersListItemAdapter(this,list)
        binding?.rvMembersList?.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_add_member->{
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
            val email = dialog.findViewById<androidx.appcompat.widget.AppCompatEditText>(R.id.et_email_add_member)
                .text.toString()

            if (email != null) {
                if (email.isNotEmpty()){
                    showProgressDialog("Loading..")
                    FirestoreClass().getMemberDetails(this,email)
                    dialog.dismiss()
                }else{
                    Toast.makeText(this@MembersActivity,
                        "Field cannot be empty",Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun memberDetails(user:User){
        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this,mBoardDetails,user)
    }

    fun memberAssignSuccess(user : User){
        hideProgressDialog()
        mAssignedToMembersList.add(user)
        anyChangesMade = true
        setUpMembersList(mAssignedToMembersList)
    }
}