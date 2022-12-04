package com.example.managesoft.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.managesoft.R
import com.example.managesoft.databinding.ActivityCardDetailsBinding
import com.example.managesoft.model.Board
import com.example.managesoft.utils.Constants

class CardDetailsActivity : AppCompatActivity() {

    var binding : ActivityCardDetailsBinding ?= null
    private lateinit var mBoardDetails : Board
    private var mTaskListPosition = -1
    private var mCardListPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getIntentData()
        setUpActionBar()

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
    }

}