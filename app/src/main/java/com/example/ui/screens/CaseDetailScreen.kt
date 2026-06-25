package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PatientCase
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDetailScreen(
    patientCase: PatientCase,
    onBack: () -> Unit,
    onEdit: (PatientCase) -> Unit,
    onDelete: (PatientCase) -> Unit
) {
    var isPresentationMode by remember { mutableStateOf(false) }
    var currentSlideIndex by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    val isDark = isSystemInDarkTheme()

    val formatter = SimpleDateFormat("dd MMMM, yyyy - hh:mm a", Locale.getDefault())
    val dateString = formatter.format(Date(patientCase.admissionDate))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (isPresentationMode) "Case Presentation Helper" else "Case Record Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { if (isPresentationMode) isPresentationMode = false else onBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Presentation Mode Toggle
                    IconButton(
                        onClick = { 
                            isPresentationMode = !isPresentationMode
                            currentSlideIndex = 0
                        },
                        modifier = Modifier.testTag("toggle_presentation_mode")
                    ) {
                        Icon(
                            imageVector = if (isPresentationMode) Icons.Default.Description else Icons.Default.Slideshow,
                            contentDescription = "Toggle Presentation",
                            tint = PolishPrimaryBlue
                        )
                    }

                    if (!isPresentationMode) {
                        IconButton(onClick = { onEdit(patientCase) }, modifier = Modifier.testTag("edit_case_btn")) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { onDelete(patientCase) }, modifier = Modifier.testTag("delete_case_btn")) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = PolishCrimsonRed)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (isPresentationMode) {
            // Presentation View
            PresentationModeView(
                patientCase = patientCase,
                currentIndex = currentSlideIndex,
                onNext = { if (currentSlideIndex < 4) currentSlideIndex++ },
                onPrev = { if (currentSlideIndex > 0) currentSlideIndex-- },
                onExit = { isPresentationMode = false },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            // Standard Detail View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Patient profile Card
                PatientProfileHeaderCard(patientCase, dateString)

                // Clinical Case logs details
                ClinicalDetailsSection(patientCase)
            }
        }
    }
}

@Composable
fun PatientProfileHeaderCard(patientCase: PatientCase, dateString: String) {
    val isDark = isSystemInDarkTheme()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (isDark) TintBlueDark else TintBlueLight),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "PATIENT REGISTRATION ID",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PolishPrimaryBlue
                    )
                    Text(
                        text = patientCase.patientId,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                    Text(
                        text = "Logged on $dateString",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) PolishSlate500 else PolishSlate600,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Surface(
                    color = PolishPrimaryBlue,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = patientCase.caseType,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Divider(color = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HeaderMetadataItem(title = "AGE / GENDER", value = "${patientCase.age} Years / ${patientCase.gender}")
                HeaderMetadataItem(title = "WARD / UNIT", value = patientCase.wardOrUnit.ifBlank { "N/A" })
                HeaderMetadataItem(title = "OUTCOME", value = patientCase.outcome)
            }
        }
    }
}

@Composable
fun HeaderMetadataItem(title: String, value: String) {
    val isDark = isSystemInDarkTheme()
    Column {
        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) PolishSlate500 else PolishSlate600
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else PolishSlate900
        )
    }
}

@Composable
fun ClinicalDetailsSection(patientCase: PatientCase) {
    val isDark = isSystemInDarkTheme()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DetailBlock(
            title = "Chief Complaint",
            icon = Icons.Default.ChatBubbleOutline,
            content = patientCase.chiefComplaint
        )

        if (patientCase.history.isNotBlank()) {
            DetailBlock(
                title = "History of Present Illness & Past History",
                icon = Icons.Default.History,
                content = patientCase.history
            )
        }

        if (patientCase.clinicalFindings.isNotBlank()) {
            DetailBlock(
                title = "Clinical Examination Findings",
                icon = Icons.Default.Assignment,
                content = patientCase.clinicalFindings
            )
        }

        DetailBlock(
            title = "Clinical Diagnosis",
            icon = Icons.Default.MedicalInformation,
            content = patientCase.diagnosis,
            isAccent = true
        )

        if (patientCase.procedures.isNotBlank()) {
            DetailBlock(
                title = "Surgical / Clinical Procedures Performed",
                icon = Icons.Default.Healing,
                content = patientCase.procedures
            )
        }

        if (patientCase.treatment.isNotBlank()) {
            DetailBlock(
                title = "Treatment Given & Medical Management",
                icon = Icons.Default.LocalPharmacy,
                content = patientCase.treatment
            )
        }

        if (patientCase.competencyTag.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = PolishPrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "NMC / DCI Logbook Competency",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = PolishPrimaryBlue
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = patientCase.competencyTag,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else PolishSlate900
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Resident Role: ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) PolishSlate500 else PolishSlate600
                            )
                            Text(
                                text = patientCase.competencyRole,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else PolishSlate900
                            )
                        }
                    }
                }
            }
        }

        if (patientCase.notes.isNotBlank()) {
            DetailBlock(
                title = "Clinical Reflections & Learnings",
                icon = Icons.Default.Lightbulb,
                content = patientCase.notes
            )
        }
    }
}

