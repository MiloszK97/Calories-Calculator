package com.example.caloriescalculator

data class Product(
    val name: String,
    val numCalories: Int,
    val productWeight: Double,
    val numProteins: Double,
    val numFat: Double,
    val numCarbs: Double,
    val mealItemID: String
)
