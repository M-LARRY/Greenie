package com.example.greenie.network

import com.example.greenie.model.Plant
import com.example.greenie.model.Search
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET("plants")
    fun getPlants(): Call<List<Plant>>

    @GET("plants/search")
    fun searchPlants(@Query("lat") lat : Double, @Query("lng") lng : Double, @Query("brightness") brightness : Float): Call<List<Plant>>

    @GET("users/{userId}/searches")
    fun getSearches(@Path("userId") userId : String): Call<List<Search>>

    @POST("users/{userId}/searches")
    fun saveSearch(@Path("userId") userId : String, @Body body : Search): Call<Search>
}