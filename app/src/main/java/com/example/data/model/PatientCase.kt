package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patient_cases")
data class PatientCase(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: String,          // Initials or Registration No. (e.g., OPD-10492)
    val age: Int,
    val gender: String,             // Male, Female, Other
    val department: String,         // e.g., General Medicine, General Surgery, Pediatrics, Orthodontics
    val caseType: String,           // OPD, IPD, OT, Emergency
    val wardOrUnit: String,         // e.g., Ward 4B, Unit 2, Chair No. 12
    val chiefComplaint: String,     // Chief complaint
    val history: String = "",       // History of Present Illness (HPI) & Past history
    val clinicalFindings: String = "", // Examination findings (General & Systemic)
    val diagnosis: String,          // Diagnosis
    val procedures: String = "",    // Procedures / Interventions performed
    val treatment: String = "",     // Treatment given or planned
    val competencyTag: String = "", // NMC/DCI Competency code (e.g., SU-4.2, PE-3.1)
    val competencyRole: String = "Observed", // Observed, Assisted, Performed Independently
    val admissionDate: Long = System.currentTimeMillis(),
    val outcome: String = "Active", // Active, Recovered, Discharged, Referred, Deceased
    val notes: String = "",         // Personal clinical learning/reflections
    val lastUpdated: Long = System.currentTimeMillis()
)
