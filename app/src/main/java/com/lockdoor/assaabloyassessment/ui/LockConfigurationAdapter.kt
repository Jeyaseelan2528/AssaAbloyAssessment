package com.lockdoor.assaabloyassessment.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.lockdoor.assaabloyassessment.R


class LockConfigurationAdapter(private var list: List<LockModel>) :
    RecyclerView.Adapter<LockConfigurationAdapter.LockViewHolder>() {
    var spinnerList = ArrayList<String>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LockConfigurationAdapter.LockViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.lock_configuration_item_layout, parent, false)
        return LockViewHolder(v)
    }

    public fun getLockList(): List<LockModel> {
        return list.ifEmpty {
            ArrayList()
        }
    }

    override fun onBindViewHolder(holder: LockConfigurationAdapter.LockViewHolder, position: Int) {
        val lockModel = list[position]

        //Handling all the different UI type in one file itself
        //Based on the configuration name hide and show the UI and set the default data in the recycler view
        if (lockModel.configurationName.equals("Lock Release")) {
            holder.parentLayout.visibility = View.GONE
            holder.lockReleaseLayout.visibility = View.VISIBLE
            holder.lockAngleLayout.visibility = View.GONE
            holder.primaryArrow.visibility = View.GONE
            holder.secondaryArrow.visibility = View.GONE
            holder.lockReleaseText.text = lockModel.configurationName
            holder.lockReleaseSwitch.isChecked = lockModel.primaryDefaultValue.equals("on")
        } else if (lockModel.configurationName.equals("Lock Angle")) {
            holder.lockAngle.text = lockModel.configurationName
            holder.edtAngle.setText(lockModel.primaryDefaultValue + "\u00B0")
            holder.lockAngleLayout.visibility = View.VISIBLE
            holder.parentLayout.visibility = View.GONE
            holder.lockReleaseLayout.visibility = View.GONE
            holder.primaryArrow.visibility = View.GONE
            holder.secondaryArrow.visibility = View.GONE
        } else {
            holder.parentLayout.visibility = View.VISIBLE
            holder.lockReleaseLayout.visibility = View.GONE
            holder.lockAngleLayout.visibility = View.GONE
            holder.primaryArrow.visibility = View.VISIBLE
            holder.secondaryArrow.visibility = View.VISIBLE
            holder.configurationText.text = lockModel.configurationName
            spinnerList = ArrayList()
            lockModel.itemModel.forEach {
                spinnerList.add(it.itemName)
            }
            if (lockModel.configurationName.equals("Lock Release Time")) {
                holder.lockReleaseTimePrimaryEdit.visibility = View.VISIBLE
                holder.lockReleaseTimeSecondaryEdit.visibility = View.VISIBLE
                holder.primarySpinner.visibility = View.GONE
                holder.secondarySpinner.visibility = View.GONE
                holder.primaryArrow.visibility = View.GONE
                holder.secondaryArrow.visibility = View.GONE
                holder.lockReleaseTimePrimaryEdit.setText(lockModel.primaryDefaultValue)
                holder.lockReleaseTimeSecondaryEdit.setText(lockModel.secondaryDefaultValue)
            } else {
                holder.lockReleaseTimePrimaryEdit.visibility = View.GONE
                holder.lockReleaseTimeSecondaryEdit.visibility = View.GONE
                holder.primarySpinner.visibility = View.VISIBLE
                holder.secondarySpinner.visibility = View.VISIBLE
                holder.primaryArrow.visibility = View.VISIBLE
                holder.secondaryArrow.visibility = View.VISIBLE
                //Take the default value and set it to the selection by default
                val primaryDefault =
                    lockModel.itemModel.find { it.itemName == lockModel.primaryDefaultValue }?.itemId
                val secondaryDefault =
                    lockModel.itemModel.find { it.itemName == lockModel.secondaryDefaultValue }?.itemId
                //Load the value in the spinner adapter
                val primaryAdapter =
                    ArrayAdapter(
                        holder.itemView.context,
                        R.layout.spinner_layout,
                        spinnerList
                    )
                val secondaryAdapter =
                    ArrayAdapter(
                        holder.itemView.context,
                        R.layout.spinner_layout,
                        spinnerList
                    )
                holder.primarySpinner.adapter = primaryAdapter
                holder.secondarySpinner.adapter = secondaryAdapter
                holder.primarySpinner.setSelection(primaryDefault?.minus(1) ?: 0)
                holder.secondarySpinner.setSelection(secondaryDefault?.minus(1) ?: 0)
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun filterList(filterlist: ArrayList<LockModel>) {
        list = filterlist
        notifyDataSetChanged()
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class LockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var configurationText: TextView
        var primarySpinner: Spinner
        var secondarySpinner: Spinner
        var parentLayout: LinearLayout
        var lockReleaseLayout: LinearLayout
        var lockReleaseSwitch: SwitchCompat
        var lockReleaseText: TextView
        var lockReleaseTimePrimaryEdit: EditText
        var lockReleaseTimeSecondaryEdit: EditText
        var lockAngle: TextView
        var edtAngle: EditText
        var lockAngleLayout: LinearLayout
        var primaryArrow: ImageView
        var secondaryArrow: ImageView

        init {
            configurationText = itemView.findViewById(R.id.txtConfigurationName)
            primarySpinner = itemView.findViewById(R.id.primarySpinner)
            secondarySpinner = itemView.findViewById(R.id.secondarySpinner)
            parentLayout = itemView.findViewById(R.id.parentLayout)
            lockReleaseLayout = itemView.findViewById(R.id.lockReleaseLayout)
            lockReleaseSwitch = itemView.findViewById(R.id.lockReleaseSwitch)
            lockReleaseText = itemView.findViewById(R.id.txtLockRelease)
            lockReleaseTimePrimaryEdit = itemView.findViewById(R.id.primaryLockReleaseEditText)
            lockReleaseTimeSecondaryEdit = itemView.findViewById(R.id.secondaryLockReleaseEditText)
            lockAngle = itemView.findViewById(R.id.txtLockAngle)
            edtAngle = itemView.findViewById(R.id.edtAngle)
            lockAngleLayout = itemView.findViewById(R.id.lockAngleLayout)
            primaryArrow = itemView.findViewById(R.id.primaryArrow)
            secondaryArrow = itemView.findViewById(R.id.secondaryArrow)

            //Remove the focus for edit text when click on on recycler view
            lockReleaseTimePrimaryEdit.onFocusChangeListener =
                View.OnFocusChangeListener { view, b ->
                    if (!b) {
                        manageRange(
                            lockReleaseTimePrimaryEdit,
                            itemView.context,
                        )
                        list[adapterPosition].primaryDefaultValue =
                            lockReleaseTimePrimaryEdit.text.toString().trim().toDouble().toString()

                    }
                }
            //Remove the focus for edit text when click on on recycler view
            lockReleaseTimeSecondaryEdit.onFocusChangeListener =
                View.OnFocusChangeListener { view, b ->
                    if (!b) {
                        manageRange(
                            lockReleaseTimeSecondaryEdit,
                            itemView.context,
                        )
                        list[adapterPosition].secondaryDefaultValue =
                            lockReleaseTimeSecondaryEdit.text.toString().trim().toDouble()
                                .toString()
                    }
                }
            //Remove the focus for edit text when click on on recycler view
            itemView.setOnTouchListener { p0, p1 ->
                lockReleaseTimePrimaryEdit.clearFocus()
                lockReleaseTimeSecondaryEdit.clearFocus()
                edtAngle.clearFocus()
                itemView.requestFocus()
                false
            }

            edtAngle.setOnFocusChangeListener(object : OnFocusChangeListener {
                override fun onFocusChange(p0: View?, p1: Boolean) {
                    if (!p1) {
                        manageRangeAngle(edtAngle, itemView.context)
                        list[adapterPosition].primaryDefaultValue =
                            edtAngle.text.toString().trim().replace("°", "")
                    }
                }

            })
            //Store the Primary Door selection from the spinner
            primarySpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View,
                    position: Int,
                    id: Long
                ) {
                    list[adapterPosition].primaryDefaultValue =
                        list[adapterPosition].itemModel[position].itemName
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {

                }
            }
            //Store the secondary door selection from the spinner
            secondarySpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View,
                    position: Int,
                    id: Long
                ) {
                    list[adapterPosition].secondaryDefaultValue =
                        list[adapterPosition].itemModel[position].itemName
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {

                }
            }
            //Store the selection of user preference
            lockReleaseSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                list[adapterPosition].primaryDefaultValue = if (isChecked) "on" else "off"
                list[adapterPosition].secondaryDefaultValue = if (isChecked) "on" else "off"
            }
        }
    }
}

