package com.example.caloriescalculator

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.caloriescalculator.api.RetrofitInstance
import com.example.caloriescalculator.databinding.ActivityWaterControlBinding
import com.example.caloriescalculator.model.MealItem
import com.example.caloriescalculator.model.ProfileResponse
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


class ActivityWaterControl : AppCompatActivity() {

    companion object{
        private const val TAG = "ActivityWaterControl"
    }

    private lateinit var binding: ActivityWaterControlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWaterControlBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val currentUserID = intent.getStringExtra("USER_ID")
        val chosenDate = intent.getStringExtra("DATE_TO_SHOW")

        binding.pbWaterActivity.isVisible = false
        getWaterAmountFromDB(currentUserID!!, chosenDate!!)

        binding.ibGlassOfWater.setOnClickListener {
            addOneGlassWater(currentUserID, chosenDate!!)
            binding.pbWaterActivity.isVisible = true
            Handler().postDelayed({
                getWaterAmountFromDB(currentUserID, chosenDate!!)
            },2000)
            binding.pbWaterActivity.isVisible = false
        }
    }

    private fun getWaterAmountFromDB(currentUserID: String, chosenDate: String) {
        binding.pbWaterActivity.isVisible = true
        RetrofitInstance.api.getProfile(currentUserID).enqueue(object: Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.body()!!.waterAmount == null){
                    binding.tvWaterAmount.text = "0 ml"
                    binding.tvWaterAmount2.text = "/0 ml"
                    setupPieChart()
                    loadPieChartData(0, 0)
                }else{
                    binding.tvWaterAmount2.text = "/${response.body()!!.waterAmount} ml"
                    getWaterConsumed(response.body()!!.waterAmount, currentUserID, chosenDate)
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                binding.pbWaterActivity.isVisible = false
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    private fun getWaterConsumed(waterAmount: Int, currentUserID: String, chosenDate: String) {
        RetrofitInstance.api.getWaterConsumed(currentUserID, 5, chosenDate).enqueue(object: Callback<Int>{
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if(response.body() == null){
                    binding.tvWaterAmount.text = "0 ml"
                    setupPieChart()
                    loadPieChartData(0, waterAmount)
                }else{
                    binding.tvWaterAmount.text = "${response.body()!!} ml"
                    setupPieChart()
                    loadPieChartData(response.body()!!, waterAmount)
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                binding.pbWaterActivity.isVisible = false
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    private fun setupPieChart(){
        binding.pieChartWater.isDrawHoleEnabled = true
        binding.pieChartWater.setUsePercentValues(true)
        binding.pieChartWater.setEntryLabelTextSize(12f)
        binding.pieChartWater.setEntryLabelColor(Color.BLACK)
        binding.pieChartWater.description.isEnabled = false

        val legend = binding.pieChartWater.legend
        legend.isEnabled = false
    }

    private fun loadPieChartData(waterConsumed: Int, waterAmount: Int) {
        binding.pbWaterActivity.isVisible = false
        var percentage = (waterConsumed.toFloat()/waterAmount)*100
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(percentage, "Water In Your Body"))
        entries.add(PieEntry(100-percentage, "Water To Drink"))

        val colors = ArrayList<Int>()
        colors.add(Color.rgb(36, 119, 255))
        colors.add(Color.rgb(193, 207, 230))

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(binding.pieChartWater))
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)

        binding.pieChartWater.data = data
        binding.pieChartWater.invalidate()
        binding.pieChartWater.animateY(1400, Easing.EaseInQuad)
    }

    private fun addOneGlassWater(currentUserID: String, chosenDate: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                RetrofitInstance.api.postMealItemToDB(
                    MealItem(
                        "Water",
                        250.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        currentUserID,
                        5,
                        chosenDate))
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