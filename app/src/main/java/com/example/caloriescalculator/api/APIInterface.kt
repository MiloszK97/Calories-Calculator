package com.example.caloriescalculator.api

import com.example.caloriescalculator.model.MealItem
import com.example.caloriescalculator.model.OneMealModelItem
import com.example.caloriescalculator.model.ProfileResponse
import retrofit2.Call
import retrofit2.http.*

interface APIInterface {

    @GET("api/v1/mealitem/getFood/{foodName}")
    fun getData(@Path("foodName") foodName: String): Call<GetResponse>

    @GET("api/v1/mealitem/food/{foodName}")
    fun getNutrition(@Path("foodName") foodName: String): Call<NutritionResponse>

    @GET("/api/v1/mealitem/getdaymacros/{profileID}")
    fun getFullDayMacro(@Path("profileID") profileID: String,
                        @Query("date") date: String): Call<List<Double>>

    @POST("api/v1/mealitem")
    suspend fun postMealItemToDB(@Body mealItem: MealItem)

    @GET("api/v1/mealitem/getmeal/{profileID}")
    fun getMeal(@Path("profileID") profileID: String,
                @Query("mealID") mealID: Int,
                @Query("date") date: String): Call<List<OneMealModelItem>>

    @GET("api/v1/mealitem/getWaterConsumed/{profileID}")
    fun getWaterConsumed(@Path("profileID") profileID: String,
                         @Query("mealID") mealID: Int,
                         @Query("date") date: String): Call<Int>

    @GET("api/v1/mealitem/getmealmacros/{profileID}")
    fun getMealTotKcal(
        @Path("profileID") profileID: String,
        @Query("mealID") mealID: Int,
        @Query("date") date: String): Call<List<List<Double>>>

    @DELETE("api/v1/mealitem/{mealItemID}")
    suspend fun deleteMealItem(@Path("mealItemID") mealItemID: String)

    @PUT("api/v1/mealitem/{mealItemID}")
    suspend fun updateMealItem(@Path("mealItemID") mealItemID: String,
                               @Query("itemName") mealItemName: String,
                               @Query("itemCalories") mealItemCalories: Double,
                               @Query("itemWeight") mealItemWeight: Double,
                               @Query("itemProteins") mealItemProteins: Double,
                               @Query("itemFat") mealItemFat: Double,
                               @Query("itemCarbs") mealItemCarbs: Double)

    @GET("api/v1/profile/nickname/{profileID}")
    fun getProfileNickname(@Path("profileID") profileID: String): Call<String>

    @GET("api/v1/profile/{profileID}")
    fun getProfile(@Path("profileID") profileID: String): Call<ProfileResponse>

    @POST("api/v1/profile")
    suspend fun postProfile(@Body userProfile: ProfileResponse)


}