//Validate the Lock Angle data to be a valid one
private fun manageRangeAngle(editText: EditText, context: Context) {
    if (editText.text.isNotEmpty()) {
        editText.setText(
            editText.text.toString().trim() + "°"
        )
        val value: Int =
            editText.text.toString().trim().replace("°", "").toInt()
        if (value > 125) {
            editText.setText("125°")
            Toast.makeText(context, "The max angle is 125°", Toast.LENGTH_LONG).show()
        } else if (value < 65) {
            editText.setText("65°")
            Toast.makeText(context, "The min angle is 65°", Toast.LENGTH_LONG).show()
        }
    } else {
        editText.setText("0.1")
        Toast.makeText(context, "The min range is 0.1", Toast.LENGTH_LONG).show()
    }
}

//Validate the Lock Release time value for valid data
private fun manageRange(
    editText: EditText,
    context: Context,
) {
    if (editText.text.isNotEmpty()) {
        editText.setText(
            editText.text.toString().trim().toDouble()
                .toString()
        )
        val value: Double =
            editText.text.toString().trim().toDouble()
        if (value > 120.0) {
            editText.setText("120.0")
            Toast.makeText(context, "The max range is 2.0", Toast.LENGTH_LONG).show()
        } else if (value <= 0.0) {
            editText.setText("0.1")
            Toast.makeText(context, "The min range is 0.1", Toast.LENGTH_LONG).show()
        }
    } else {
        editText.setText("0.1")
        Toast.makeText(context, "The min range is 0.1", Toast.LENGTH_LONG).show()
    }
}
