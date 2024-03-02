package com.ayoze.memorium.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayoze.memorium.R
import com.ayoze.memorium.viewHolder.PatientViewHolder
import com.ayoze.memorium.model.Patient

class PatientAdapter(
    private val patientsList: List<Patient>, private val onClickListener: (Patient) -> Unit
) : RecyclerView.Adapter<PatientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PatientViewHolder(layoutInflater.inflate(R.layout.item_patient, parent, false))
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val item = patientsList[position]
        holder.render(item, onClickListener)
    }

    override fun getItemCount(): Int = patientsList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}