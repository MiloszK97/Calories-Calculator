package com.example.caloriescalculator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class SearchProductAdapter(
    private val context: Context,
    private val searchedProducts: List<SearchedProduct>)
    : RecyclerView.Adapter<SearchProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.one_searched_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val foundProduct = searchedProducts[position]
        holder.bind(foundProduct)
    }

    override fun getItemCount() = searchedProducts.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val tvFoundProdName: TextView = itemView.findViewById(R.id.tvFoundProdName)
        private val tvFoundProdCalories: TextView = itemView.findViewById(R.id.tvFoundProdCalories)
        private val tvFoundProdWeight: TextView = itemView.findViewById(R.id.tvFoundProdWeight)

        fun bind(foundProduct: SearchedProduct) {
            tvFoundProdName.text = foundProduct.name
            tvFoundProdCalories.text = "${foundProduct.caloriesExample}kcal"
            tvFoundProdWeight.text = "${foundProduct.weightExample}g"
        }
    }

}
