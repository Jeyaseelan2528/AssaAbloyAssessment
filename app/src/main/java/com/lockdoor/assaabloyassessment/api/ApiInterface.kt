package com.lockdoor.assaabloyassessment.api

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    @GET("v3/d5f5d613-474b-49c4-a7b0-7730e8f8f486")

    suspend fun getBreakingNews(): Response<JsonObject>
}