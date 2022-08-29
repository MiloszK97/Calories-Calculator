package com.example.caloriescalculator.model

data class OneMealModelItem(
    val date: String,
    val itemCalories: Double,
    val itemCarbs: Double,
    val itemFat: Double,
    val itemName: String,
    val itemProteins: Double,
    val itemWeight: Double,
    val mealID: Int,
    val mealItemID: String,
    val profileID: String
)