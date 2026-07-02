package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MedicalViewModel
import com.example.ui.Screen
import com.example.ui.screens.DoctorDashboard
import com.example.ui.screens.PatientDashboard
import com.example.ui.screens.RoleSelectionScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Force Right-to-Left (RTL) Layout direction to render Arabic beautifully
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val medicalViewModel: MedicalViewModel = viewModel()
                    val currentScreen by medicalViewModel.currentScreen.collectAsState()
                    val userRole by medicalViewModel.userRole.collectAsState()
                    val loggedInDoctor by medicalViewModel.loggedInDoctor.collectAsState()
                    val doctors by medicalViewModel.doctors.collectAsState()
                    val cases by medicalViewModel.allCases.collectAsState()

                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        val unusedPadding = innerPadding // we manage padding in screen levels for edge to edge
                        
                        when (currentScreen) {
                            is Screen.RoleSelection -> {
                                RoleSelectionScreen(
                                    viewModel = medicalViewModel,
                                    doctors = doctors,
                                    onSelectPatient = {
                                        medicalViewModel.selectRole("PATIENT")
                                    },
                                    onSelectDoctor = { selectedDoc ->
                                        medicalViewModel.loginAsDoctor(selectedDoc)
                                        medicalViewModel.selectRole("DOCTOR")
                                    }
                                )
                            }
                            is Screen.PatientHome, is Screen.PatientSubmitCase, is Screen.PatientConsultations, is Screen.PatientNotifications -> {
                                PatientDashboard(
                                    viewModel = medicalViewModel,
                                    doctors = doctors,
                                    cases = cases,
                                    currentScreen = currentScreen,
                                    onNavigateBack = {
                                        medicalViewModel.selectRole(null)
                                    }
                                )
                            }
                            is Screen.DoctorHome, is Screen.DoctorCaseDetails -> {
                                if (loggedInDoctor != null) {
                                    DoctorDashboard(
                                        viewModel = medicalViewModel,
                                        loggedInDoctor = loggedInDoctor!!,
                                        cases = cases,
                                        currentScreen = currentScreen,
                                        onNavigateBack = {
                                            if (currentScreen is Screen.DoctorCaseDetails) {
                                                medicalViewModel.navigateTo(Screen.DoctorHome)
                                            } else {
                                                medicalViewModel.selectRole(null)
                                            }
                                        }
                                    )
                                } else {
                                    // Safety fallback
                                    medicalViewModel.selectRole(null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
