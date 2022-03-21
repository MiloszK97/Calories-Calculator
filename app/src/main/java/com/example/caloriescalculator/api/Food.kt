package com.example.caloriescalculator.api

data class Food(
    val nf_calories: Double,
    val nf_protein: Double,
    val nf_total_carbohydrate: Double,
    val nf_total_fat: Double,
    val serving_weight_grams: Int
)