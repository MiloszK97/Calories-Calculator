package com.example.caloriescalculator.profile_setup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.caloriescalculator.R

class ViewPagerAdapter (
    val labels: List<String>
        ): RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>(){

    inner class ViewPagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_pager, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val curLabel = labels[position]
        val tvLabel: TextView = holder.itemView.findViewById(R.id.tvLabel)
        tvLabel.text = curLabel
    }

    override fun getItemCount() = labels.size
}