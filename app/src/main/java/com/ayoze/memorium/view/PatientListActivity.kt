package com.ayoze.memorium.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayoze.memorium.adapter.PatientAdapter
import com.ayoze.memorium.databinding.ActivityPatientListBinding
import com.ayoze.memorium.model.Patient
import com.ayoze.memorium.model.Roles
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class PatientListActivity : ComponentActivity() {

    private lateinit var db : FirebaseFirestore
    private lateinit var binding: ActivityPatientListBinding
    private lateinit var patientList: ArrayList<Patient>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        // Setup
        val bundle: Bundle? = intent.extras
        val uId: String? = bundle?.getString("uId")
        setUp(uId)
    }

    private fun initRecyclerView() {
        val manager = LinearLayoutManager(this)
        val decoration = DividerItemDecoration(this, manager.orientation)
        binding.rvPatient.layoutManager = manager

        patientList = arrayListOf()

        binding.rvPatient.adapter =
            PatientAdapter(patientList) { onItemSelected(it) }
        binding.rvPatient.addItemDecoration(decoration)

        EventChangeListener()
    }

    private fun setUp(uId: String?) {

    }

    private fun onItemSelected(patient: Patient) {
        Toast.makeText(this, patient.name + ' ' + patient.lastName, Toast.LENGTH_SHORT).show()
    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("users").whereEqualTo("rol", Roles.PACIENTE.name).addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent (
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if (error != null) {
                    Log.e("Error Firestore", error.message.toString())
                    return
                }

                for (dc : DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        patientList.add(dc.document.toObject(Patient::class.java))
                    }
                }

                binding.rvPatient.adapter?.notifyDataSetChanged()
            }
        })
    }

    companion object {
        private const val TAG = "PatientListActivity"
    }
}