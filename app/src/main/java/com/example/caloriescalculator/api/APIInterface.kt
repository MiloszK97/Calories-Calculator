package com.example.caloriescalculator.api

import com.example.caloriescalculator.model.MealItem
import com.example.caloriescalculator.model.OneMealModelItem
import retrofit2.Call
import retrofit2.http.*

interface APIInterface {

    @GET("api/v1/mealitem/getFood/{foodName}")
    fun getData(@Path("foodName") foodName: String): Call<GetResponse>

    @GET("api/v1/mealitem/food/{foodName}")
    fun getNutrition(@Path("foodName") foodName: String): Call<NutritionResponse>

    @POST("api/v1/mealitem")
    suspend fun postMealItemToDB(@Body mealItem: MealItem)

    @GET("api/v1/mealitem/getmeal/{profileID}")
    fun getMeal(@Path("profileID") profileID: String,
                @Query("mealID") mealID: Int,
                @Query("date") date: String): Call<List<OneMealModelItem>>

    @GET("api/v1/mealitem/getmealmacros/{profileID}")
    fun getMealTotKcal(
        @Path("profileID") profileID: String,
        @Query("mealID") mealID: Int,
        @Query("date") date: String): Call<List<List<Double>>>

    @DELETE("api/v1/mealitem/{mealItemID}")
    suspend fun deleteMealItem(@Path("mealItemID") mealItemID: String)
}

