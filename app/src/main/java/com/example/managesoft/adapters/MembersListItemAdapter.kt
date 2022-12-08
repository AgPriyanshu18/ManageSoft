package com.example.managesoft.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.managesoft.R
import com.example.managesoft.databinding.ActivityMembersBinding
import com.example.managesoft.databinding.ItemMemberBinding
import com.example.managesoft.model.User
import com.example.managesoft.utils.Constants

open class MembersListItemAdapter (
    private val context : Context,
    private var list : ArrayList<User>
        ) : RecyclerView.Adapter<MembersListItemAdapter.MemberListItemHolder>(){

            class MemberListItemHolder(binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root){
                val ivMemberImage = binding.ivMemberImage
                val tvMemberName = binding.tvMemberName
                val tvMemberEmail = binding.tvMemberEmail
                val ivSelectedMember = binding.ivSelectedMember
            }

    private var onClickListener : OnClickListener ?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberListItemHolder {
        return MemberListItemHolder(ItemMemberBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun onBindViewHolder(holder: MemberListItemHolder, position: Int) {
        val model = list[position]

        Glide.with(context).load(model.image).centerCrop().
        placeholder(R.drawable.ic_board_place_holder).
        into(holder.ivMemberImage)

        holder.tvMemberEmail.text = model.email
        holder.tvMemberName.text = model.name

        if (model.selected){
            holder.ivSelectedMember.visibility = View.VISIBLE
        }else{
            holder.ivSelectedMember.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null){
                if (model.selected){
                    onClickListener!!.onClick(position,model,Constants.UN_SELECT)
                }else{
                    onClickListener!!.onClick(position,model,Constants.SELECT)
                }
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener{
        fun onClick(position : Int , user : User , action : String)
    }
}
