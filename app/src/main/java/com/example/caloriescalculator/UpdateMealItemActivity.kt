package com.example.caloriescalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.caloriescalculator.api.RetrofitInstance
import com.example.caloriescalculator.databinding.ActivityUpdateMealItemBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class UpdateMealItemActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "UpdateMealItemActivity"
    }

    private lateinit var binding: ActivityUpdateMealItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateMealItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pbUpdateMI.isVisible = false

        val productID = intent.getStringExtra("PRODUCT_ID")
        val productName = intent.getStringExtra("PRODUCT_NAME")
        val productWeight = intent.getDoubleExtra("PRODUCT_WEIGHT", 0.0)
        val productCalories = intent.getIntExtra("PRODUCT_CAL", -1)
        val productProteins = intent.getDoubleExtra("PRODUCT_PROT", 0.0)
        val productFat = intent.getDoubleExtra("PRODUCT_FAT", 0.0)
        val productCarbs = intent.getDoubleExtra("PRODUCT_CARBS", 0.0)

        binding.button.setOnClickListener {
            if (binding.editTextNumberDecimal.text.toString() != ""){
                binding.pbUpdateMI.isVisible = true
                calculateNewNutrition(productID, productName, productWeight, productCalories, productProteins, productFat, productCarbs, binding.editTextNumberDecimal.text.toString())
            }else{
                Toast.makeText(this@UpdateMealItemActivity, "Weight cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateNewNutrition(
        productID: String?,
        productName: String?,
        productWeight: Double,
        productCalories: Int,
        productProteins: Double,
        productFat: Double,
        productCarbs: Double,
        newProductWeight: String
    ) {
        val newCalories = calculateNewValues(productWeight, productCalories.toDouble(), newProductWeight.toDouble()).toInt()
        val newProteins = calculateNewValues(productWeight, productProteins, newProductWeight.toDouble())
        val newFat = calculateNewValues(productWeight, productFat, newProductWeight.toDouble())
        val newCarbs = calculateNewValues(productWeight, productCarbs, newProductWeight.toDouble())

        updatingDB(productID, productName, newProductWeight, newCalories, newProteins, newFat, newCarbs)

    }

    private fun calculateNewValues(weight: Double, value: Double, fromET: Double): Double{
        return (fromET*value)/weight
    }

    private fun updatingDB(
        productID: String?,
        productName: String?,
        newProductWeight: String,
        newCalories: Int,
        newProteins: Double,
        newFat: Double,
        newCarbs: Double
    ) {
        lifecycleScope.launch {
            try {
                RetrofitInstance.api.updateMealItem(productID!!, productName!!, newCalories.toDouble(), newProductWeight.toDouble(), newProteins, newFat, newCarbs)
            }catch (e: IOException){
                Log.e(TAG, "IOException, you might have not internet connection.")
                binding.pbUpdateMI.isVisible = false
                return@launch
            }catch (e: HttpException){
                Log.e(TAG, "HttpException, unexpected response.")
                binding.pbUpdateMI.isVisible = false
                return@launch
            }
            binding.pbUpdateMI.isVisible = false
            val intent = Intent(this@UpdateMealItemActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}