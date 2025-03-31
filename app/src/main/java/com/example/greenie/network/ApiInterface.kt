package com.example.greenie.network

import com.example.greenie.model.Plant
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {
    @GET("plants")
    fun getPost(): Call<List<Plant>>
}