package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PatientCase
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogbookScreen(
    cases: List<PatientCase>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedDept: String,
    onDeptSelect: (String) -> Unit,
    selectedType: String,
    onTypeSelect: (String) -> Unit,
    onSelectCase: (PatientCase) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val departments = listOf(
        "All", "General Medicine", "General Surgery", "Pediatrics", 
        "Obstetrics & Gynecology", "Orthopedics", "Dental Sciences", 
        "Emergency Medicine", "Ophthalmology", "ENT"
    )

    val caseTypes = listOf("All", "OPD", "IPD", "OT", "Emergency")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("logbook_screen")
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("search_bar"),
            placeholder = { Text("Search ID, Diagnosis, Complaint...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = PolishPrimaryBlue) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = PolishSlate500)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = PolishPrimaryBlue,
                unfocusedBorderColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
            )
        )

        // Department Filter row
        Column(
            modifier = Modifier.padding(bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "DEPARTMENT / SPECIALTY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) PolishSlate500 else PolishSlate600,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                departments.forEach { dept ->
                    FilterChip(
                        selected = selectedDept == dept,
                        onClick = { onDeptSelect(dept) },
                        label = { Text(dept, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PolishPrimaryBlue,
                            selectedLabelColor = Color.White,
                            containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF),
                            labelColor = if (isDark) PolishSlate100 else PolishSlate900
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedDept == dept,
                            borderColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                            selectedBorderColor = PolishPrimaryBlue
                        )
                    )
                }
            }
        }

        // Case Type Filter row
        Column(
            modifier = Modifier.padding(bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "CLINICAL SETTING",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) PolishSlate500 else PolishSlate600,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                caseTypes.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { onTypeSelect(type) },
                        label = { Text(type, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PolishSecondaryTeal,
                            selectedLabelColor = Color.White,
                            containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF),
                            labelColor = if (isDark) PolishSlate100 else PolishSlate900
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedType == type,
                            borderColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                            selectedBorderColor = PolishSecondaryTeal
                        )
                    )
                }
            }
        }

        Divider(color = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))

        // Cases list
        if (cases.isEmpty()) {
            EmptyLogsView(hasFilters = searchQuery.isNotEmpty() || selectedDept != "All" || selectedType != "All")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("cases_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cases, key = { it.id }) { patientCase ->
                    CaseLogCard(
                        patientCase = patientCase,
                        onClick = { onSelectCase(patientCase) },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}

@Composable
fun CaseLogCard(
    patientCase: PatientCase,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    val dateString = formatter.format(Date(patientCase.admissionDate))
    val isDark = isSystemInDarkTheme()

    // Select outcome-specific colors
    val outcomeColor = when (patientCase.outcome) {
        "Active" -> PolishTertiaryOrange
        "Recovered" -> PolishSecondaryTeal
        "Discharged" -> PolishPrimaryBlue
        "Referred" -> Color(0xFF8B5CF6) // Purple
        else -> PolishCrimsonRed
    }

    // Secondary settings tag icon
    val caseIcon = when (patientCase.caseType) {
        "OPD" -> Icons.Default.EventNote
        "IPD" -> Icons.Default.Hotel
        "OT" -> Icons.Default.Healing
        else -> Icons.Default.NotificationImportant
    }
    
    val caseColor = when (patientCase.caseType.uppercase()) {
        "OPD" -> PolishSecondaryTeal
        "IPD" -> PolishPrimaryBlue
        "OT" -> PolishTertiaryOrange
        "EMERGENCY" -> PolishCrimsonRed
        else -> PolishPrimaryBlue
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("case_card_${patientCase.id}")
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: ID, settings & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(caseColor.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = caseIcon,
                            contentDescription = patientCase.caseType,
                            tint = caseColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column {
                        Text(
                            text = patientCase.patientId,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isDark) Color.White else PolishSlate900
                        )
                        Text(
                            text = patientCase.department,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) PolishSlate500 else PolishSlate600
                        )
                    }
                }

                Text(
                    text = dateString,
                    fontSize = 11.sp,
                    color = if (isDark) PolishSlate500 else PolishSlate600,
                    fontWeight = FontWeight.Bold
                )
            }

            // Body Row: Diagnosis & Chief Complaint
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = patientCase.diagnosis,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isDark) Color.White else PolishSlate900,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Complaint: ${patientCase.chiefComplaint}",
                    fontSize = 12.sp,
                    color = if (isDark) PolishSlate100.copy(alpha = 0.7f) else PolishSlate600,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Divider(color = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0).copy(alpha = 0.5f))

            // Footer Row: Age/Gender, Outcome pill, Competency tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Patient bio badge
                    Surface(
                        color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${patientCase.age}y / ${patientCase.gender}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = if (isDark) PolishSlate100 else PolishSlate600
                        )
                    }

                    // Competency tag if present
                    if (patientCase.competencyTag.isNotBlank()) {
                        Surface(
                            color = if (isDark) TintBlueDark else TintBlueLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = "Competency",
                                    tint = PolishPrimaryBlue,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = patientCase.competencyTag.takeBeforeColon(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PolishPrimaryBlue
                                )
                            }
                        }
                    }
                }

                // Outcome pill
                Surface(
                    color = outcomeColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = patientCase.outcome,
                        color = outcomeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyLogsView(hasFilters: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (hasFilters) Icons.Default.FilterListOff else Icons.Default.PostAdd,
                    contentDescription = "No Logs",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (hasFilters) "No Matching Logs" else "Your Clinical Logbook is Empty",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (hasFilters) 
                        "Try clearing your search query or modifying filters to locate your logs."
                    else 
                        "Log your clinical interactions during your MBBS/MS/MDS clinical rotations. Press the '+' button to log your first case.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

// Utility extension to pull the code part of a competency (e.g. "SU-15.1: Appendicitis" -> "SU-15.1")
private fun String.takeBeforeColon(): String {
    val colonIdx = this.indexOf(':')
    return if (colonIdx != -1) this.substring(0, colonIdx).trim() else this.trim()
}
