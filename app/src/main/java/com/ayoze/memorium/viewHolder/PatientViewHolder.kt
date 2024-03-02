package com.ayoze.memorium.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ayoze.memorium.databinding.ItemPatientBinding
import com.ayoze.memorium.model.Patient
import com.bumptech.glide.Glide

class PatientViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemPatientBinding.bind(view)

    fun render(patient: Patient, onClickListener: (Patient) -> Unit) {
        Glide.with(binding.ivPatient.context).load(patient.profilePicture).into(binding.ivPatient)

        val fullName = patient.name + ' ' + patient.lastName

        binding.tvPatientName.text = fullName
        binding.tvPatientEmail.text = patient.email
        binding.tvPatientBirthDate.text = patient.birthDate

        itemView.setOnClickListener { onClickListener(patient) }
    }
}