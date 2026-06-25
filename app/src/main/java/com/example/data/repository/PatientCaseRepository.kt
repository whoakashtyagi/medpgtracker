package com.example.data.repository

import com.example.data.db.PatientCaseDao
import com.example.data.model.PatientCase
import kotlinx.coroutines.flow.Flow

class PatientCaseRepository(private val patientCaseDao: PatientCaseDao) {
    val allCases: Flow<List<PatientCase>> = patientCaseDao.getAllCases()
    val totalCaseCount: Flow<Int> = patientCaseDao.getCaseCount()
    val opdCount: Flow<Int> = patientCaseDao.getOpdCount()
    val ipdCount: Flow<Int> = patientCaseDao.getIpdCount()
    val otCount: Flow<Int> = patientCaseDao.getOtCount()
    val emergencyCount: Flow<Int> = patientCaseDao.getEmergencyCount()

    fun searchCases(query: String): Flow<List<PatientCase>> {
        return if (query.isBlank()) {
            patientCaseDao.getAllCases()
        } else {
            patientCaseDao.searchCases(query)
        }
    }

    fun getCasesByDepartment(department: String): Flow<List<PatientCase>> {
        return patientCaseDao.getCasesByDepartment(department)
    }

    suspend fun getCaseById(id: Int): PatientCase? {
        return patientCaseDao.getCaseById(id)
    }

    suspend fun insertCase(patientCase: PatientCase): Long {
        return patientCaseDao.insertCase(patientCase)
    }

    suspend fun updateCase(patientCase: PatientCase) {
        patientCaseDao.updateCase(patientCase)
    }

    suspend fun deleteCase(patientCase: PatientCase) {
        patientCaseDao.deleteCase(patientCase)
    }
}
