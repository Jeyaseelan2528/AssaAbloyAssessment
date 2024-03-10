package com.lockdoor.assaabloyassessment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lockdoor.assaabloyassessment.repository.LockRepository
import kotlinx.coroutines.launch

class LockViewModel : ViewModel() {
    var lockDoorDetails: MutableLiveData<JsonObject>? = MutableLiveData()
    fun getUser() {
        viewModelScope.launch {
            lockDoorDetails?.value = LockRepository().getLocDetails()
        }
    }
}