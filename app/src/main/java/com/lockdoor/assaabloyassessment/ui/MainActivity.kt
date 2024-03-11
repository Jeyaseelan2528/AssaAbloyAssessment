package com.lockdoor.assaabloyassessment.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.lockdoor.assaabloyassessment.R
import com.lockdoor.assaabloyassessment.viewmodel.LockViewModel
import java.util.Locale


class MainActivity : AppCompatActivity() {
    lateinit var context: Context
    lateinit var lockViewModel: LockViewModel
    lateinit var progressBar: ProgressBar
    lateinit var recyclerView: RecyclerView
    val finalList = ArrayList<LockModel>()
    lateinit var searchView: SearchView
    var lockAdapter : LockConfigurationAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this@MainActivity
        //Instantiate View Model
        lockViewModel = ViewModelProvider(this).get(LockViewModel::class.java)
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //ShowProgress bar during API call
        progressBar.visibility = View.VISIBLE
        //call getUser() to fetch the data from API
        lockViewModel.getUser()

        //Observe the data from view model
        lockViewModel.lockDoorDetails?.observe(this, Observer {
            progressBar.visibility = View.GONE
            val jsonObject: JsonObject = it

            //Parse Lock Voltage
            val lockVoltageObject = jsonObject.getAsJsonObject("lockVoltage")
            val lockVoltageArray = lockVoltageObject.getAsJsonArray("values")
            var id = 0
            var listItemModel: ListItemModel
            var listItems = ArrayList<ListItemModel>()
            lockVoltageArray.forEach {
                listItemModel = ListItemModel(id + 1, lockVoltageArray[id].asString)
                listItems.add(listItemModel)
                id++
            }
            finalList.add(
                LockModel(
                    "Lock Voltage",
                    listItems,
                    lockVoltageObject.get("default").asString,
                    lockVoltageObject.get("default").asString
                )
            )

            //Parse Lock Type
            val lockTypeObject = jsonObject.getAsJsonObject("lockType")
            val lockTypeArray = lockTypeObject.getAsJsonArray("values")
            id = 0
            listItems = ArrayList<ListItemModel>()
            lockTypeArray.forEach {
                listItemModel = ListItemModel(id + 1, lockTypeArray[id].asString)
                listItems.add(listItemModel)
                id++
            }
            finalList.add(
                LockModel(
                    "Lock Type",
                    listItems,
                    lockTypeObject.get("default").asString,
                    lockTypeObject.get("default").asString
                )
            )

            //Parse Lock Type
            val lockKickObject = jsonObject.getAsJsonObject("lockKick")
            val lockKickArray = lockKickObject.getAsJsonArray("values")
            id = 0
            listItems = ArrayList<ListItemModel>()
            lockKickArray.forEach {
                listItemModel = ListItemModel(id + 1, lockKickArray[id].asString)
                listItems.add(listItemModel)
                id++
            }
            finalList.add(
                LockModel(
                    "Lock Kick",
                    listItems,
                    lockKickObject.get("default").asString,
                    lockKickObject.get("default").asString
                )
            )

            //Parse Lock Release
            val lockReleaseObject = jsonObject.getAsJsonObject("lockRelease")
            val lockReleaseArray = lockReleaseObject.getAsJsonArray("values")
            id = 0
            listItems = ArrayList<ListItemModel>()
            lockReleaseArray.forEach {
                listItemModel = ListItemModel(id + 1, lockReleaseArray[id].asString)
                listItems.add(listItemModel)
                id++
            }
            finalList.add(
                LockModel(
                    "Lock Release",
                    listItems,
                    lockReleaseObject.get("default").asString,
                    lockReleaseObject.get("default").asString
                )
            )

            //Parse Lock Release Time
            val lockReleaseTime =
                jsonObject.getAsJsonObject("lockReleaseTime").getAsJsonObject("range")
            listItems = ArrayList()
            listItems.add(ListItemModel(1, lockReleaseTime.get("min").asString))
            listItems.add(ListItemModel(2, lockReleaseTime.get("max").asString))

            finalList.add(
                LockModel(
                    "Lock Release Time",
                    listItems,
                    jsonObject.getAsJsonObject("lockReleaseTime").get("default").asString,
                    jsonObject.getAsJsonObject("lockReleaseTime").get("default").asString
                )
            )

            //Parse Lock Angle
            val lockAngleObject = jsonObject.getAsJsonObject("lockAngle").getAsJsonObject("range")
            listItems = ArrayList()
            listItems.add(ListItemModel(1, lockAngleObject.get("min").asString))
            listItems.add(ListItemModel(2, lockAngleObject.get("max").asString))
            finalList.add(
                LockModel(
                    "Lock Angle",
                    listItems,
                    jsonObject.getAsJsonObject("lockAngle").get("default").asString,
                    jsonObject.getAsJsonObject("lockAngle").get("default").asString
                )
            )
            lockAdapter = LockConfigurationAdapter(finalList)
            recyclerView.adapter = lockAdapter
        })

        searchView.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                p0?.let { filter(it) }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                p0?.let { filter(it) }
                return false
            }

        })
    }

    private fun filter(text: String) {
        val filteredList: ArrayList<LockModel> = ArrayList()

        for (item in finalList) {
            if (item.configurationName.lowercase().contains(text.lowercase(Locale.getDefault()))) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            lockAdapter?.filterList(filteredList)
        }
    }
}