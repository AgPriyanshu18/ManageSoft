package com.example.managesoft.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.managesoft.databinding.ItemLabelColorBinding

class LabelColorListItemsAdapter(private val context : Context,
                                 private var list : ArrayList<String>,
                                 private val mSelectedColor : String) :
    RecyclerView.Adapter<LabelColorListItemsAdapter.ItemLabelColorHolder>(){

    var onItemClickListener : OnItemClickListener ?= null

        class ItemLabelColorHolder(binding : ItemLabelColorBinding) : RecyclerView.ViewHolder(binding.root){
            val viewMain = binding.viewMain
            val ivSelectedColor = binding.ivSelectedColor
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemLabelColorHolder {
        return ItemLabelColorHolder(ItemLabelColorBinding.inflate(
            LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ItemLabelColorHolder, position: Int) {
        val item = list[position]
        holder.viewMain.setBackgroundColor(Color.parseColor(item))
        if (item == mSelectedColor){
            holder.ivSelectedColor.visibility = View.VISIBLE
        }else{
            holder.ivSelectedColor.visibility = View.GONE
        }

        holder.itemView.setOnClickListener{
            onItemClickListener?.onClick(position,item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onClick(position : Int , color : String)
    }
}