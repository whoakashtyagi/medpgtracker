package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PatientCase
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    totalCount: Int,
    opdCount: Int,
    ipdCount: Int,
    otCount: Int,
    emergencyCount: Int,
    cases: List<PatientCase>,
    onSelectCase: (PatientCase) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome & Banner card
        WelcomeBannerCard(totalCount)

        // Key stats summary cards (OPD, IPD, OT, Emergency)
        StatsGridSection(
            totalCount = totalCount,
            opdCount = opdCount,
            ipdCount = ipdCount,
            otCount = otCount,
            emergencyCount = emergencyCount
        )

        // Specialty / Department breakdown
        DepartmentDistributionSection(cases = cases)

        // Recent learning notes and cases log
        RecentLogsSection(cases = cases, onSelectCase = onSelectCase)
    }
}

@Composable
fun WelcomeBannerCard(totalCount: Int) {
    val isDark = isSystemInDarkTheme()
    val primaryColor = PolishPrimaryBlue
    val secondaryColor = PolishSecondaryTeal

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("welcome_banner_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (isDark) {
                            listOf(Color(0xFF1E293B), PolishDarkBg)
                        } else {
                            listOf(primaryColor, secondaryColor)
                        }
                    )
                )
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalInformation,
                            contentDescription = "Clinical Logbook",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "MedLog Pro • Residency Tracker",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        Text(
                            text = "Dr. Resident",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Divider(color = Color.White.copy(alpha = 0.2f), thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Active Case Logs",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "$totalCount cases logged",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "NMC Compliant",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatsGridSection(
    totalCount: Int,
    opdCount: Int,
    ipdCount: Int,
    otCount: Int,
    emergencyCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "CLINICAL CASE DISTRIBUTION",
            style = MaterialTheme.typography.labelMedium,
            color = if (isSystemInDarkTheme()) PolishSlate500 else PolishSlate600,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "OPD Logs",
                count = opdCount,
                total = totalCount,
                icon = Icons.Default.EventNote,
                color = PolishSecondaryTeal,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "IPD Admissions",
                count = ipdCount,
                total = totalCount,
                icon = Icons.Default.Hotel,
                color = PolishPrimaryBlue,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "OT / Procedures",
                count = otCount,
                total = totalCount,
                icon = Icons.Default.Healing,
                color = PolishTertiaryOrange,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Emergency Logs",
                count = emergencyCount,
                total = totalCount,
                icon = Icons.Default.NotificationImportant,
                color = PolishCrimsonRed,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    count: Int,
    total: Int,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (total > 0) count.toFloat() / total else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")
    val isDark = isSystemInDarkTheme()

    val containerColor = if (isDark) {
        color.copy(alpha = 0.08f)
    } else {
        color.copy(alpha = 0.04f)
    }
    val borderColor = color.copy(alpha = if (isDark) 0.25f else 0.15f)

    Card(
        modifier = modifier.testTag("stat_card_$title"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(color.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "$count",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else color
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) PolishSlate500 else PolishSlate600
                )

                LinearProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = color,
                    trackColor = color.copy(alpha = 0.1f)
                )

                Text(
                    text = "${(progress * 100).toInt()}% of total logs",
                    fontSize = 10.sp,
                    color = if (isDark) PolishSlate500 else PolishSlate500
                )
            }
        }
    }
}

@Composable
fun DepartmentDistributionSection(cases: List<PatientCase>) {
    val deptCounts = cases.groupBy { it.department }.mapValues { it.value.size }
    val total = cases.size
    val isDark = isSystemInDarkTheme()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("department_distribution_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "ROTATIONS & DEPARTMENT METRICS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDark) PolishSlate500 else PolishSlate600
            )

            if (total == 0) {
                Text(
                    text = "No cases logged yet to show rotation metrics.",
                    fontSize = 13.sp,
                    color = if (isDark) PolishSlate500 else PolishSlate600
                )
            } else {
                val departmentsList = listOf(
                    "General Medicine",
                    "General Surgery",
                    "Pediatrics",
                    "Obstetrics & Gynecology",
                    "Orthopedics",
                    "Dental Sciences",
                    "Emergency Medicine",
                    "Ophthalmology",
                    "ENT"
                )

                // Get active departments in logbook, sorted by frequency
                val sortedDepts = departmentsList.map { dept ->
                    dept to (deptCounts[dept] ?: 0)
                }.filter { it.second > 0 }.sortedByDescending { it.second }

                // If some custom department is logged, also include it
                val customDepts = deptCounts.filter { it.key !in departmentsList }
                val allSorted = (sortedDepts + customDepts.toList()).sortedByDescending { it.second }

                allSorted.forEach { (dept, count) ->
                    val ratio = count.toFloat() / total
                    DepartmentProgressRow(dept = dept, count = count, ratio = ratio)
                }
            }
        }
    }
}

