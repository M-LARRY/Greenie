package com.example.greenie.network

import com.example.greenie.model.Nation
import com.example.greenie.model.Plant
import com.example.greenie.model.Search
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET("plants")
    suspend fun getPlants(): List<Plant>

    @GET("plants/search")
    suspend fun searchPlants(@Query("lat") lat : Double, @Query("lng") lng : Double, @Query("brightness") brightness : Float): List<Plant>

    @GET("nations/search")
    suspend fun searchNations(@Query("lat") lat : Double, @Query("lng") lng : Double): List<Nation>

    @GET("users/{userId}/searches")
    suspend fun getSearches(@Path("userId") userId : String): List<Search>

    @POST("users/{userId}/searches")
    suspend fun saveSearch(@Path("userId") userId : String, @Body body : Search): Search
}