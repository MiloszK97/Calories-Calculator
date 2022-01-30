package com.example.caloriescalculator.profile_setup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.RadioButton
import com.example.caloriescalculator.databinding.ActivitySetupProfileBinding

class ActivitySetupProfile : AppCompatActivity() {

    companion object{
        private const val TAG = "ActivitySetupProfile"
    }

    private lateinit var binding: ActivitySetupProfileBinding
    private lateinit var activityLevel: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spinnerActivityLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                activityLevel = adapterView?.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.btnProfileCreate.setOnClickListener {
            collectUserProfileData(activityLevel)
            //Toast.makeText(this, "You choose $activityLevel", Toast.LENGTH_SHORT).show()
        }

    }

    private fun collectUserProfileData(activityLevel: String) {

        val checkedGenderRadioButtonID = binding.rgGender.checkedRadioButtonId
        val chosenGender = findViewById<RadioButton>(checkedGenderRadioButtonID)
        val checkedBodyGoalRadioButtonID = binding.rgBodyGoal.checkedRadioButtonId
        val chosenBodyGoal = findViewById<RadioButton>(checkedBodyGoalRadioButtonID)

        val userAge = binding.etProfileAge.text.toString()
        val userHeight = binding.etProfileHeight.text.toString()
        val userWeight = binding.etProfileWeight.text.toString()
        val userBodyGoal = chosenBodyGoal.text.toString()

        val totalCalories = countTotalCalories(
            activityLevel,
            chosenGender.text.toString(),
            userAge.toInt(),
            userHeight.toInt(),
            userWeight.toInt(),
            userBodyGoal)

        val userProfileInfo = listOf(
            binding.etProfileNickname.text.toString(),
            userAge,
            chosenGender.text.toString(),
            userHeight,
            userWeight,
            userBodyGoal,
            totalCalories.toString()
        )

        Log.d(TAG, "Essa patrz to denciaku : $userProfileInfo")
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
}
