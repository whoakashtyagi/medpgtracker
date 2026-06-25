package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PatientCase
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCaseScreen(
    patientCase: PatientCase?,
    onSave: (PatientCase) -> Unit,
    onCancel: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isDark = isSystemInDarkTheme()

    // Form states
    var patientId by remember { mutableStateOf(patientCase?.patientId ?: "") }
    var ageStr by remember { mutableStateOf(patientCase?.age?.toString() ?: "") }
    var gender by remember { mutableStateOf(patientCase?.gender ?: "Male") }
    var department by remember { mutableStateOf(patientCase?.department ?: "General Medicine") }
    var caseType by remember { mutableStateOf(patientCase?.caseType ?: "OPD") }
    var wardOrUnit by remember { mutableStateOf(patientCase?.wardOrUnit ?: "") }
    var chiefComplaint by remember { mutableStateOf(patientCase?.chiefComplaint ?: "") }
    var history by remember { mutableStateOf(patientCase?.history ?: "") }
    var clinicalFindings by remember { mutableStateOf(patientCase?.clinicalFindings ?: "") }
    var diagnosis by remember { mutableStateOf(patientCase?.diagnosis ?: "") }
    var procedures by remember { mutableStateOf(patientCase?.procedures ?: "") }
    var treatment by remember { mutableStateOf(patientCase?.treatment ?: "") }
    var competencyTag by remember { mutableStateOf(patientCase?.competencyTag ?: "") }
    var competencyRole by remember { mutableStateOf(patientCase?.competencyRole ?: "Observed") }
    var outcome by remember { mutableStateOf(patientCase?.outcome ?: "Active") }
    var notes by remember { mutableStateOf(patientCase?.notes ?: "") }

    // Validation state
    var showErrors by remember { mutableStateOf(false) }

    val isPatientIdValid = patientId.isNotBlank()
    val isDiagnosisValid = diagnosis.isNotBlank()
    val isChiefComplaintValid = chiefComplaint.isNotBlank()
    val isAgeValid = ageStr.toIntOrNull() != null && ageStr.toInt() > 0

    val isFormValid = isPatientIdValid && isDiagnosisValid && isChiefComplaintValid && isAgeValid

    // Dropdown expanded states
    var genderExpanded by remember { mutableStateOf(false) }
    var deptExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var roleExpanded by remember { mutableStateOf(false) }
    var outcomeExpanded by remember { mutableStateOf(false) }
    var competencyExpanded by remember { mutableStateOf(false) }

    // Static competency suggestions based on selected department
    val competenciesMap = remember {
        mapOf(
            "General Surgery" to listOf(
                "SU-15.1: Acute Appendicitis",
                "SU-17.2: Inguinal/Femoral Hernia",
                "SU-22.3: Primary Wound Debridement & Suturing",
                "SU-28.1: Incision & Drainage of Abscess"
            ),
            "General Medicine" to listOf(
                "IM-1.2: General Physical Examination & Vitals",
                "IM-5.3: Diabetic Ketoacidosis (DKA)",
                "IM-12.2: Hypertensive Emergency Titration",
                "IM-18.4: 12-Lead ECG Interpretation"
            ),
            "Pediatrics" to listOf(
                "PE-2.1: Pediatric Developmental Milestones assessment",
                "PE-5.1: Immunization scheduling and execution",
                "PE-15.3: Dehydration & Rehydration therapy"
            ),
            "Obstetrics & Gynecology" to listOf(
                "OB-4.3: Conduction of Normal Labor & delivery",
                "OB-12.1: Antenatal counseling & routine checkups",
                "OB-15.2: Contraceptive options counseling & IUCD insertion"
            ),
            "Dental Sciences" to listOf(
                "DS-3.1: Surgical extraction of impacted teeth",
                "DS-1.4: Dental charting & Caries cavity preparation",
                "DS-5.2: Scaling and root planing of gingiva"
            )
        )
    }

    val suggestedCompetencies = competenciesMap[department] ?: emptyList()

    val formFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedBorderColor = PolishPrimaryBlue,
        unfocusedBorderColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (patientCase == null) "Log New Clinical Case" else "Edit Clinical Case Log",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (isFormValid) {
                                val savedCase = PatientCase(
                                    id = patientCase?.id ?: 0,
                                    patientId = patientId.trim(),
                                    age = ageStr.toIntOrNull() ?: 0,
                                    gender = gender,
                                    department = department,
                                    caseType = caseType,
                                    wardOrUnit = wardOrUnit.trim(),
                                    chiefComplaint = chiefComplaint.trim(),
                                    history = history.trim(),
                                    clinicalFindings = clinicalFindings.trim(),
                                    diagnosis = diagnosis.trim(),
                                    procedures = procedures.trim(),
                                    treatment = treatment.trim(),
                                    competencyTag = competencyTag,
                                    competencyRole = competencyRole,
                                    outcome = outcome,
                                    notes = notes.trim(),
                                    admissionDate = patientCase?.admissionDate ?: System.currentTimeMillis(),
                                    lastUpdated = System.currentTimeMillis()
                                )
                                onSave(savedCase)
                            } else {
                                showErrors = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PolishPrimaryBlue),
                        modifier = Modifier.testTag("save_case_button")
                    ) {
                        Text("Save Case", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // General Info Card
            FormSectionHeader(title = "Patient Profile & Particulars", icon = Icons.Default.Person)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = patientId,
                    onValueChange = { patientId = it },
                    label = { Text("Registration ID / Initials") },
                    placeholder = { Text("e.g. OPD-104 or S.K.") },
                    modifier = Modifier.weight(1.2f).testTag("input_patient_id"),
                    isError = showErrors && !isPatientIdValid,
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = formFieldColors
                )

                OutlinedTextField(
                    value = ageStr,
                    onValueChange = { ageStr = it.filter { char -> char.isDigit() } },
                    label = { Text("Age (Years)") },
                    placeholder = { Text("e.g. 25") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.8f).testTag("input_age"),
                    isError = showErrors && !isAgeValid,
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = formFieldColors
                )
            }

            // Gender & Setting Selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Gender Dropdown
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PolishPrimaryBlue) },
                        modifier = Modifier.fillMaxWidth().clickable { genderExpanded = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = formFieldColors
                    )
                    DropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.45f)
                    ) {
                        listOf("Male", "Female", "Other").forEach { g ->
                            DropdownMenuItem(
                                text = { Text(g) },
                                onClick = {
                                    gender = g
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }

                // Setting Dropdown (OPD/IPD/OT/Emergency)
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = caseType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Setting") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PolishPrimaryBlue) },
                        modifier = Modifier.fillMaxWidth().clickable { typeExpanded = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = formFieldColors
                    )
                    DropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.45f)
                    ) {
                        listOf("OPD", "IPD", "OT", "Emergency").forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = {
                                    caseType = t
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Department & Ward
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = department,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Admitting Department / Rotation") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PolishPrimaryBlue) },
                    modifier = Modifier.fillMaxWidth().clickable { deptExpanded = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = formFieldColors
                )
                DropdownMenu(
                    expanded = deptExpanded,
                    onDismissRequest = { deptExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    listOf(
                        "General Medicine", "General Surgery", "Pediatrics",
                        "Obstetrics & Gynecology", "Orthopedics", "Dental Sciences",
                        "Emergency Medicine", "Ophthalmology", "ENT"
                    ).forEach { dept ->
                        DropdownMenuItem(
                            text = { Text(dept) },
                            onClick = {
                                department = dept
                                deptExpanded = false
                                // Reset competency tag if it doesn't apply to the new department
                                competencyTag = ""
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = wardOrUnit,
                onValueChange = { wardOrUnit = it },
                label = { Text("Ward / Unit / Clinical Chair (Optional)") },
                placeholder = { Text("e.g. Male Surgical Ward, Bed 14") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = formFieldColors
            )

            // Clinical Case Details Card
            FormSectionHeader(title = "Clinical Presentation details", icon = Icons.Default.MedicalInformation)

            OutlinedTextField(
                value = chiefComplaint,
                onValueChange = { chiefComplaint = it },
                label = { Text("Chief Complaint") },
                placeholder = { Text("Describe main symptoms and duration (e.g., Fever and cough since 5 days)") },
                modifier = Modifier.fillMaxWidth().height(100.dp).testTag("input_chief_complaint"),
                isError = showErrors && !isChiefComplaintValid,
                maxLines = 4,
                shape = RoundedCornerShape(16.dp),
                colors = formFieldColors
            )

            OutlinedTextField(
                value = history,
                onValueChange = { history = it },
                label = { Text("Clinical History (Optional)") },
                placeholder = { Text("HPI (History of Present Illness), Past clinical history, comorbidities...") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 6,
                shape = RoundedCornerShape(16.dp),
                colors = formFieldColors
            )

            OutlinedTextField(
                value = clinicalFindings,
                onValueChange = { clinicalFindings = it },
                label = { Text("Physical & Clinical Examination Findings (Optional)") },
                placeholder = { Text("Vitals, general physical examination, systemic/local examination findings...") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 6,
                shape = RoundedCornerShape(16.dp),
                colors = formFieldColors
            )

            OutlinedTextField(
                value = diagnosis,
                onValueChange = { diagnosis = it },
                label = { Text("Clinical Diagnosis (Provisional or Final)") },
                placeholder = { Text("e.g. Lobar Pneumonia / Acute Calculous Cholecystitis") },
                modifier = Modifier.fillMaxWidth().testTag("input_diagnosis"),
                isError = showErrors && !isDiagnosisValid,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = formFieldColors
            )

            OutlinedTextField(
                value = procedures,
                onValueChange = { procedures = it },
                label = { Text("Clinical / Surgical Procedures Performed (Optional)") },
                placeholder = { Text("e.g. Suturing, Incision & Drainage, RCT, Appendectomy, LP...") },
                modifier = Modifier.fillMaxWidth().height(80.dp),
                shape = RoundedCornerShape(16.dp),
                colors = formFieldColors
            )

            OutlinedTextField(
                value = treatment,
                onValueChange = { treatment = it },
                label = { Text("Treatment Given / Pharmacotherapy (Optional)") },
                placeholder = { Text("Antibiotics, pain meds, IV fluids, discharge prescriptions...") },
                modifier = Modifier.fillMaxWidth().height(80.dp),
                shape = RoundedCornerShape(16.dp),
                colors = formFieldColors
            )

            // Academic Logbook & Competencies
            FormSectionHeader(title = "Academic Logbook & Competencies", icon = Icons.Default.School)

            // Dynamic Competency tagging
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = competencyTag,
                        onValueChange = { competencyTag = it },
                        label = { Text("NMC / DCI Competency Tag") },
                        placeholder = { Text("Type tag or select suggestion (e.g. SU-15.1)") },
                        trailingIcon = {
                            if (suggestedCompetencies.isNotEmpty()) {
                                IconButton(onClick = { competencyExpanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Suggestions", tint = PolishPrimaryBlue)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = formFieldColors
                    )

                    if (suggestedCompetencies.isNotEmpty()) {
                        DropdownMenu(
                            expanded = competencyExpanded,
                            onDismissRequest = { competencyExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            suggestedCompetencies.forEach { comp ->
                                DropdownMenuItem(
                                    text = { Text(comp, fontSize = 12.sp) },
                                    onClick = {
                                        competencyTag = comp
                                        competencyExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (suggestedCompetencies.isNotEmpty()) {
                    Text(
                        text = "Suggested based on chosen department: ${department}",
                        fontSize = 11.sp,
                        color = PolishPrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            // Student role & Outcome Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Role Dropdown
                Box(modifier = Modifier.weight(1.1f)) {
                    OutlinedTextField(
                        value = competencyRole,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Candidate Role") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PolishPrimaryBlue) },
                        modifier = Modifier.fillMaxWidth().clickable { roleExpanded = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = formFieldColors
                    )
                    DropdownMenu(
                        expanded = roleExpanded,
                        onDismissRequest = { roleExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.5f)
                    ) {
                        listOf("Observed", "Assisted", "Performed Independently").forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r, fontSize = 13.sp) },
                                onClick = {
                                    competencyRole = r
                                    roleExpanded = false
                                }
                            )
                        }
                    }
                }

                // Outcome Dropdown
                Box(modifier = Modifier.weight(0.9f)) {
                    OutlinedTextField(
                        value = outcome,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Outcome") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PolishPrimaryBlue) },
                        modifier = Modifier.fillMaxWidth().clickable { outcomeExpanded = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = formFieldColors
                    )
                    DropdownMenu(
                        expanded = outcomeExpanded,
                        onDismissRequest = { outcomeExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.4f)
                    ) {
                        listOf("Active", "Recovered", "Discharged", "Referred", "Deceased").forEach { o ->
                            DropdownMenuItem(
                                text = { Text(o, fontSize = 13.sp) },
                                onClick = {
                                    outcome = o
                                    outcomeExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Clinical Takeaways & Personal Reflections") },
                placeholder = { Text("Write key academic learnings, questions raised, or clinical points observed during this case...") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(16.dp),
                colors = formFieldColors
            )

            if (showErrors && !isFormValid) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Validation Errors Present:",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.sp
                        )
                        if (!isPatientIdValid) {
                            Text("- Registration ID / Patient ID is required", color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 11.sp)
                        }
                        if (!isAgeValid) {
                            Text("- Age must be a positive integer", color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 11.sp)
                        }
                        if (!isChiefComplaintValid) {
                            Text("- Chief Complaint is required", color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 11.sp)
                        }
                        if (!isDiagnosisValid) {
                            Text("- Clinical Diagnosis is required", color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PolishPrimaryBlue,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = PolishPrimaryBlue
        )
    }
}