@Composable
fun DepartmentProgressRow(dept: String, count: Int, ratio: Float) {
    val animatedRatio by animateFloatAsState(targetValue = ratio, label = "ratio")
    val isDark = isSystemInDarkTheme()
    
    // Choose appropriate color for department
    val color = when {
        dept.contains("Surgery", ignoreCase = true) -> PolishCrimsonRed
        dept.contains("Medicine", ignoreCase = true) -> PolishPrimaryBlue
        dept.contains("Pediatrics", ignoreCase = true) -> PolishSecondaryTeal
        dept.contains("Obstetrics", ignoreCase = true) -> Color(0xFFEC4899) // Rose
        dept.contains("Dental", ignoreCase = true) -> Color(0xFF06B6D4) // Cyan
        else -> PolishPrimaryBlue
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dept,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color.White else PolishSlate900
            )
            Text(
                text = "$count cases (${(ratio * 100).toInt()}%)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        LinearProgressIndicator(
            progress = animatedRatio,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = color,
            trackColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
        )
    }
}

@Composable
fun RecentLogsSection(cases: List<PatientCase>, onSelectCase: (PatientCase) -> Unit) {
    val isDark = isSystemInDarkTheme()
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LATEST LOGBOOK ENTRIES",
                style = MaterialTheme.typography.labelMedium,
                color = if (isDark) PolishSlate500 else PolishSlate600,
                fontWeight = FontWeight.Bold
            )
            if (cases.isNotEmpty()) {
                val newCount = minOf(6, cases.size)
                Text(
                    text = "$newCount Records Available",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PolishPrimaryBlue,
                    modifier = Modifier
                        .background(
                            if (isDark) TintBlueDark else TintBlueLight,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        if (cases.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your logbook is empty. Use the '+' button in the Logbook tab to create your first clinical case entry.",
                        color = if (isDark) PolishSlate500 else PolishSlate600,
                        fontSize = 13.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            // Take up to 3 recent cases
            val recentCases = cases.take(3)
            recentCases.forEach { patientCase ->
                val caseTypeColor = when (patientCase.caseType.uppercase()) {
                    "OPD" -> PolishSecondaryTeal
                    "IPD" -> PolishPrimaryBlue
                    "OT" -> PolishTertiaryOrange
                    "EMERGENCY" -> PolishCrimsonRed
                    else -> PolishPrimaryBlue
                }
                val caseTypeBg = if (isDark) {
                    caseTypeColor.copy(alpha = 0.15f)
                } else {
                    caseTypeColor.copy(alpha = 0.08f)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recent_case_card_${patientCase.id}"),
                    onClick = { onSelectCase(patientCase) },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = caseTypeBg,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = patientCase.caseType,
                                        color = caseTypeColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = patientCase.patientId,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDark) Color.White else PolishSlate900
                                )
                            }

                            Text(
                                text = "${patientCase.age}y / ${patientCase.gender}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) PolishSlate500 else PolishSlate600
                            )
                        }

                        Text(
                            text = patientCase.diagnosis,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isDark) Color.White else PolishSlate900,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (patientCase.notes.isNotBlank()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Key takeaway",
                                    tint = PolishPrimaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = patientCase.notes,
                                    fontSize = 12.sp,
                                    color = if (isDark) PolishSlate500 else PolishSlate600,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Floating Action Prompt from HTML
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (isDark) Color(0xFF1E293B) else PolishSlate900,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AssignmentLate,
                            contentDescription = "Thesis Milestone",
                            tint = PolishSecondaryTeal,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Thesis Milestone Log",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Case ${cases.size} required for submission",
                                color = if (isDark) PolishSlate500 else Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
