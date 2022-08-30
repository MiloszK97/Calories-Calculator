package com.example.caloriescalculator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListOfProductsAdapter(
    private val context: Context,
    private val products: List<Product>,
    private val productClickListener: ProductClickListener,
    private val editProductListener: EditButtonClickListener)
    : RecyclerView.Adapter<ListOfProductsAdapter.ViewHolder>(){

    interface ProductClickListener {
        fun onProductRemoveClick(position : Int)
    }

    interface EditButtonClickListener {
        fun onProductEditClick(position : Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.one_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount() = products.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val ibDeleteProduct: ImageButton = itemView.findViewById(R.id.ibDeleteProduct)
        private val ibEditProduct: ImageButton = itemView.findViewById(R.id.ibEditProduct)
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductWeight: TextView = itemView.findViewById(R.id.tvProductWeight)
        private val tvProductCalories: TextView = itemView.findViewById(R.id.tvProductCalories)
        private val tvProductProteins: TextView = itemView.findViewById(R.id.tvProductProteins)
        private val tvProductFat: TextView = itemView.findViewById(R.id.tvProductFat)
        private val tvProductCarbs: TextView = itemView.findViewById(R.id.tvProductCarbs)
        fun bind(product: Product){
            tvProductName.text = product.name
            tvProductWeight.text = "${product.productWeight}g"
            tvProductCalories.text = "${product.numCalories}kcal"
            tvProductProteins.text = "${product.numProteins}g"
            tvProductFat.text = "${product.numFat}g"
            tvProductCarbs.text = "${product.numCarbs}"

            ibDeleteProduct.setOnClickListener {
                productClickListener.onProductRemoveClick(position)
            }

            ibEditProduct.setOnClickListener {
                editProductListener.onProductEditClick(position)
            }
        }
    }
}