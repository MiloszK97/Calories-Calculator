package com.example.caloriescalculator.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface APIInterface {

    @GET("api/v1/mealitem/getFood/{foodName}")
    suspend fun getData(@Path("foodName") foodName: String): Response<GetResponse>

}