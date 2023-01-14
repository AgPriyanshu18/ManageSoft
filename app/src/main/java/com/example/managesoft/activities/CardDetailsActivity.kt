package com.example.managesoft.activities

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.managesoft.R
import com.example.managesoft.activities.activities.BaseActivity
import com.example.managesoft.adapters.CardMemberListItemAdapter
import com.example.managesoft.dialogs.LabelColorListDialog
import com.example.managesoft.databinding.ActivityCardDetailsBinding
import com.example.managesoft.dialogs.MemberListDialog
import com.example.managesoft.firebase.FirestoreClass
import com.example.managesoft.model.*
import com.example.managesoft.utils.Constants

class CardDetailsActivity : BaseActivity() {

    var binding : ActivityCardDetailsBinding ?= null
    private lateinit var mBoardDetails : Board
    private var mTaskListPosition = -1
    private var mCardListPosition = -1
    private var mSelectedColor : String = ""
    private var mSelectedMember : User ?= null
    private lateinit var mMembersDetailsList : ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getIntentData()
        setUpActionBar()

        binding?.etNameCardDetails?.setText(mBoardDetails.taskList[mTaskListPosition].
        cards[mCardListPosition].name)

        binding?.etNameCardDetails?.setSelection(binding?.etNameCardDetails?.text.toString().length)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].
        cards[mCardListPosition].color

        if (mSelectedColor.isNotEmpty()){
            setColor()
        }

        binding?.btnUpdateCardDetails?.setOnClickListener {
            if (binding?.etNameCardDetails?.text?.toString()?.isNotEmpty()!!){
                updateCardDetails()
            }else{
                makeToast(this,"Enter a card name")
            }
        }

        binding?.tvSelectLabelColor?.setOnClickListener {
            labelColorListDialog()
        }

        binding?.tvSelectMembers?.setOnClickListener {
            memberListDialog()
        }

        setUpSelectedMembersList()
    }

    private fun setUpActionBar(){

        setSupportActionBar(binding?.toolbarCardDetailsActivity)
        val actionbar = supportActionBar
        if (actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp)
            actionbar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].name
        }
        binding?.toolbarCardDetailsActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun getIntentData(){
        if (intent.hasExtra(Constants.BOARD_DETAILS)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAILS)!!
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardListPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailsList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_delete_card->{
                alertDialogForDeleteCard(mBoardDetails.
                taskList[mTaskListPosition].cards[mCardListPosition].name)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun addUpdateTaskListSuccess(){
         hideProgressDialog()

        setResult(Activity.RESULT_OK)

        finish()
    }

    private fun updateCardDetails(){
        val card = Card(
            binding?.etNameCardDetails?.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo,
            mSelectedColor
        )
        mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition] = card

        val taskList : ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        showProgressDialog("Changing..")
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }

    private fun deleteCard(){
        val cardList : ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards

        cardList.removeAt(mCardListPosition)

        val taskList : ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        taskList[mTaskListPosition].cards = cardList
        showProgressDialog("Changing..")
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)

    }

    private fun alertDialogForDeleteCard(cardName : String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage(resources.getString(R.string.confirmation_message_to_delete_card,cardName))
        builder.setIcon(R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){ dialogInterface, _ ->
            dialogInterface.dismiss()

            deleteCard()
        }
        builder.setNegativeButton("No"){ dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog : AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun colorList():ArrayList<String>{
        val colorList : ArrayList<String> = ArrayList()
        colorList.add("#43C86F")
        colorList.add("#0C90F1")
        colorList.add("#F72400")
        colorList.add("#7A8089")
        colorList.add("#D57C1D")
        colorList.add("#770000")
        colorList.add("#0022F8")
        return colorList
    }

    private fun setColor(){
        binding?.tvSelectLabelColor?.text = ""
        binding?.tvSelectLabelColor?.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun labelColorListDialog(){
        val colorsList : ArrayList<String> = colorList()

        val listDialog = object : LabelColorListDialog(
            this,colorsList,resources.getString(R.string.str_select_label_color),
        mSelectedColor){
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }
        listDialog.show()
    }

    private fun memberListDialog(){
        var cardAssignedMemberList = mBoardDetails.taskList[mTaskListPosition].
        cards[mCardListPosition].assignedTo

        if (cardAssignedMemberList.size > 0){
            for (i in mMembersDetailsList.indices){
                for (j in cardAssignedMemberList){
                    if (mMembersDetailsList[i].id == j){
                        mMembersDetailsList[i].selected = true
                    }
                }
            }
        }else{
            for (i in mMembersDetailsList.indices){
                mMembersDetailsList[i].selected = false
            }
        }

        val listDialog = object : MemberListDialog(
            this,mMembersDetailsList,resources.getString(R.string.str_select_member)
        ){
            override fun onItemSelected(user: User, action: String) {
                if (action == Constants.SELECT){
                    if (!mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition]
                            .assignedTo.contains(user.id)){
                        mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition]
                            .assignedTo.add(user.id)
                    }
                }else{
                    mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition]
                        .assignedTo.remove(user.id)

                    for (i in mMembersDetailsList.indices){
                        if (mMembersDetailsList[i].id == user.id){
                            mMembersDetailsList[i].selected = false
                        }
                    }
                }
                setUpSelectedMembersList()
            }

        }
        listDialog.show()
    }

    private fun setUpSelectedMembersList(){
        val cardAssignedMemberList = mBoardDetails.taskList[mTaskListPosition].
        cards[mCardListPosition].assignedTo

        val selectedMembersList : ArrayList<SelectedMembers> = ArrayList()

        for (i in mMembersDetailsList.indices){
            for (j in cardAssignedMemberList){
                if (mMembersDetailsList[i].id == j){
                    val selectedMember = SelectedMembers(
                        mMembersDetailsList[i].id,
                        mMembersDetailsList[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if (selectedMembersList.size > 0){
            selectedMembersList.add(SelectedMembers("",""))
            binding?.tvSelectMembers?.visibility = View.GONE
            binding?.rvSelectedMembersList?.visibility = View.VISIBLE

            binding?.rvSelectedMembersList?.layoutManager = GridLayoutManager(this, 6)

            val adapter = CardMemberListItemAdapter(this, selectedMembersList,false)

            binding?.rvSelectedMembersList?.adapter = adapter

            adapter.setOnClickListener(object : CardMemberListItemAdapter.OnClickListener{
                override fun onClick() {
                    memberListDialog()
            }
        })
    }else{
        binding?.tvSelectMembers?.visibility = View.VISIBLE
            binding?.rvSelectedMembersList?.visibility = View.GONE
        }
    }
}