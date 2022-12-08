package com.example.managesoft.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.managesoft.R
import com.example.managesoft.activities.TaskListActivity
import com.example.managesoft.model.Card
import com.example.managesoft.model.SelectedMembers

open class CardListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Card>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvCardName : TextView = itemView.findViewById(R.id.tv_card_name)
        val viewLabelColor : View = itemView.findViewById(R.id.view_label_color)
        val rvCardSelectedMemberList : RecyclerView = itemView.findViewById(R.id.rv_card_selected_member_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card,
                parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            if (model.color.isNotEmpty()){
                holder.viewLabelColor.visibility = View.VISIBLE
                holder.viewLabelColor.setBackgroundColor(Color.parseColor(model.color))
            }else{
                holder.viewLabelColor.visibility = View.GONE
            }

            holder.tvCardName.text = model.name

            if ((context as TaskListActivity).mAssignedMembersDetailsList.size>0){
                val selectedMembersList : ArrayList<SelectedMembers> = ArrayList()

                for (i in context.mAssignedMembersDetailsList.indices){
                    for (j in model.assignedTo){
                        if (context.mAssignedMembersDetailsList[i].id == j){
                            val selectedMembers = SelectedMembers(
                                context.mAssignedMembersDetailsList[i].id,
                                context.mAssignedMembersDetailsList[i].image
                            )
                            selectedMembersList.add(selectedMembers)
                        }
                    }
                }

                if (selectedMembersList.size>0){
                    if (selectedMembersList.size == 1 && selectedMembersList[0].id==model.createdBy){
                        holder.rvCardSelectedMemberList.visibility = View.GONE
                    }else{
                        holder.rvCardSelectedMemberList.visibility = View.VISIBLE

                        holder.rvCardSelectedMemberList.layoutManager = GridLayoutManager(context,4)
                        val adapter = CardMemberListItemAdapter(context,selectedMembersList,false)
                        holder.rvCardSelectedMemberList.adapter = adapter

                        adapter!!.setOnClickListener(object  : CardMemberListItemAdapter.OnClickListener{
                            override fun onClick() {
                                if (onClickListener!=null){
                                    onClickListener!!.onClick(position)
                                }
                            }
                        })
                    }
                }else{
                    holder.rvCardSelectedMemberList.visibility = View.GONE
                }
            }

            holder.tvCardName.setOnClickListener {
                if (onClickListener!=null){
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }


}