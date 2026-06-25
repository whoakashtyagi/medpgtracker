package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.model.PatientCase
import com.example.ui.screens.AddEditCaseScreen
import com.example.ui.screens.CaseDetailScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LogbookScreen
import com.example.ui.screens.ReferenceScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.PatientCaseViewModel

sealed interface TabScreen {
    object Dashboard : TabScreen
    object Logbook : TabScreen
    object Reference : TabScreen
}

class MainActivity : ComponentActivity() {
    private val viewModel: PatientCaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainContainer(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(viewModel: PatientCaseViewModel) {
    var activeTab by remember { mutableStateOf<TabScreen>(TabScreen.Dashboard) }
    var selectedDetailCase by remember { mutableStateOf<PatientCase?>(null) }
    var editingCase by remember { mutableStateOf<PatientCase?>(null) }
    var isAddingNewCase by remember { mutableStateOf(false) }

    // Core Reactive States from Room ViewModel
    val casesList by viewModel.patientCases.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedDeptFilter by viewModel.selectedDepartment.collectAsState()
    val selectedTypeFilter by viewModel.selectedCaseType.collectAsState()

    val totalCount by viewModel.totalCount.collectAsState()
    val opdCount by viewModel.opdCount.collectAsState()
    val ipdCount by viewModel.ipdCount.collectAsState()
    val otCount by viewModel.otCount.collectAsState()
    val emergencyCount by viewModel.emergencyCount.collectAsState()

    // Determine current navigation screen state
    when {
        isAddingNewCase -> {
            AddEditCaseScreen(
                patientCase = null,
                onSave = { newCase ->
                    viewModel.saveCase(newCase)
                    isAddingNewCase = false
                },
                onCancel = { isAddingNewCase = false }
            )
        }
        editingCase != null -> {
            AddEditCaseScreen(
                patientCase = editingCase,
                onSave = { updatedCase ->
                    viewModel.saveCase(updatedCase)
                    editingCase = null
                },
                onCancel = { editingCase = null }
            )
        }
        selectedDetailCase != null -> {
            CaseDetailScreen(
                patientCase = selectedDetailCase!!,
                onBack = { selectedDetailCase = null },
                onEdit = { kase ->
                    editingCase = kase
                    selectedDetailCase = null
                },
                onDelete = { kase ->
                    viewModel.deleteCase(kase)
                    selectedDetailCase = null
                }
            )
        }
        else -> {
            // Main Standard Navigation Dashboard Shell
            Scaffold(
                modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = when (activeTab) {
                                    TabScreen.Dashboard -> "MedLog PG - Residency Tracker"
                                    TabScreen.Logbook -> "Clinical Case Logbook"
                                    TabScreen.Reference -> "NMC Competency Logbook"
                                }
                            )
                        }
                    )
                },
                bottomBar = {
                    NavigationBar(
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        NavigationBarItem(
                            selected = activeTab == TabScreen.Dashboard,
                            onClick = { activeTab = TabScreen.Dashboard },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                            label = { Text("Dashboard") },
                            modifier = Modifier.testTag("tab_dashboard")
                        )
                        NavigationBarItem(
                            selected = activeTab == TabScreen.Logbook,
                            onClick = { activeTab = TabScreen.Logbook },
                            icon = { Icon(Icons.Default.Book, contentDescription = "Logbook") },
                            label = { Text("Logbook") },
                            modifier = Modifier.testTag("tab_logbook")
                        )
                        NavigationBarItem(
                            selected = activeTab == TabScreen.Reference,
                            onClick = { activeTab = TabScreen.Reference },
                            icon = { Icon(Icons.Default.School, contentDescription = "NMC Tracker") },
                            label = { Text("NMC Tracker") },
                            modifier = Modifier.testTag("tab_tracker")
                        )
                    }
                },
                floatingActionButton = {
                    if (activeTab == TabScreen.Logbook) {
                        FloatingActionButton(
                            onClick = { isAddingNewCase = true },
                            modifier = Modifier.testTag("add_case_fab")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Case")
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (activeTab) {
                        TabScreen.Dashboard -> {
                            DashboardScreen(
                                totalCount = totalCount,
                                opdCount = opdCount,
                                ipdCount = ipdCount,
                                otCount = otCount,
                                emergencyCount = emergencyCount,
                                cases = casesList,
                                onSelectCase = { selectedDetailCase = it }
                            )
                        }
                        TabScreen.Logbook -> {
                            LogbookScreen(
                                cases = casesList,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                selectedDept = selectedDeptFilter,
                                onDeptSelect = { viewModel.setSelectedDepartment(it) },
                                selectedType = selectedTypeFilter,
                                onTypeSelect = { viewModel.setSelectedCaseType(it) },
                                onSelectCase = { selectedDetailCase = it }
                            )
                        }
                        TabScreen.Reference -> {
                            ReferenceScreen(cases = casesList)
                        }
                    }
                }
            }
        }
    }
}
