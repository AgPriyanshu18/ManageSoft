package com.example.managesoft.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.managesoft.R
import com.example.managesoft.activities.TaskListActivity
import com.example.managesoft.databinding.ActivityTaskListBinding
import com.example.managesoft.databinding.ItemBoardBinding
import com.example.managesoft.databinding.ItemTaskBinding
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
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }

    private fun Int.toDp() : Int = (this/Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx() : Int = (this*Resources.getSystem().displayMetrics.density).toInt()


}