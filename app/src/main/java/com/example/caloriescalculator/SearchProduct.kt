package com.example.caloriescalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caloriescalculator.api.GetResponse
import com.example.caloriescalculator.api.RetrofitInstance
import com.example.caloriescalculator.utils.CLICKED_MEAL_POSITION
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchProduct : AppCompatActivity() {

    companion object{
        private const val TAG = "SearchProduct"
        private const val FOOD_KEY = "FoodName"
        private const val MEAL_ID = "mealID"
    }

    private lateinit var searchProductAdapter: SearchProductAdapter
    private lateinit var rvSearchProduct: RecyclerView
    private lateinit var etSearchProduct: EditText
    private lateinit var progressbar: ProgressBar
    private lateinit var cvSearchProd: CardView
    private lateinit var cvFavouriteProduct: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_product)

        rvSearchProduct = findViewById(R.id.rvSearchProd)
        etSearchProduct = findViewById(R.id.etSearchProd)
        cvSearchProd = findViewById(R.id.cvSearchProd)
        cvFavouriteProduct = findViewById(R.id.cvFavouriteProd)
        progressbar = findViewById(R.id.progressBarSearchProd)
        progressbar.isVisible = false

        val clickedMealPosition = intent.getIntExtra(CLICKED_MEAL_POSITION, -1)

        etSearchProduct.afterTextChangedDelayed {
            if (it != null && it !=""){
                getRequest(it, clickedMealPosition)
            }
            else{
                Toast.makeText(this@SearchProduct, "Start typing...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getRequest(productName: String, clickedMealPosition: Int){
        progressbar.isVisible = true
        RetrofitInstance.api.getData(productName).enqueue(object : Callback<GetResponse?> {
            override fun onResponse(call: Call<GetResponse?>, response: Response<GetResponse?>) {
                val responseBody = response.body()!!
                provideDataForRV(responseBody, clickedMealPosition)
                progressbar.isVisible = false
            }

            override fun onFailure(call: Call<GetResponse?>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
                progressbar.isVisible = false
            }
        })
    }

    private fun setupSearchedProdView(searchedProducts: MutableList<SearchedProduct>, clickedMealPosition: Int) {
        searchProductAdapter = SearchProductAdapter(this, searchedProducts, object: SearchProductAdapter.FoodClickListener{
            override fun onFoodClicked(position: Int) {
                val intent = Intent(this@SearchProduct, AddProduct::class.java)
                intent.putExtra(FOOD_KEY, searchedProducts[position].food_name)
                intent.putExtra(MEAL_ID, clickedMealPosition)
                startActivity(intent)
            }
        })
        rvSearchProduct.adapter = searchProductAdapter
        rvSearchProduct.layoutManager = LinearLayoutManager(this)
    }

    private fun provideDataForRV(getResponseFood: GetResponse, clickedMealPosition: Int){
        var list1: MutableList<SearchedProduct> =
            getResponseFood.common.map { SearchedProduct(it.food_name, it.serving_qty, it.serving_unit) } as MutableList<SearchedProduct>
        var list2: MutableList<SearchedProduct> =
            getResponseFood.branded.map { SearchedProduct(it.food_name, it.serving_qty, it.serving_unit) } as MutableList<SearchedProduct>
        Log.e(TAG, "Common: $list1")
        Log.e(TAG, "Branded: $list2")
        var searchedProducts: MutableList<SearchedProduct> = list1.let { first -> list2.let { second -> first + second } } as MutableList<SearchedProduct>
        Log.e(TAG, "Merged: $searchedProducts")
        setupSearchedProdView(searchedProducts, clickedMealPosition)
        //return searchedProducts
    }

    private fun TextView.afterTextChangedDelayed(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            var timer: CountDownTimer? = null

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                timer?.cancel()
                timer = object : CountDownTimer(2000, 2500) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        afterTextChanged.invoke(editable.toString())
                    }
                }.start()
            }
        })
    }

}
