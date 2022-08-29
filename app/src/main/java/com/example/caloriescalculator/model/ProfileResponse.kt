package com.example.caloriescalculator.model

data class ProfileResponse(
    val profileAge: Int,
    val profileGender: String,
    val profileHeight: Int,
    val profileID: String,
    val profileNickname: String,
    val profileTotCal: Int,
    val profileWeight: Double,
    val waterAmount: Int,
    val profileTotProt: Double,
    val profileTotFat: Double,
    val profileTotCarbs: Double
)