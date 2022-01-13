package com.example.caloriescalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caloriescalculator.utils.CLICKED_MEAL_POSITION
import com.example.caloriescalculator.utils.DEFAULT_MEALS

class MealActivity : AppCompatActivity() {

private lateinit var tvMealName : TextView
private lateinit var rvProdAdapter: ListOfProductsAdapter
private lateinit var rvListOfProducts : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)

        tvMealName = findViewById(R.id.tvMealName)
        rvListOfProducts = findViewById(R.id.rvListOfProducts)

        setupMealAppearance()
    }

    private fun setupMealAppearance() {
        val clickedMealPosition: Int = intent.getIntExtra(CLICKED_MEAL_POSITION, -1)
        tvMealName.text = getString(DEFAULT_MEALS[clickedMealPosition])
        val products = createProducts()
        rvProdAdapter = ListOfProductsAdapter(this, products, object: ListOfProductsAdapter.ProductClickListener{
            override fun onProductRemoveClick(position: Int) {
                products.removeAt(position)
                rvProdAdapter!!.notifyItemRemoved(position)
            }
        })
        rvListOfProducts.adapter = rvProdAdapter
        rvListOfProducts.layoutManager = LinearLayoutManager(this)
    }

    private fun createProducts(): MutableList<Product> {
        val products = mutableListOf<Product>()
        for (i in 0..25)  products.add(Product("Potatoes", i,i.toDouble(),i.toDouble(),i.toDouble(),i.toDouble()))
        return products
    }
}