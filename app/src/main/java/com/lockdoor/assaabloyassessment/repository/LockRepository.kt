package com.lockdoor.assaabloyassessment.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.lockdoor.assaabloyassessment.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LockRepository {
    suspend fun getLocDetails() = RetrofitInstance.api.getLockService()
}