package com.example.caloriescalculator.profile_setup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.caloriescalculator.AddProduct
import com.example.caloriescalculator.MainActivity
import com.example.caloriescalculator.api.RetrofitInstance
import com.example.caloriescalculator.databinding.ActivitySetupProfileBinding
import com.example.caloriescalculator.model.ProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.math.round

class ActivitySetupProfile : AppCompatActivity() {

    companion object{
        private const val TAG = "ActivitySetupProfile"
    }

    private lateinit var binding: ActivitySetupProfileBinding
    private lateinit var activityLevel: String
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userID = intent.getStringExtra("USER_ID")!!
        //Toast.makeText(this@ActivitySetupProfile, "Userid: $userID", Toast.LENGTH_SHORT).show()
        getProfileInfo(userID)

        binding.spinnerActivityLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                activityLevel = adapterView?.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.btnProfileCreate.setOnClickListener {
            collectUserProfileData(activityLevel)
            Toast.makeText(this, "Successfully created profile!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun collectUserProfileData(activityLevel: String) {

        val checkedGenderRadioButtonID = binding.rgGender.checkedRadioButtonId
        val chosenGender = findViewById<RadioButton>(checkedGenderRadioButtonID)
        val checkedBodyGoalRadioButtonID = binding.rgBodyGoal.checkedRadioButtonId
        val chosenBodyGoal = findViewById<RadioButton>(checkedBodyGoalRadioButtonID)

        val userNickname = binding.etProfileNickname.text.toString()
        val userAge = binding.etProfileAge.text.toString()
        val userHeight = binding.etProfileHeight.text.toString()
        val userWeight = binding.etProfileWeight.text.toString()
        val userBodyGoal = chosenBodyGoal.text.toString()
        val userWaterAmount = userWeight.toInt() * 33


        val totalCalories = countTotalCalories(
            activityLevel,
            chosenGender.text.toString(),
            userAge.toInt(),
            userHeight.toInt(),
            userWeight.toInt(),
            userBodyGoal)

        val totProteins = countTotalProteins(userWeight.toDouble())
        val totFat = countTotalFat(totalCalories.toDouble())
        val totCarbs = countTotalCarbs(totalCalories, totProteins, totFat)

        val userProfileInfo = ProfileResponse(
            userAge.toInt(),
            chosenGender.text.toString(),
            userHeight.toInt(),
            userID!!,
            userNickname,
            totalCalories,
            userWeight.toDouble(),
            userWaterAmount,
            totProteins,
            totFat,
            totCarbs
        )
        postProfileToDB(userProfileInfo)
    }

    private fun countTotalProteins(userWeight: Double): Double {
        return String.format("%.1f", userWeight*1.8).toDouble()
    }

    private fun countTotalFat(totalCalories: Double): Double {
        return String.format("%.1f", (totalCalories*0.25)/9).toDouble()
    }

    private fun countTotalCarbs(totalCalories: Int, totProteins: Double, totFat: Double): Double {
        return String.format("%.1f", (totalCalories - totProteins*4 - totFat*9)/4).toDouble()
    }

    private fun countTotalCalories(
        level: String,
        gender: String,
        userAge: Int,
        userHeight: Int,
        userWeight: Int,
        userBodyGoal: String
    ): Int {

        val activityFactor = level.take(3).toDoubleOrNull()
        Log.d(TAG, "Activity level: $activityFactor")

        if (gender == "Male"){
            var countedCalories = ((10 * userWeight) + (6.25 * userHeight) - (5 * userAge) + 5) * activityFactor!!

            when(userBodyGoal){
                "Loose Weight" -> {
                    countedCalories *= 0.85
                }

                "Keep your weight" -> {
                    countedCalories *= 1
                }

                "Gain muscle" -> {
                    countedCalories += 300
                }
            }

            return countedCalories.toInt()

        }

        if (gender == "Female"){
            var countedCalories = ((10 * userWeight) + (6.25 * userHeight) - (5 * userAge) - 161) * activityFactor!!

            when(userBodyGoal){
                "Loose Weight" -> {
                    countedCalories *= 0.85
                }

                "Keep your weight" -> {
                    countedCalories *= 1
                }

                "Gain muscle" -> {
                    countedCalories += 300
                }
            }

            return countedCalories.toInt()
        }
    return 0
    }

    private fun postProfileToDB(profileInfo: ProfileResponse) {
        lifecycleScope.launch(Dispatchers.IO){
            try {
                RetrofitInstance.api.postProfile(profileInfo)
                val intent = Intent(this@ActivitySetupProfile, MainActivity::class.java)
                startActivity(intent)
            }catch (e: IOException){
                Log.e(TAG, "IOException, you might have not internet connection.")
                return@launch
            }catch (e: HttpException){
                Log.e(TAG, "HttpException, unexpected response.")
                return@launch
            }
        }
    }


    private fun getProfileInfo(userID: String) {
        RetrofitInstance.api.getProfile(userID).enqueue(object: Callback<ProfileResponse>{
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                  if (response.body() != null){
                    binding.btnProfileCreate.text = "Update"
                    setupProfileToUpdate(response.body()!!)
                }else{
                    Toast.makeText(this@ActivitySetupProfile, "You do not have profile, go and create one.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun setupProfileToUpdate(response: ProfileResponse) {
        binding.etProfileNickname.setText(response.profileNickname)
        binding.etProfileAge.setText(response.profileAge.toString())
        binding.etProfileWeight.setText(response.profileWeight.toString())
        binding.etProfileHeight.setText(response.profileHeight.toString())
        binding.rgGender.isVisible = false
        binding.tvGenderLabel.isVisible = false
    }


}
