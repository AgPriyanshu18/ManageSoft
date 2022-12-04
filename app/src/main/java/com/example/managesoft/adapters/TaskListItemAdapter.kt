package com.example.managesoft.adapters

import android.content.Context
import android.content.res.Resources
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.managesoft.R
import com.example.managesoft.activities.TaskListActivity
import com.example.managesoft.model.Task

open class TaskListItemAdapter(private var context: Context,
                               private var list : ArrayList<Task>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    class MyViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var tvAddTaskList: TextView = itemView.findViewById(R.id.tv_add_task_list)
        var llTaskItem: LinearLayout = itemView.findViewById(R.id.ll_task_item)
        var tvTaskListTitle : TextView = itemView.findViewById(R.id.tv_task_list_title)
        var cvAddTaskListName : androidx.cardview.widget.CardView = itemView.findViewById(R.id.cv_add_task_list_name)
        var ibCloseListName : ImageButton = itemView.findViewById(R.id.ib_close_list_name)
        var ibDoneListName : ImageButton = itemView.findViewById(R.id.ib_done_list_name)
        val etTaskListName : EditText = itemView.findViewById(R.id.et_task_list_name)
        val etEditTaskListName : EditText = itemView.findViewById(R.id.et_edit_task_list_name)
        val llTitleView : LinearLayout = itemView.findViewById(R.id.ll_title_view)
        val ibEditListName : ImageButton = itemView.findViewById(R.id.ib_edit_list_name)
        val ibCloseEditableView : ImageButton = itemView.findViewById(R.id.ib_close_editable_view)
        val cvEditTaskListName : androidx.cardview.widget.CardView = itemView.findViewById(R.id.cv_edit_task_list_name)
        val ibDoneEditListName : ImageButton = itemView.findViewById(R.id.ib_done_edit_list_name)
        val ibDeleteList : ImageButton = itemView.findViewById(R.id.ib_delete_list)
        val tvAddCard : TextView = itemView.findViewById(R.id.tv_add_card)
        val cvAddCard : androidx.cardview.widget.CardView = itemView.findViewById(R.id.cv_add_card)
        val ibCloseCardName : ImageButton = itemView.findViewById(R.id.ib_close_card_name)
        val ibDoneCardName : ImageButton = itemView.findViewById(R.id.ib_done_card_name)
        val etCardName : EditText = itemView.findViewById(R.id.et_card_name)
        val rvCardList : RecyclerView = itemView.findViewById(R.id.rv_card_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_task,parent,false)


        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(
            (15.toDp().toPx()),0,(40.toDp()).toPx(),0)
        view.layoutParams = layoutParams
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder){
            if (position == list.size - 1){
                holder.tvAddTaskList.visibility = View.VISIBLE
                holder.llTaskItem.visibility = View.GONE
            }else{
                holder.tvAddTaskList.visibility = View.GONE
                holder.llTaskItem.visibility = View.VISIBLE
            }

            holder.tvTaskListTitle.text = model.title
            holder.tvAddTaskList.setOnClickListener {
                holder.tvAddTaskList.visibility = View.GONE
                holder.cvAddTaskListName.visibility = View.VISIBLE
            }

            holder.ibCloseListName.setOnClickListener{
                holder.tvAddTaskList.visibility = View.VISIBLE
                holder.cvAddTaskListName.visibility = View.GONE
            }

            holder.ibDoneListName.setOnClickListener {
                val listName = holder.etTaskListName.text.toString()
                if (listName.isNotEmpty()){
                    if (context is TaskListActivity){
                        (context as TaskListActivity).createTaskList(listName)
                    }else{
                        Toast.makeText(context,"Please enter the list name",
                             Toast.LENGTH_SHORT).show()
                    }
                }
            }

            holder.ibEditListName.setOnClickListener {
                holder.etEditTaskListName.setText(model.title)
                holder.llTitleView.visibility = View.GONE
                holder.cvEditTaskListName.visibility = View.VISIBLE
            }

            holder.ibCloseEditableView.setOnClickListener{
                holder.llTitleView.visibility = View.VISIBLE
                holder.cvEditTaskListName.visibility = View.GONE
            }

            holder.ibDoneEditListName.setOnClickListener {
                val listName = holder.etEditTaskListName.text.toString()
                if (listName.isNotEmpty()){
                    if (context is TaskListActivity){
                        Log.e("ye chakkar hai shayad","position is $position")
                        (context as TaskListActivity).updateTaskList(position,listName,model)
                    }else{
                        Toast.makeText(context,"Please enter the list name",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }

            holder.ibDeleteList.setOnClickListener {
                alertDialogForDeleteList(position,model.title)
            }

            holder.tvAddCard.setOnClickListener {
                holder.tvAddCard.visibility = View.GONE
                holder.cvAddCard.visibility = View.VISIBLE
            }

            holder.ibCloseCardName.setOnClickListener {
                holder.tvAddCard.visibility = View.VISIBLE
                holder.cvAddCard.visibility = View.GONE
            }

            holder.ibDoneCardName.setOnClickListener {
                val cardName = holder.etCardName.text.toString()
                if (cardName.isNotEmpty()){
                    if (context is TaskListActivity){
                        (context as TaskListActivity).addCardToTaskList(position,cardName)
                    }else{
                        Toast.makeText(context,"Please enter the card name",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
            holder.rvCardList.layoutManager = LinearLayoutManager(context)
            holder.rvCardList.setHasFixedSize(true)
            val adapter = CardListItemsAdapter(context,model.cards)
            holder.rvCardList.adapter = adapter
            adapter.setOnClickListener(
                object : CardListItemsAdapter.OnClickListener{
                    override fun onClick(cardPosition: Int) {
                        if (context is TaskListActivity){
                            (context as TaskListActivity).cardDetails(holder.adapterPosition, cardPosition )
                        }
                    }
                }
            )
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }

    private fun Int.toDp() : Int = (this/Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx() : Int = (this*Resources.getSystem().displayMetrics.density).toInt()

    private fun alertDialogForDeleteList(position: Int , title : String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure want to delete $title.")
        builder.setIcon(R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){dialogInterface,whih ->
            dialogInterface.dismiss()

            if (context is TaskListActivity){
                (context as TaskListActivity).deleteTaskList(position)
            }
        }

        builder.setNegativeButton("No"){dialogInterface,which->
            dialogInterface.dismiss()
        }

        val alertDialog : AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}