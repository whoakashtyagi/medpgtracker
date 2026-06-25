package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PatientCase
import com.example.ui.theme.*

data class ClinicalCompetency(
    val code: String,
    val description: String,
    val department: String,
    val targetRole: String
)

@Composable
fun ReferenceScreen(cases: List<PatientCase>) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDeptFilter by remember { mutableStateOf("All") }
    val isDark = isSystemInDarkTheme()

    val competencies = listOf(
        // Surgery
        ClinicalCompetency("SU-15.1", "Acute Appendicitis (Diagnosis, differential diagnosis and surgical management)", "General Surgery", "Assisted/Performed"),
        ClinicalCompetency("SU-17.2", "Inguinal/Femoral Hernia (Clinical diagnosis, complications, and surgical principles)", "General Surgery", "Assisted"),
        ClinicalCompetency("SU-22.3", "Primary wound debridement and layered suturing of clean/dirty wounds", "General Surgery", "Performed Independently"),
        ClinicalCompetency("SU-28.1", "Incision & Drainage of superficial abscess under local anesthesia", "General Surgery", "Performed Independently"),
        
        // Medicine
        ClinicalCompetency("IM-1.2", "Perform comprehensive general physical examination and record vital signs", "General Medicine", "Performed Independently"),
        ClinicalCompetency("IM-5.3", "Diagnosis and emergency management of Diabetic Ketoacidosis (DKA)", "General Medicine", "Observed/Assisted"),
        ClinicalCompetency("IM-12.2", "Hypertensive emergencies assessment, target organ damage screen and drug titration", "General Medicine", "Performed Independently"),
        ClinicalCompetency("IM-18.4", "Interpretation of 12-lead Electrocardiogram (ECG) for ischemia, blocks, hypertrophy", "General Medicine", "Performed Independently"),

        // Pediatrics
        ClinicalCompetency("PE-2.1", "Developmental mile-stones assessment and growth charting in infants/children", "Pediatrics", "Performed Independently"),
        ClinicalCompetency("PE-5.1", "Immunization schedule counseling, cold chain management, and vaccine injection", "Pediatrics", "Performed Independently"),
        ClinicalCompetency("PE-15.3", "Assess degree of dehydration in acute gastroenteritis and prepare ORS/IV therapy", "Pediatrics", "Performed Independently"),

        // OBG
        ClinicalCompetency("OB-4.3", "Conduction of normal labor, active management of third stage, episiotomy suturing", "Obstetrics & Gynecology", "Assisted/Performed"),
        ClinicalCompetency("OB-12.1", "Routine antenatal check-up, symphysio-fundal height, obstetric grips, fetal heart monitoring", "Obstetrics & Gynecology", "Performed Independently"),
        ClinicalCompetency("OB-15.2", "Contraceptive options counseling and intrauterine contraceptive device (IUCD) insertion", "Obstetrics & Gynecology", "Performed Independently"),

        // Dental
        ClinicalCompetency("DS-3.1", "Surgical extraction of non-restorable teeth and partially/fully impacted third molars", "Dental Sciences", "Performed Independently"),
        ClinicalCompetency("DS-1.4", "Clinical oral examination, dental charting, diagnosis of caries, pulpitis, and cavity prep", "Dental Sciences", "Performed Independently"),
        ClinicalCompetency("DS-5.2", "Periodontal charting, supra-gingival and sub-gingival scaling and root planing", "Dental Sciences", "Performed Independently")
    )

    // Compute how many times each competency is logged in the user's database
    val logCounts = remember(cases) {
        cases.map { it.competencyTag.trim() }.groupBy { tag ->
            // Try to match either exact code or code prefix
            competencies.firstOrNull { comp -> 
                tag.startsWith(comp.code, ignoreCase = true) 
            }?.code ?: ""
        }.filter { it.key.isNotEmpty() }.mapValues { it.value.size }
    }

    val filteredCompetencies = competencies.filter { comp ->
        val matchesSearch = comp.code.contains(searchQuery, ignoreCase = true) ||
                comp.description.contains(searchQuery, ignoreCase = true)
        val matchesDept = selectedDeptFilter == "All" || comp.department == selectedDeptFilter
        matchesSearch && matchesDept
    }

    val deptsList = listOf("All", "General Medicine", "General Surgery", "Pediatrics", "Obstetrics & Gynecology", "Dental Sciences")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("reference_screen")
    ) {
        // Guidance Box
        Card(
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) TintBlueDark else TintBlueLight),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Guidance Info",
                    tint = PolishPrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "National Medical Commission (NMC) Logbook Tracker",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PolishPrimaryBlue
                    )
                    Text(
                        text = "This utility lists mandatory residency competencies. Create patient case logs with matching competency tags to automatically mark them as complete.",
                        fontSize = 12.sp,
                        color = if (isDark) PolishSlate100.copy(alpha = 0.8f) else PolishSlate600,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            placeholder = { Text("Search competency code or text...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PolishPrimaryBlue) },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = PolishPrimaryBlue,
                unfocusedBorderColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Department Filter Tabs
        ScrollableTabRow(
            selectedTabIndex = deptsList.indexOf(selectedDeptFilter).coerceAtLeast(0),
            edgePadding = 16.dp,
            divider = {},
            containerColor = Color.Transparent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[deptsList.indexOf(selectedDeptFilter).coerceAtLeast(0)]),
                    color = PolishPrimaryBlue,
                    height = 3.dp
                )
            }
        ) {
            deptsList.forEachIndexed { idx, dept ->
                Tab(
                    selected = selectedDeptFilter == dept,
                    onClick = { selectedDeptFilter = dept },
                    text = { 
                        Text(
                            text = dept, 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Bold,
                            color = if (selectedDeptFilter == dept) PolishPrimaryBlue else if (isDark) PolishSlate100 else PolishSlate600
                        ) 
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Competency List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredCompetencies, key = { it.code }) { comp ->
                val logsRecorded = logCounts[comp.code] ?: 0
                val isCompleted = logsRecorded > 0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("competency_card_${comp.code}"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Surface(
                                    color = PolishPrimaryBlue.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = comp.code,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = PolishPrimaryBlue,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = comp.department,
                                    fontSize = 11.sp,
                                    color = if (isDark) PolishSlate500 else PolishSlate600,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            // Dynamic Status Badge
                            val badgeBg = if (isCompleted) {
                                if (isDark) Color(0xFF064E3B) else Color(0xFFD1FAE5)
                            } else {
                                if (isDark) Color(0xFF78350F) else Color(0xFFFEF3C7)
                            }
                            val badgeText = if (isCompleted) PolishSecondaryTeal else PolishTertiaryOrange

                            Surface(
                                color = badgeBg,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.PendingActions,
                                        contentDescription = if (isCompleted) "Completed" else "Pending",
                                        tint = badgeText,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (isCompleted) "$logsRecorded Logged" else "Pending",
                                        color = badgeText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Text(
                            text = comp.description,
                            fontSize = 14.sp,
                            color = if (isDark) Color.White else PolishSlate900,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Mandatory Role: ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) PolishSlate500 else PolishSlate600
                                )
                                Text(
                                    text = comp.targetRole,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else PolishSlate900
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
