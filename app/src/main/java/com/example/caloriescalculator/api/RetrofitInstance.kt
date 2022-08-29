package com.example.caloriescalculator.api

import com.example.caloriescalculator.utils.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api: APIInterface =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(APIInterface::class.java)


}