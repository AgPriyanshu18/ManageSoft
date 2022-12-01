package com.example.managesoft.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.managesoft.R
import com.example.managesoft.databinding.ItemBoardBinding
import com.example.managesoft.model.Board
import com.google.android.material.transition.Hold

open class BoardItemsAdapter(private val context : Context,
                             private var list : ArrayList<Board >):
   RecyclerView.Adapter<BoardItemsAdapter.BoardsItemHolder>(){

    class BoardsItemHolder(binding : ItemBoardBinding) : RecyclerView.ViewHolder(binding.root){
        val image = binding.ivBoardImage
        val boardName = binding?.tvName
        val createdBy = binding?.tvCreatedBy
    }

    private var onClickListener : OnClickListener ?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardsItemHolder {
        return BoardsItemHolder(ItemBoardBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun onBindViewHolder(holder: BoardsItemHolder, position: Int) {
        val model = list[position]
        if (holder is BoardsItemHolder){
            Glide.with(context).load(model.image).centerCrop().
                    placeholder(R.drawable.ic_board_place_holder).
                    into(holder.image)

            holder.boardName?.text = model.name
            "Created by ${model.createdBy}".also { holder.createdBy?.text = it }

            holder.itemView.setOnClickListener{
                if (onClickListener != null){
                    onClickListener!!.onClick(position,model)
                }
            }
        }
    }

    interface OnClickListener{
        fun onClick(position: Int , modal : Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }


    override fun getItemCount(): Int {
        return list.size
    }

}