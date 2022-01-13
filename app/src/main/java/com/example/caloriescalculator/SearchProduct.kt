package com.example.caloriescalculator

import android.app.DownloadManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchProduct : AppCompatActivity() {

    private lateinit var searchProductAdapter: SearchProductAdapter
    private lateinit var rvSearchProduct: RecyclerView
    private lateinit var etSearchProduct: EditText
    private lateinit var cvSearchProd: CardView
    private lateinit var cvFavouriteProduct: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_product)

        rvSearchProduct = findViewById(R.id.rvSearchProd)
        etSearchProduct = findViewById(R.id.etSearchProd)
        cvSearchProd = findViewById(R.id.cvSearchProd)
        cvFavouriteProduct = findViewById(R.id.cvFavouriteProd)

        etSearchProduct.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) = setupSearchedProdView()
        })
    }

    private fun setupSearchedProdView() {
        val searchedProducts = provideDataForRV()
        searchProductAdapter = SearchProductAdapter(this, searchedProducts)
        rvSearchProduct.adapter = searchProductAdapter
        rvSearchProduct.layoutManager = LinearLayoutManager(this)
    }

    private fun provideDataForRV(): MutableList<SearchedProduct>{
        val searchedProducts = mutableListOf<SearchedProduct>()
        for (i in 0..10) searchedProducts.add(SearchedProduct(etSearchProduct.text.toString(), i, i))
        return searchedProducts
    }
}