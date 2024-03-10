package com.lockdoor.assaabloyassessment.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.lockdoor.assaabloyassessment.R
import com.lockdoor.assaabloyassessment.viewmodel.LockViewModel

class MainActivity : AppCompatActivity() {
    lateinit var context: Context
    lateinit var lockViewModel: LockViewModel
    lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this@MainActivity
        //Instantiate View Model
        lockViewModel = ViewModelProvider(this).get(LockViewModel::class.java)
        progressBar = findViewById(R.id.progressBar);

        //ShowProgress bar during API call
        progressBar.visibility = View.VISIBLE
        //call getUser() to fetch the data from API
        lockViewModel.getUser()

        //Observe the data from view model
        lockViewModel.lockDoorDetails?.observe(this, Observer {
            progressBar.visibility = View.GONE
            var jsonObject : JsonObject = it
            Toast.makeText(applicationContext,jsonObject.toString(),Toast.LENGTH_LONG).show()
        })
    }
}