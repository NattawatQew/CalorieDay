package com.eatburn.calorieday

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.example_item.view.*


class ExampleAdapter(private val exampleList: List<ExampleItem>) : RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.example_item, parent, false)
        return ExampleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currrrentItem = exampleList[position]
        holder.imageView.setImageResource(currrrentItem.imageResource)
        holder.textView1.text = currrrentItem.meal
        holder.textView3.text = currrrentItem.menu
        holder.textView5.text = currrrentItem.cal
        holder.textView7.text = currrrentItem.date
        holder.textView9.text = currrrentItem.lat
        holder.textView11.text = currrrentItem.ln
//        holder.itemView.text_view_1.text  = currrrentItem.text1
    }

    override fun getItemCount() = exampleList.size

    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.image_view
        val textView1: TextView = itemView.text_view_1
        val textView3: TextView = itemView.text_view_3
        val textView5: TextView = itemView.text_view_5
        val textView7: TextView = itemView.text_view_7
        val textView9: TextView = itemView.text_view_9
        val textView11: TextView = itemView.text_view_11
    }
}