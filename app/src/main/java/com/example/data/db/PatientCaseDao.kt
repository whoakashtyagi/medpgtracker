package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PatientCase
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientCaseDao {
    @Query("SELECT * FROM patient_cases ORDER BY admissionDate DESC")
    fun getAllCases(): Flow<List<PatientCase>>

    @Query("SELECT * FROM patient_cases WHERE id = :id LIMIT 1")
    suspend fun getCaseById(id: Int): PatientCase?

    @Query("""
        SELECT * FROM patient_cases 
        WHERE (patientId LIKE '%' || :query || '%' OR diagnosis LIKE '%' || :query || '%' OR chiefComplaint LIKE '%' || :query || '%')
        ORDER BY admissionDate DESC
    """)
    fun searchCases(query: String): Flow<List<PatientCase>>

    @Query("SELECT * FROM patient_cases WHERE department = :dept ORDER BY admissionDate DESC")
    fun getCasesByDepartment(dept: String): Flow<List<PatientCase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(patientCase: PatientCase): Long

    @Update
    suspend fun updateCase(patientCase: PatientCase)

    @Delete
    suspend fun deleteCase(patientCase: PatientCase)

    @Query("SELECT COUNT(*) FROM patient_cases")
    fun getCaseCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM patient_cases WHERE caseType = 'OPD'")
    fun getOpdCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM patient_cases WHERE caseType = 'IPD'")
    fun getIpdCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM patient_cases WHERE caseType = 'OT'")
    fun getOtCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM patient_cases WHERE caseType = 'Emergency'")
    fun getEmergencyCount(): Flow<Int>
}
