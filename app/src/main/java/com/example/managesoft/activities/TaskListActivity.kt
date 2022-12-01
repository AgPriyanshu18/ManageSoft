package com.example.managesoft.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.managesoft.R
import com.example.managesoft.activities.activities.BaseActivity
import com.example.managesoft.adapters.TaskListItemAdapter
import com.example.managesoft.databinding.ActivityTaskListBinding
import com.example.managesoft.firebase.FirestoreClass
import com.example.managesoft.model.Board
import com.example.managesoft.model.Task
import com.example.managesoft.utils.Constants

class TaskListActivity : BaseActivity() {

    var binding : ActivityTaskListBinding ?= null
    private lateinit var mBoardDetails : Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        var boardDocumentId = ""

        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }
        showProgressDialog("loading..")
        FirestoreClass().getBoardsDeatails(this,boardDocumentId)
    }

    private fun setUpActionBar(){

        setSupportActionBar(binding?.toolbarTaskListActivity)
        val actionbar = supportActionBar
        if (actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp)
            actionbar.title = mBoardDetails.name
        }
        binding?.toolbarTaskListActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun boardDetails(board : Board){
        mBoardDetails = board
        hideProgressDialog()
        setUpActionBar()


        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)
        binding?.rvTaskList?.layoutManager = LinearLayoutManager(this,
        RecyclerView.HORIZONTAL,false)
        binding?.rvTaskList?.setHasFixedSize(true)

        val  adapter = TaskListItemAdapter(this,board.taskList)

        binding?.rvTaskList?.adapter = adapter
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()

        showProgressDialog("Loading.. ")
        FirestoreClass().getBoardsDeatails(this,mBoardDetails.documentId)
    }

    fun createTaskList(taskListName : String){
        val task = Task(taskListName,FirestoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog("Please Wait...")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

}