@Composable
fun DetailBlock(
    title: String,
    icon: ImageVector,
    content: String,
    isAccent: Boolean = false
) {
    val isDark = isSystemInDarkTheme()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAccent) (if (isDark) Color(0xFF1A1F2C) else Color(0xFFF0FDF4)) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isAccent) PolishTertiaryOrange else PolishPrimaryBlue,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isAccent) PolishTertiaryOrange else PolishPrimaryBlue
                )
            }

            Text(
                text = content,
                fontSize = 13.sp,
                color = if (isDark) Color.White else PolishSlate900,
                lineHeight = 18.sp,
                fontWeight = if (isAccent) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun PresentationModeView(
    patientCase: PatientCase,
    currentIndex: Int,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalSlides = 5
    val slideTitle = when (currentIndex) {
        0 -> "I. Patient Bio & Particulars"
        1 -> "II. History of Present Illness (HPI)"
        2 -> "III. Clinical Examination Findings"
        3 -> "IV. Diagnosis & Management Plan"
        else -> "V. Academic Reflection & Competency"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Slate 900 Elegant Slide Dark Canvas
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Slide Progress Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(totalSlides) { idx ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (idx <= currentIndex) PolishPrimaryBlue else Color.White.copy(alpha = 0.15f)
                        )
                )
            }
        }

        // Slide Content Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), // Slate 800 Slide
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Slide Header
                Text(
                    text = slideTitle,
                    color = PolishSecondaryTeal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Divider(color = Color.White.copy(alpha = 0.1f))

                // Slide Body Content
                when (currentIndex) {
                    0 -> { // Patient bio
                        PresentationBulletItem(title = "Age / Gender", detail = "${patientCase.age} Years / ${patientCase.gender}")
                        PresentationBulletItem(title = "Registration ID", detail = patientCase.patientId)
                        PresentationBulletItem(title = "Admitting Department", detail = patientCase.department)
                        PresentationBulletItem(title = "Clinical Setting / Location", detail = "${patientCase.caseType} (${patientCase.wardOrUnit.ifBlank { "Not Specified" }})")
                        PresentationBulletItem(title = "Outcome / Disposition", detail = patientCase.outcome)
                    }
                    1 -> { // HPI
                        PresentationBulletText(title = "Chief Complaint", text = patientCase.chiefComplaint)
                        if (patientCase.history.isNotBlank()) {
                            PresentationBulletText(title = "Clinical History", text = patientCase.history)
                        } else {
                            PresentationBulletText(title = "Clinical History", text = "No supplementary history recorded.")
                        }
                    }
                    2 -> { // Clinical Findings
                        if (patientCase.clinicalFindings.isNotBlank()) {
                            PresentationBulletText(title = "Objective Physical Examination", text = patientCase.clinicalFindings)
                        } else {
                            PresentationBulletText(title = "Objective Physical Examination", text = "No objective diagnostic examination logged.")
                        }
                    }
                    3 -> { // Diagnosis and management
                        PresentationBulletItem(title = "Primary Clinical Diagnosis", detail = patientCase.diagnosis, highlight = true)
                        if (patientCase.procedures.isNotBlank()) {
                            PresentationBulletText(title = "Interventions / Surgery", text = patientCase.procedures)
                        }
                        if (patientCase.treatment.isNotBlank()) {
                            PresentationBulletText(title = "Medical Management Given", text = patientCase.treatment)
                        }
                    }
                    4 -> { // Learning reflections
                        if (patientCase.competencyTag.isNotBlank()) {
                            PresentationBulletItem(title = "NMC Competency Link", detail = patientCase.competencyTag)
                            PresentationBulletItem(title = "Candidate Involvement", detail = patientCase.competencyRole)
                        }
                        if (patientCase.notes.isNotBlank()) {
                            PresentationBulletText(title = "Logbook Learning Points", text = patientCase.notes)
                        } else {
                            PresentationBulletText(title = "Logbook Learning Points", text = "Reflections and clinical takeaways are pending.")
                        }
                    }
                }
            }
        }

        // Slides Controller Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Slide ${currentIndex + 1} of $totalSlides",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Previous button
                IconButton(
                    onClick = onPrev,
                    enabled = currentIndex > 0,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.1f),
                        disabledContainerColor = Color.White.copy(alpha = 0.02f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous Slide",
                        tint = if (currentIndex > 0) Color.White else Color.White.copy(alpha = 0.2f)
                    )
                }

                // Next or Exit button
                Button(
                    onClick = { if (currentIndex < 4) onNext() else onExit() },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishSecondaryTeal)
                ) {
                    Text(
                        text = if (currentIndex < 4) "Next" else "Done",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (currentIndex < 4) Icons.Default.ChevronRight else Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PresentationBulletItem(title: String, detail: String, highlight: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = detail,
            color = if (highlight) PolishTertiaryOrange else Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun PresentationBulletText(title: String, text: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.padding(top = 4.dp),
            fontFamily = FontFamily.SansSerif
        )
    }
}
