package com.example.managesoft.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.managesoft.R
import com.example.managesoft.activities.activities.BaseActivity
import com.example.managesoft.adapters.TaskListItemAdapter
import com.example.managesoft.databinding.ActivityTaskListBinding
import com.example.managesoft.firebase.FirestoreClass
import com.example.managesoft.model.Board
import com.example.managesoft.model.Card
import com.example.managesoft.model.Task
import com.example.managesoft.utils.Constants
import java.text.FieldPosition

class TaskListActivity : BaseActivity() {

    var binding : ActivityTaskListBinding ?= null
    private lateinit var mBoardDetails : Board
    private lateinit var mBoardDocumentID :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentID = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }
        showProgressDialog("loading..")
        FirestoreClass().getBoardsDetails(this,mBoardDocumentID)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members ->{
                val intent = Intent(this , MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAILS,mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()

        showProgressDialog("Loading.. ")
        FirestoreClass().getBoardsDetails(this,mBoardDetails.documentId)
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

    fun createTaskList(taskListName : String){
        val task = Task(taskListName,FirestoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog("Please Wait...")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun updateTaskList(position : Int, listName : String, model : Task){
        val task = Task(listName,model.createdBy)

        mBoardDetails.taskList[position] = task
        Log.e("Dusra chahkkar","list ka size ye hai ${mBoardDetails.taskList.size}")
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog("Please Wait...")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun deleteTaskList(position : Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        showProgressDialog("Please Wait...")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun addCardToTaskList(position : Int ,cardName : String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        val cardAssignedUserList : ArrayList<String> = ArrayList()
        cardAssignedUserList.add(FirestoreClass().getCurrentUserId())

        val card = Card(cardName,FirestoreClass().getCurrentUserId(),cardAssignedUserList)

        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)

        val task  = Task(mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,cardsList)

        mBoardDetails.taskList[position] = task

        showProgressDialog("Please wait...")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == MEMBERS_REQUEST_CODE){
                showProgressDialog("Loading.. ")
                FirestoreClass().getBoardsDetails(this,mBoardDocumentID)
            }else{
                Log.e("Cancelled","Cancelled")
            }
        }
    }

    fun cardDetails(taskListPosition: Int, cardPosition : Int){
        var intent = Intent(this,CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAILS,mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardPosition)
        startActivity(intent)
    }

    companion object {
        const val MEMBERS_REQUEST_CODE : Int = 13
    }

}