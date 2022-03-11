package com.example.caloriescalculator

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caloriescalculator.api.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.io.IOException

class SearchProduct : AppCompatActivity() {

    companion object{
        private const val TAG = "SearchProduct"
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

        etSearchProduct.afterTextChangedDelayed {
            getRequest(it)
        }
    }

    private fun getRequest(productName: String) {
        runBlocking {
            launch(Dispatchers.IO) {
                //progressbar.isVisible = true
                val response = try {
                    RetrofitInstance.api.getData(productName)
                } catch (e: IOException) {
                    Log.e(TAG, "IOException, you might have not internet connection.")
                    //progressbar.isVisible = false
                    return@launch
                } catch (e: HttpException) {
                    Log.e(TAG, "HttpException, unexpected response.")
                    //progressbar.isVisible = false
                    return@launch
                }
                if (response.isSuccessful && response.body() != null) {
                    Log.e(TAG, "Cool ${response.body()}")
                } else {
                    Log.e(TAG, "Response not successful: ${response.code()}, Product name: $productName\n${response.headers()}")

                }
                //progressbar.isVisible = false
            }
        }
    }
    private fun setupSearchedProdView(productName: String) {
        val searchedProducts = provideDataForRV(productName)
        if (etSearchProduct.text.isEmpty()){
            searchedProducts.clear()
            searchProductAdapter.notifyDataSetChanged()
        }
        searchProductAdapter = SearchProductAdapter(this, searchedProducts)
        rvSearchProduct.adapter = searchProductAdapter
        rvSearchProduct.layoutManager = LinearLayoutManager(this)
    }

    private fun provideDataForRV(productName: String): MutableList<SearchedProduct>{
        val searchedProducts = mutableListOf<SearchedProduct>()
        for (i in 0..10) searchedProducts.add(SearchedProduct(productName, i, i))
        return searchedProducts
    }

    fun TextView.afterTextChangedDelayed(afterTextChanged: (String) -> Unit) {
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
/*
interface ApiInterface {
    @Headers(
        "x-app-id: 86ae75c0",
        "x-app-key: 6e2a5c7f15298becaf573be50ca4cbc3"
    )

    @GET("/v2/search/instant")
    suspend fun getData(@Query("query") foodName: String): Response<Food>

}

val api: ApiInterface = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(MainActivity.BASE_URL)
        .build()
        .create(ApiInterface::class.java)

*/
