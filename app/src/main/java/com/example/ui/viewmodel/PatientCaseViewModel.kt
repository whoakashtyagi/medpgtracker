package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.PatientCase
import com.example.data.repository.PatientCaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PatientCaseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PatientCaseRepository

    // Search and filter parameters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedDepartment = MutableStateFlow("All")
    val selectedDepartment = _selectedDepartment.asStateFlow()

    private val _selectedCaseType = MutableStateFlow("All")
    val selectedCaseType = _selectedCaseType.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = PatientCaseRepository(database.patientCaseDao())
        preloadSampleDataIfNeeded()
    }

    // Reactive Case logs filtered by Search Query, Department, and Case Type
    val patientCases: StateFlow<List<PatientCase>> = combine(
        _searchQuery,
        _selectedDepartment,
        _selectedCaseType,
        repository.allCases
    ) { query, dept, type, allCases ->
        allCases.filter { kase ->
            val matchesQuery = query.isBlank() || 
                    kase.patientId.contains(query, ignoreCase = true) ||
                    kase.diagnosis.contains(query, ignoreCase = true) ||
                    kase.chiefComplaint.contains(query, ignoreCase = true) ||
                    kase.competencyTag.contains(query, ignoreCase = true)
            
            val matchesDept = dept == "All" || kase.department == dept
            val matchesType = type == "All" || kase.caseType == type

            matchesQuery && matchesDept && matchesType
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Aggregate statistics from Repository Flows
    val totalCount: StateFlow<Int> = repository.totalCaseCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val opdCount: StateFlow<Int> = repository.opdCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val ipdCount: StateFlow<Int> = repository.ipdCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val otCount: StateFlow<Int> = repository.otCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val emergencyCount: StateFlow<Int> = repository.emergencyCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Editing State Management
    private val _selectedCase = MutableStateFlow<PatientCase?>(null)
    val selectedCase = _selectedCase.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedDepartment(department: String) {
        _selectedDepartment.value = department
    }

    fun setSelectedCaseType(caseType: String) {
        _selectedCaseType.value = caseType
    }

    fun selectCase(patientCase: PatientCase?) {
        _selectedCase.value = patientCase
    }

    fun saveCase(patientCase: PatientCase) {
        viewModelScope.launch {
            if (patientCase.id == 0) {
                repository.insertCase(patientCase)
            } else {
                repository.updateCase(patientCase)
            }
        }
    }

    fun deleteCase(patientCase: PatientCase) {
        viewModelScope.launch {
            repository.deleteCase(patientCase)
            if (_selectedCase.value?.id == patientCase.id) {
                _selectedCase.value = null
            }
        }
    }

    private fun preloadSampleDataIfNeeded() {
        viewModelScope.launch {
            // Check if database is empty by observing the first item of the flow
            val currentList = repository.searchCases("").stateIn(viewModelScope).value
            // Let's retrieve direct count
            repository.insertCase(
                PatientCase(
                    patientId = "OPD-2026/041",
                    age = 22,
                    gender = "Male",
                    department = "Dental Sciences",
                    caseType = "OPD",
                    wardOrUnit = "Unit 1, Dental Chair 4",
                    chiefComplaint = "Pain and swelling in the lower right back tooth region since 4 days.",
                    history = "Intermittent dull aching pain on chewing. No history of systemic illness. No medications.",
                    clinicalFindings = "Deep carious distal surface of mandibular right third molar (48), tenderness on percussion, mild operculitis of surrounding mucosa.",
                    diagnosis = "Symptomatic Periapical Periodontitis secondary to Dental Caries (48) with partial impaction",
                    procedures = "Surgical extraction of 48 under Local Anesthesia (2% Lignocaine with 1:80000 adrenaline). Sutures placed.",
                    treatment = "Post-op analgesics (Tab Ibuprofen 400mg + Paracetamol 325mg TDS) & Amoxicillin 500mg TDS for 5 days. Warm saline gargles post 24 hours.",
                    competencyTag = "DS-3.1: Surgical extraction",
                    competencyRole = "Performed Independently",
                    outcome = "Recovered",
                    notes = "Gained practical confidence in administering inferior alveolar nerve block and elevation of mucoperiosteal flap for impacted teeth.",
                    admissionDate = System.currentTimeMillis() - 86400000L * 4 // 4 days ago
                )
            )

            repository.insertCase(
                PatientCase(
                    patientId = "IPD-Surg-409",
                    age = 28,
                    gender = "Female",
                    department = "General Surgery",
                    caseType = "OT",
                    wardOrUnit = "Female Surgical Ward, Bed 12",
                    chiefComplaint = "Acute severe pain in the right lower quadrant of abdomen for 24 hours, associated with nausea and low-grade fever.",
                    history = "Pain started around the umbilicus and migrated to the right iliac fossa. Vomited twice. L.M.P was 10 days ago (normal).",
                    clinicalFindings = "Tenderness in Right Iliac Fossa, Rebound tenderness present at McBurney's point. Rovsing's sign positive. Pulse: 98/min, Temp: 99.4 F.",
                    diagnosis = "Acute Appendicitis (Clinical + Ultrasonographic correlation showing aperistaltic, non-compressible appendix of 7.2mm diameter)",
                    procedures = "Open Appendectomy performed under General Anesthesia. Appendix was inflamed, turgid, with no perforation.",
                    treatment = "IV fluids, IV Ceftriaxone 1g BD, IV Metronidazole 500mg TDS, and analgesic support post-op.",
                    competencyTag = "SU-15.1: Appendicitis",
                    competencyRole = "Assisted",
                    outcome = "Discharged",
                    notes = "Observed McBurney's incision, identification of cecum and appendix, ligation of mesoappendix and appendiceal base, and layered closure.",
                    admissionDate = System.currentTimeMillis() - 86400000L * 2 // 2 days ago
                )
            )

            repository.insertCase(
                PatientCase(
                    patientId = "EMR-Med-103",
                    age = 45,
                    gender = "Male",
                    department = "General Medicine",
                    caseType = "Emergency",
                    wardOrUnit = "ICU, Bed 2",
                    chiefComplaint = "Altered sensorium, deep rapid breathing, severe dehydration, and abdominal pain since last night.",
                    history = "Known patient of Type 2 Diabetes Mellitus on irregular oral medication. History of high-grade fever and cough for past 3 days.",
                    clinicalFindings = "Patient stuporous, Glasgow Coma Scale 11/15. Kussmaul respiration. Fruity/acetone odor in breath. Dehydration +++, BP: 90/60 mmHg, Pulse: 112/min, Chest: bilateral crepitations in right lung base.",
                    diagnosis = "Diabetic Ketoacidosis (DKA) precipitated by Community-Acquired Pneumonia",
                    procedures = "Arterial Blood Gas (ABG) monitoring, Central venous line insertion, Urinary catheterization.",
                    treatment = "Aggressive IV fluid resuscitation (Normal Saline 1L in 1st hour, then controlled), continuous low-dose IV insulin infusion (0.1 units/kg/hr), Potassium replacement, IV Piperacillin-Tazobactam 4.5g TDS.",
                    competencyTag = "IM-5.3: Diabetic Ketoacidosis",
                    competencyRole = "Observed",
                    outcome = "Active",
                    notes = "Learned the critical importance of hourly monitoring of blood glucose, serum electrolytes, and fluid balance in managing endocrine emergencies.",
                    admissionDate = System.currentTimeMillis() - 3600000L * 12 // 12 hours ago
                )
            )
        }
    }
}
