package com.example.caloriescalculator.model

import java.time.LocalDateTime

data class MealItem(
    private var itemName: String,
    private val itemWeight: Double,
    private val itemCalories: Double,
    private val itemProteins: Double,
    private val itemFat: Double,
    private val itemCarbs: Double,
    private val profileID: String,
    private val mealID: Int,
    private val date: String
)
