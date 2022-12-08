package com.example.managesoft.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.managesoft.R
import com.example.managesoft.databinding.ItemBoardBinding
import com.example.managesoft.databinding.ItemCardSelectedMemberBinding
import com.example.managesoft.model.Board
import com.example.managesoft.model.SelectedMembers

open class CardMemberListItemAdapter(private val context : Context,
                                     private val list : ArrayList<SelectedMembers>,
                                     private val assignMembers: Boolean) :
    RecyclerView.Adapter<CardMemberListItemAdapter.ItemCardSelectMemberHolder>() {

    private var onClickListener : OnClickListener?= null

        class ItemCardSelectMemberHolder(binding: ItemCardSelectedMemberBinding) : RecyclerView.ViewHolder(binding.root){
            val ivAddMember = binding.ivAddMember
            val ivSelectedMemberImage = binding.ivSelectedMemberImage
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCardSelectMemberHolder {
        return ItemCardSelectMemberHolder(
            ItemCardSelectedMemberBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemCardSelectMemberHolder, position: Int) {
        val model = list[position]

        if (position == list.size - 1 && assignMembers){
            holder.ivAddMember.visibility = View.VISIBLE
            holder.ivSelectedMemberImage.visibility = View.GONE
        }else{
            holder.ivAddMember.visibility = View.GONE
            holder.ivSelectedMemberImage.visibility = View.VISIBLE

            Glide.with(context).load(model.image).centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.ivSelectedMemberImage)
        }

        holder.itemView.setOnClickListener {
            if (onClickListener!= null){
                onClickListener!!.onClick()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener{
        fun onClick()
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

}