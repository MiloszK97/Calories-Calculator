package com.example.caloriescalculator.api

data class GetResponse(
    val branded: List<Branded>,
    val common: List<Common>
)