package com.example.greenie.network

import com.example.greenie.model.Plant
import com.example.greenie.model.Search
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET("plants")
    suspend fun getPlants(): Response<List<Plant>>

    @GET("plants/search")
    suspend fun searchPlants(@Query("lat") lat : Double, @Query("lng") lng : Double, @Query("brightness") brightness : Float): Response<List<Plant>>

    @GET("users/{userId}/searches")
    suspend fun getSearches(@Path("userId") userId : String): Response<List<Search>>

    @POST("users/{userId}/searches")
    suspend fun saveSearch(@Path("userId") userId : String, @Body body : Search): Response<Search>
}