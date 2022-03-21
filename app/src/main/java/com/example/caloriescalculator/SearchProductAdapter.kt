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
    private val searchedProducts: MutableList<SearchedProduct>,
    private val foodClickListener: SearchProductAdapter.FoodClickListener
) : RecyclerView.Adapter<SearchProductAdapter.ViewHolder>() {

    interface FoodClickListener{
        fun onFoodClicked(position: Int)
    }


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
        private val cvFoundProd: CardView = itemView.findViewById(R.id.cvFoundProd)

        fun bind(foundProduct: SearchedProduct) {
            tvFoundProdName.text = foundProduct.food_name
            tvFoundProdCalories.text = "${foundProduct.serving_qty}"
            tvFoundProdWeight.text = "${foundProduct.serving_unit}"

            cvFoundProd.setOnClickListener {
                foodClickListener.onFoodClicked(position)
            }

        }
    }

}
