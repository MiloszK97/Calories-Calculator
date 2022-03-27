package com.example.caloriescalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caloriescalculator.api.RetrofitInstance
import com.example.caloriescalculator.model.OneMealModelItem
import com.example.caloriescalculator.utils.CLICKED_MEAL_POSITION
import com.example.caloriescalculator.utils.DEFAULT_MEALS
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class MealActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MealActivity"
    }

private lateinit var tvMealName : TextView
private lateinit var rvProdAdapter: ListOfProductsAdapter
private lateinit var rvListOfProducts : RecyclerView
private var clickedMealPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)

        tvMealName = findViewById(R.id.tvMealName)
        rvListOfProducts = findViewById(R.id.rvListOfProducts)
        clickedMealPosition = intent.getIntExtra(CLICKED_MEAL_POSITION, -1)

        getMealItemsList()

    }

    private fun setupMealAppearance(data: List<OneMealModelItem>) {
        tvMealName.text = getString(DEFAULT_MEALS[clickedMealPosition])
        val products = createProducts(data)
        rvProdAdapter = ListOfProductsAdapter(this, products, object: ListOfProductsAdapter.ProductClickListener{
            override fun onProductRemoveClick(position: Int) {
                deleteMealItemFromDB(products[position].mealItemID)
                products.removeAt(position)
                rvProdAdapter!!.notifyItemRemoved(position)
            }
        })
        rvListOfProducts.adapter = rvProdAdapter
        rvListOfProducts.layoutManager = LinearLayoutManager(this)
    }


    private fun createProducts(data: List<OneMealModelItem>): MutableList<Product> {
        val products = mutableListOf<Product>()
        for (i in data.indices)  products.add(Product(data[i].itemName, data[i].itemCalories.toInt(), data[i].itemWeight, data[i].itemProteins, data[i].itemFat, data[i].itemCarbs, data[i].mealItemID))
        return products
    }

    private fun getMealItemsList(){
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        RetrofitInstance.api.getMeal("dsf34t4t34tdfg", clickedMealPosition, "2022-02-21").enqueue(object: Callback<List<OneMealModelItem>>{
            override fun onResponse(call: Call<List<OneMealModelItem>>, response: Response<List<OneMealModelItem>>) {
                if(response.body()!!.isEmpty()){
                    Toast.makeText(this@MealActivity, "There is nothing in this meal!", Toast.LENGTH_SHORT).show()
                    Handler().postDelayed({
                        val intent = Intent(this@MealActivity, MainActivity::class.java)
                        finish()
                        startActivity(intent)
                    }, 1800)
                }else{
                    Log.d(TAG, "Response: ${response.body()}")
                    setupMealAppearance(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<OneMealModelItem>>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun deleteMealItemFromDB(mealItemID: String) {
        Log.d(TAG, "Meal Item ID: $mealItemID")
        GlobalScope.launch {
            try {
                RetrofitInstance.api.deleteMealItem(mealItemID)
            }catch (e: IOException){
                Log.e(TAG, "IOException, you might have not internet connection.")
                return@launch
            }catch (e: HttpException){
                Log.e(TAG, "HttpException, unexpected response.")
                return@launch
            }
        }
    }
}
