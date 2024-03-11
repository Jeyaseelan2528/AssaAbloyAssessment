package com.lockdoor.assaabloyassessment.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
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

    override fun onBindViewHolder(holder: LockConfigurationAdapter.LockViewHolder, position: Int) {
        val lockModel = list[position]

        if (lockModel.configurationName.equals("Lock Release")) {
            holder.parentLayout.visibility = View.GONE
            holder.lockReleaseLayout.visibility = View.VISIBLE
            holder.lockReleaseText.text = lockModel.configurationName
            holder.lockReleaseSwitch.isChecked = lockModel.primaryDefaultValue.equals("on")
        } else {
            holder.parentLayout.visibility = View.VISIBLE
            holder.lockReleaseLayout.visibility = View.GONE
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
                holder.lockReleaseTimePrimaryEdit.setText(lockModel.primaryDefaultValue)
                holder.lockReleaseTimeSecondaryEdit.setText(lockModel.secondaryDefaultValue)
            } else {
                holder.lockReleaseTimePrimaryEdit.visibility = View.GONE
                holder.lockReleaseTimeSecondaryEdit.visibility = View.GONE
                holder.primarySpinner.visibility = View.VISIBLE
                holder.secondarySpinner.visibility = View.VISIBLE
                val primaryDefault =
                    lockModel.itemModel.find { it.itemName == lockModel.primaryDefaultValue }?.itemId
                val secondaryDefault =
                    lockModel.itemModel.find { it.itemName == lockModel.secondaryDefaultValue }?.itemId
                val primaryAdapter =
                    ArrayAdapter(
                        holder.itemView.context,
                        android.R.layout.simple_spinner_item,
                        spinnerList
                    )
                val secondaryAdapter =
                    ArrayAdapter(
                        holder.itemView.context,
                        android.R.layout.simple_spinner_item,
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

            lockReleaseTimePrimaryEdit.onFocusChangeListener =
                View.OnFocusChangeListener { view, b ->
                    if (!b) {
                        lockReleaseTimePrimaryEdit.setText(
                            lockReleaseTimePrimaryEdit.text.toString().trim().toDouble().toString()
                        )
                        manageRange(lockReleaseTimePrimaryEdit,itemView.context)
                    }
                }

            lockReleaseTimeSecondaryEdit.onFocusChangeListener =
                View.OnFocusChangeListener { view, b ->
                    if (!b) {
                        lockReleaseTimeSecondaryEdit.setText(
                            lockReleaseTimeSecondaryEdit.text.toString().trim().toDouble()
                                .toString()
                        )
                        manageRange(lockReleaseTimeSecondaryEdit,itemView.context)
                    }
                }

        }
    }

    private fun manageRange(editText: EditText,context: Context) {
        if (editText.text.isNotEmpty()) {
            val value: Double =
                editText.text.toString().trim().toDouble()
            if (value > 120.0) {
                editText.setText("120.0")
                Toast.makeText(context,"The max range is 2.0",Toast.LENGTH_LONG).show()
            } else if (value <= 0.0) {
                editText.setText("0.1")
                Toast.makeText(context,"The min range is 0.1",Toast.LENGTH_LONG).show()
            }
        }
    }
}