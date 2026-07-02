package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ConsultationCase
import com.example.data.model.Doctor
import com.example.data.model.PatientNotification
import com.example.data.repository.MedicalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object RoleSelection : Screen()
    
    // Patient Screens
    object PatientHome : Screen()
    data class PatientSubmitCase(val doctor: Doctor) : Screen()
    object PatientConsultations : Screen()
    object PatientNotifications : Screen() // New Screen
    
    // Doctor Screens
    object DoctorHome : Screen()
    data class DoctorCaseDetails(val consultationCase: ConsultationCase) : Screen()
}

class MedicalViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MedicalRepository
    
    init {
        val db = AppDatabase.getDatabase(application)
        repository = MedicalRepository(db.doctorDao(), db.consultationCaseDao(), db.patientNotificationDao())
        
        // Seed doctors if database is empty
        viewModelScope.launch {
            repository.seedDoctorsIfEmpty()
        }
    }
    
    // Expose reactive streams
    val doctors: StateFlow<List<Doctor>> = repository.activeDoctors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val allCases: StateFlow<List<ConsultationCase>> = repository.allCases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<PatientNotification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active screen navigation
    private val _currentScreen = MutableStateFlow<Screen>(Screen.RoleSelection)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Active Role selection
    private val _userRole = MutableStateFlow<String?>(null) // "PATIENT", "DOCTOR", or null
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    // Logged in doctor state
    private val _loggedInDoctor = MutableStateFlow<Doctor?>(null)
    val loggedInDoctor: StateFlow<Doctor?> = _loggedInDoctor.asStateFlow()

    // Filtered cases for logged in doctor
    val doctorFilteredCases: StateFlow<List<ConsultationCase>> = combine(allCases, _loggedInDoctor) { cases, doc ->
        if (doc == null) emptyList()
        else cases.filter { it.assignedDoctorId == doc.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Temporary Form state for Patient Case Submission
    val formPatientName = MutableStateFlow("")
    val formPatientAge = MutableStateFlow("")
    val formPatientGender = MutableStateFlow("ذكر")
    val formCaseDescription = MutableStateFlow("")
    val formAttachments = MutableStateFlow<List<String>>(emptyList()) // List of attached file descriptions

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun selectRole(role: String?) {
        _userRole.value = role
        if (role == null) {
            _loggedInDoctor.value = null
            _currentScreen.value = Screen.RoleSelection
        } else if (role == "PATIENT") {
            _currentScreen.value = Screen.PatientHome
        } else if (role == "DOCTOR") {
            // By default, let's pick the first doctor or show Doctor Home where they can select who they are
            _currentScreen.value = Screen.DoctorHome
        }
    }

    fun loginAsDoctor(doctor: Doctor) {
        _loggedInDoctor.value = doctor
        _currentScreen.value = Screen.DoctorHome
    }

    // Submit case
    fun submitCase(doctor: Doctor, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val age = formPatientAge.value.toIntOrNull() ?: 30
            val attachmentStr = if (formAttachments.value.isEmpty()) {
                ""
            } else {
                formAttachments.value.joinToString(", ")
            }

            val newCase = ConsultationCase(
                patientName = formPatientName.value.ifEmpty { "مريض فاعل خير" },
                patientAge = age,
                patientGender = formPatientGender.value,
                specialty = doctor.specialty,
                description = formCaseDescription.value.ifEmpty { "طلب استشارة عامة وتوجيه طبي" },
                attachmentPath = attachmentStr,
                status = "قيد المراجعة",
                assignedDoctorId = doctor.id,
                assignedDoctorName = doctor.name
            )

            repository.insertCase(newCase)
            
            // Clear form
            formPatientName.value = ""
            formPatientAge.value = ""
            formPatientGender.value = "ذكر"
            formCaseDescription.value = ""
            formAttachments.value = emptyList()

            onSuccess()
        }
    }

    // Doctor updates medical report
    fun provideMedicalGuidance(
        caseId: Int,
        guidance: String,
        isUrgentSurgery: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val case = repository.getCaseById(caseId)
            if (case != null) {
                val updated = case.copy(
                    doctorGuidance = guidance,
                    isUrgentSurgicalIntervention = isUrgentSurgery,
                    status = "تم الرد"
                )
                repository.updateCase(updated)

                // Add Notification
                val notification = PatientNotification(
                    caseId = case.id,
                    patientName = case.patientName,
                    doctorName = case.assignedDoctorName ?: "طبيب استشاري",
                    title = "تم الرد على استشارتك الطبية",
                    message = "قام الاستشاري ${case.assignedDoctorName ?: "المتابع لحالتك"} بكتابة التوجيه الطبي المعتمد لحالتك (${case.patientName}). يرجى مراجعة صفحة استشاراتي لمعرفة الرأي الثاني والتعليمات.",
                    type = "REPLY"
                )
                repository.insertNotification(notification)

                onSuccess()
            }
        }
    }

    // Doctor refers externally
    fun referCaseExternally(caseId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val case = repository.getCaseById(caseId)
            if (case != null) {
                val updated = case.copy(
                    isReferredExternally = true,
                    status = "قيد التنسيق الخارجي"
                )
                repository.updateCase(updated)

                // Add Notification
                val notification = PatientNotification(
                    caseId = case.id,
                    patientName = case.patientName,
                    doctorName = case.assignedDoctorName ?: "طبيب استشاري",
                    title = "تحويل الحالة للتنسيق الطبي الخارجي",
                    message = "تمت إحالة حالة (${case.patientName}) للتنسيق الطبي الخارجي مع مركز طبي متقدم لتقديم الدعم التخصصي اللازم.",
                    type = "REFERRAL"
                )
                repository.insertNotification(notification)

                onSuccess()
            }
        }
    }

    // Doctor schedules a virtual call
    fun scheduleVirtualCall(caseId: Int, dateTime: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val case = repository.getCaseById(caseId)
            if (case != null) {
                val updated = case.copy(
                    scheduledCallTime = dateTime,
                    status = "موعد اتصال مرئي"
                )
                repository.updateCase(updated)

                // Add Notification
                val notification = PatientNotification(
                    caseId = case.id,
                    patientName = case.patientName,
                    doctorName = case.assignedDoctorName ?: "طبيب استشاري",
                    title = "جدولة موعد اتصال مرئي مباشر",
                    message = "تمت جدولة موعد اتصال مرئي مباشر مع الاستشاري ${case.assignedDoctorName ?: "المتابع لحالتك"} في: $dateTime لمناقشة التوجيه الطبي لحالة (${case.patientName}).",
                    type = "SCHEDULE"
                )
                repository.insertNotification(notification)

                onSuccess()
            }
        }
    }

    // Manage Notifications
    fun markNotificationAsRead(id: Int) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun deleteNotification(id: Int) {
        viewModelScope.launch {
            repository.deleteNotificationById(id)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
        }
    }

    // Helper to add/remove attachment mock
    fun toggleAttachment(fileName: String) {
        val current = formAttachments.value.toMutableList()
        if (current.contains(fileName)) {
            current.remove(fileName)
        } else {
            current.add(fileName)
        }
        formAttachments.value = current
    }
}
