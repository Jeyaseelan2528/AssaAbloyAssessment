package com.lockdoor.assaabloyassessment.ui

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    var finalList = ArrayList<LockModel>()
    lateinit var searchView: SearchView
    var lockAdapter: LockConfigurationAdapter? = null
    lateinit var btnOk: Button
    lateinit var sharedPreferences: SharedPreferences
    var id = 0
    lateinit var listItemModel: ListItemModel
    var listItems = ArrayList<ListItemModel>()
    lateinit var editor: SharedPreferences.Editor
    lateinit var jsonObject: JsonObject
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this@MainActivity
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        //Instantiate View Model
        lockViewModel = ViewModelProvider(this).get(LockViewModel::class.java)
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        supportActionBar?.title = "Lock Settings"

        btnOk = findViewById(R.id.btnOk);


        //ShowProgress bar during API call
        progressBar.visibility = View.VISIBLE
        //call getUser() to fetch the data from API
        lockViewModel.getUser()

        //Observe the data from view model
        lockViewModel.lockDoorDetails?.observe(this, Observer {
            progressBar.visibility = View.GONE
            jsonObject = it
            //Separating the parsing logic to be called in different place and to reduce cognitive complexity
            parseDefaultResponse(jsonObject)
        })

        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                p0?.let { filter(it) }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                p0?.let { filter(it) }
                return false
            }
        })

        btnOk.setOnClickListener {
            val list = lockAdapter?.getLockList()

            //Storing the selected values in shared preferences for future usage
            if (list!!.isNotEmpty()) {
                for (item in list) {
                    if (item.configurationName.equals("Lock Voltage")) {
                        editor.putString("lockVoltagePrimary", item.primaryDefaultValue)
                        editor.putString("lockVoltageSecondary", item.secondaryDefaultValue)
                    } else if (item.configurationName.equals("Lock Type")) {
                        editor.putString("lockTypePrimary", item.primaryDefaultValue)
                        editor.putString("lockTypeSecondary", item.secondaryDefaultValue)
                    } else if (item.configurationName.equals("Lock Kick")) {
                        editor.putString("lockKickPrimary", item.primaryDefaultValue)
                        editor.putString("lockKickSecondary", item.secondaryDefaultValue)
                    } else if (item.configurationName.equals("Lock Release")) {
                        editor.putString("lockReleasePrimary", item.primaryDefaultValue)
                        editor.putString("lockReleaseSecondary", item.secondaryDefaultValue)
                    } else if (item.configurationName.equals("Lock Release Time")) {
                        editor.putString("lockReleaseTimePrimary", item.primaryDefaultValue)
                        editor.putString("lockReleaseTimeSecondary", item.secondaryDefaultValue)
                    } else if (item.configurationName.equals("Lock Angle")) {
                        editor.putString("lockAnglePrimary", item.primaryDefaultValue)
                        editor.putString("lockAngleSecondary", item.secondaryDefaultValue)
                    }
                }
            }
            editor.apply()
            editor.commit()

            //Show Confirmation Dialog
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            val customLayout: View = layoutInflater.inflate(R.layout.custom_dialog, null)
            builder.setView(customLayout)
            val btnOk: Button = customLayout.findViewById(R.id.btnClose)

            val dialog: AlertDialog = builder.create()
            btnOk.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun parseDefaultResponse(jsonObject: JsonObject) {
        finalList = ArrayList()
        parseLockVoltageAndSetDefaultValue(jsonObject)
        parseLockTypeAndSetDefaultValue(jsonObject)
        parseLockKickAndSetDefaultValue(jsonObject)
        parseLockReleaseAndSetDefaultValue(jsonObject)
        parseLockReleaseTimeAndSetDefaultValue(jsonObject)
        parseLockAngleAndSetDefaultValue(jsonObject)
        lockAdapter = LockConfigurationAdapter(finalList)
        recyclerView.adapter = lockAdapter
        lockAdapter!!.notifyDataSetChanged()
    }

    private fun parseLockAngleAndSetDefaultValue(jsonObject: JsonObject) {
        //Parse Lock Angle
        val lockAngleObject = jsonObject.getAsJsonObject("lockAngle").getAsJsonObject("range")
        listItems = ArrayList()
        listItems.add(ListItemModel(1, lockAngleObject.get("min").asString))
        listItems.add(ListItemModel(2, lockAngleObject.get("max").asString))
        //If the user has already made selection on the data then show the selected ones else show the default one comes from API
        if (sharedPreferences.getString("lockAnglePrimary", "")!!.isNotEmpty()) {
            finalList.add(
                LockModel(
                    "Lock Angle",
                    listItems,
                    sharedPreferences.getString("lockAnglePrimary", "")!!,
                    sharedPreferences.getString("lockAngleSecondary", "")!!
                )
            )
        } else {
            finalList.add(
                LockModel(
                    "Lock Angle",
                    listItems,
                    jsonObject.getAsJsonObject("lockAngle").get("default").asString,
                    jsonObject.getAsJsonObject("lockAngle").get("default").asString
                )
            )
        }

    }

    private fun parseLockReleaseTimeAndSetDefaultValue(jsonObject: JsonObject) {
        //Parse Lock Release Time
        val lockReleaseTime = jsonObject.getAsJsonObject("lockReleaseTime").getAsJsonObject("range")
        listItems = ArrayList()
        listItems.add(ListItemModel(1, lockReleaseTime.get("min").asString))
        listItems.add(ListItemModel(2, lockReleaseTime.get("max").asString))
        //If the user has already made selection on the data then show the selected ones else show the default one comes from API
        if (sharedPreferences.getString("lockReleaseTimePrimary", "")!!.isNotEmpty()) {
            finalList.add(
                LockModel(
                    "Lock Release Time",
                    listItems,
                    sharedPreferences.getString("lockReleaseTimePrimary", "")!!,
                    sharedPreferences.getString("lockReleaseTimeSecondary", "")!!
                )
            )
        } else {
            finalList.add(
                LockModel(
                    "Lock Release Time",
                    listItems,
                    jsonObject.getAsJsonObject("lockReleaseTime").get("default").asString,
                    jsonObject.getAsJsonObject("lockReleaseTime").get("default").asString
                )
            )
        }

    }

    private fun parseLockReleaseAndSetDefaultValue(jsonObject: JsonObject) {
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
        //If the user has already made selection on the data then show the selected ones else show the default one comes from API
        if (sharedPreferences.getString("lockReleasePrimary", "")!!.isNotEmpty()) {
            finalList.add(
                LockModel(
                    "Lock Release",
                    listItems,
                    sharedPreferences.getString("lockReleasePrimary", "")!!,
                    sharedPreferences.getString("lockReleaseSecondary", "")!!
                )
            )
        } else {
            finalList.add(
                LockModel(
                    "Lock Release",
                    listItems,
                    lockReleaseObject.get("default").asString,
                    lockReleaseObject.get("default").asString
                )
            )
        }

    }

    private fun parseLockKickAndSetDefaultValue(jsonObject: JsonObject) {
        //Parse Lock Type
        val lockKickObject = jsonObject.getAsJsonObject("lockKick")
        val lockKickArray = lockKickObject.getAsJsonArray("values")
        id = 0
        listItems = ArrayList()
        lockKickArray.forEach {
            listItemModel = ListItemModel(id + 1, lockKickArray[id].asString)
            listItems.add(listItemModel)
            id++
        }
        //If the user has already made selection on the data then show the selected ones else show the default one comes from API
        if (sharedPreferences.getString("lockKickPrimary", "")!!.isNotEmpty()) {
            finalList.add(
                LockModel(
                    "Lock Kick",
                    listItems,
                    sharedPreferences.getString("lockKickPrimary", "")!!,
                    sharedPreferences.getString("lockKickSecondary", "")!!
                )
            )
        } else {
            finalList.add(
                LockModel(
                    "Lock Kick",
                    listItems,
                    lockKickObject.get("default").asString,
                    lockKickObject.get("default").asString
                )
            )
        }
    }

    private fun parseLockTypeAndSetDefaultValue(jsonObject: JsonObject) {
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
        //If the user has already made selection on the data then show the selected ones else show the default one comes from API
        if (sharedPreferences.getString("lockTypePrimary", "")!!.isNotEmpty()) {
            finalList.add(
                LockModel(
                    "Lock Type",
                    listItems,
                    sharedPreferences.getString("lockTypePrimary", "")!!,
                    sharedPreferences.getString("lockTypeSecondary", "")!!
                )
            )
        } else {
            finalList.add(
                LockModel(
                    "Lock Type",
                    listItems,
                    lockTypeObject.get("default").asString,
                    lockTypeObject.get("default").asString
                )
            )
        }

    }

    private fun parseLockVoltageAndSetDefaultValue(jsonObject: JsonObject) {
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
//If the user has already made selection on the data then show the selected ones else show the default one comes from API
        if (sharedPreferences.getString("lockVoltagePrimary", "")!!.isNotEmpty()) {
            finalList.add(
                LockModel(
                    "Lock Voltage",
                    listItems,
                    sharedPreferences.getString("lockVoltagePrimary", "")!!,
                    sharedPreferences.getString("lockVoltageSecondary", "")!!
                )
            )
        } else {
            finalList.add(
                LockModel(
                    "Lock Voltage",
                    listItems,
                    lockVoltageObject.get("default").asString,
                    lockVoltageObject.get("default").asString
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        //Reset the stored values when required
        if (id == R.id.reset) {
            //Show Confirmation dialog before resetting the values
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setMessage("Are you sure you want to clear the lock settings parameters")
            builder.setTitle("Warning !!")
            builder.setCancelable(false)
            builder.setPositiveButton(
                "Yes"
            ) { dialog: DialogInterface?, _: Int ->
                dialog?.cancel()
                editor.clear()
                editor.apply()
                parseDefaultResponse(jsonObject)
            }
            builder.setNegativeButton(
                "No"
            ) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }


        return super.onOptionsItemSelected(item)
    }

    //Filter the particular setting from the list
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