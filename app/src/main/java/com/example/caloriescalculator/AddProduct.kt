package com.example.caloriescalculator

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.caloriescalculator.api.NutritionResponse
import com.example.caloriescalculator.api.RetrofitInstance
import com.example.caloriescalculator.databinding.ActivityAddProductBinding
import com.example.caloriescalculator.model.MealItem
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.internal.bind.util.ISO8601Utils.format
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.lang.String.format
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddProduct : AppCompatActivity() {

    companion object{
        private const val TAG = "AddProduct"
    }

    private lateinit var binding: ActivityAddProductBinding
    lateinit var data: NutritionResponse

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postFoodName: String? = intent.getStringExtra("FoodName")
        val mealID: Int = intent.getIntExtra("mealID", -1)
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        binding.postProgressBar.isVisible = false
        binding.tvAddProductName.text = postFoodName

        getNutritionFromAPI(postFoodName)

        val formatter = DateTimeFormatter.ISO_DATE
        val date = LocalDateTime.now().format(formatter)

        binding.ibAddFromApi.setOnClickListener {
            val mealitem = MealItem(
                postFoodName!!,
                data.foods[0].serving_weight_grams.toDouble(),
                data.foods[0].nf_calories,
                data.foods[0].nf_protein,
                data.foods[0].nf_total_fat,
                data.foods[0].nf_total_carbohydrate,
                currentUserID,
                mealID,
                date)
            sendMealItemToDB(mealitem)
        }

        binding.ibAddHalf.setOnClickListener {
            val mealitem = MealItem(
                postFoodName!!,
                50.0,
                convertToFifty(data.foods[0].nf_calories, data.foods[0].serving_weight_grams).toDouble(),
                convertProteins(data.foods[0].nf_protein, data.foods[0].serving_weight_grams, 50)/2,
                convertFat(data.foods[0].nf_total_fat, data.foods[0].serving_weight_grams, 50)/2,
                convertCarbs(data.foods[0].nf_total_carbohydrate, data.foods[0].serving_weight_grams, 50)/2,
                currentUserID,
                mealID,
                date)
            sendMealItemToDB(mealitem)
        }

        binding.ibAddHundred.setOnClickListener {
            val mealitem = MealItem(
                postFoodName!!,
                100.0,
                convertToHundred(data.foods[0].nf_calories, data.foods[0].serving_weight_grams, 100).toDouble(),
                convertProteins(data.foods[0].nf_protein, data.foods[0].serving_weight_grams, 100),
                convertFat(data.foods[0].nf_total_fat, data.foods[0].serving_weight_grams, 100),
                convertCarbs(data.foods[0].nf_total_carbohydrate, data.foods[0].serving_weight_grams, 100),
                currentUserID,
                mealID,
                date)
            sendMealItemToDB(mealitem)
        }

        binding.ibAddCustom.setOnClickListener {
            val weightByUser = binding.etCustomWeight.text.toString()
            val mealitem = MealItem(
                postFoodName!!,
                weightByUser.toDouble(),
                convertToHundred(data.foods[0].nf_calories, data.foods[0].serving_weight_grams, weightByUser.toInt()).toDouble(),
                convertProteins(data.foods[0].nf_protein, data.foods[0].serving_weight_grams, weightByUser.toInt()),
                convertFat(data.foods[0].nf_total_fat, data.foods[0].serving_weight_grams, weightByUser.toInt()),
                convertCarbs(data.foods[0].nf_total_carbohydrate, data.foods[0].serving_weight_grams, weightByUser.toInt()),
                currentUserID,
                mealID,
                date)
            sendMealItemToDB(mealitem)
        }
    }

    private fun sendMealItemToDB(mealitem: MealItem) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    RetrofitInstance.api.postMealItemToDB(mealitem)
                }catch (e: IOException){
                    Log.e(TAG, "IOException, you might have not internet connection.")
                    return@launch
                }catch (e: HttpException){
                    Log.e(TAG, "HttpException, unexpected response.")
                    return@launch
                }
            }
        val intent = Intent(this@AddProduct, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun getNutritionFromAPI(postFoodName: String?){
        binding.postProgressBar.isVisible = true
        if (postFoodName != null) {
            RetrofitInstance.api.getNutrition(postFoodName).enqueue(object : Callback<NutritionResponse?> {
                override fun onResponse(call: Call<NutritionResponse?>, response: Response<NutritionResponse?>) {
                    val responseBody = response.body()!!
                    setupNutritionInfo(responseBody)
                    provideDataForActivity(responseBody)
                    binding.postProgressBar.isVisible = false
                }

                override fun onFailure(call: Call<NutritionResponse?>, t: Throwable) {
                    Log.d(TAG, "onFailure: ${t.message}")
                    binding.postProgressBar.isVisible = false
                }
            })
        }else{
            throw IOException("Cannot be null")
        }
    }

    private fun provideDataForActivity(responseBody: NutritionResponse) {
        data = responseBody
    }

    private fun setupNutritionInfo(responseBody: NutritionResponse) {
        binding.tvAddProductFromApiWeight.text = "${responseBody.foods[0].serving_weight_grams} g"
        binding.tvAddProductFromApiKcal.text = "${responseBody.foods[0].nf_calories} kcal"
        binding.tvAddProductHalfKcal.text = "${convertToFifty(responseBody.foods[0].nf_calories, responseBody.foods[0].serving_weight_grams)} kcal"
        binding.tvAddProductDefaultKcal.text = "${convertToHundred(responseBody.foods[0].nf_calories, responseBody.foods[0].serving_weight_grams, 100)} kcal"
    }

    private fun convertToHundred(calories: Double, grams: Int, customWeight: Int): String {
        val calculation = calories*customWeight/grams.toDouble()
        return String.format("%.2f", calculation)
    }

    private fun convertToFifty(calories: Double, grams: Int): String {
        val calculation = (calories*100/grams.toDouble())/2
        return String.format("%.2f", calculation)
    }

    private fun convertProteins(proteins: Double, grams: Int, customWeight: Int): Double{
        return proteins*customWeight/grams.toDouble()
    }

    private fun convertFat(fat: Double, grams: Int, customWeight: Int): Double{
        return fat*customWeight/grams.toDouble()
    }

    private fun convertCarbs(carbs: Double, grams: Int, customWeight: Int): Double{
        return carbs*customWeight/grams.toDouble()
    }
}