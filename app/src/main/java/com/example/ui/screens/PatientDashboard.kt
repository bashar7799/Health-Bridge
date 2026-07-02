package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ConsultationCase
import com.example.data.model.Doctor
import com.example.ui.MedicalViewModel
import com.example.ui.Screen
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PatientDashboard(
    viewModel: MedicalViewModel,
    doctors: List<Doctor>,
    cases: List<ConsultationCase>,
    currentScreen: Screen,
    onNavigateBack: () -> Unit
) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (currentScreen is Screen.PatientSubmitCase || currentScreen is Screen.PatientConsultations || currentScreen is Screen.PatientNotifications) {
                            viewModel.navigateTo(Screen.PatientHome)
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (currentScreen) {
                            is Screen.PatientHome -> "جسر الصحة - بوصلة الشفاء"
                            is Screen.PatientSubmitCase -> "تقديم حالة جديدة"
                            is Screen.PatientConsultations -> "استشاراتي الطبية"
                            is Screen.PatientNotifications -> "التنبيهات والاشعارات"
                            else -> "بوابة المريض"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp
                        )
                    )
                }
            }
        },
        bottomBar = {
            if (currentScreen !is Screen.PatientSubmitCase) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen is Screen.PatientHome,
                        onClick = {
                            viewModel.navigateTo(Screen.PatientHome)
                        },
                        icon = { Icon(imageVector = Icons.Default.SupervisorAccount, contentDescription = "الاستشاريين") },
                        label = { Text("الاستشاريين", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen is Screen.PatientConsultations,
                        onClick = {
                            viewModel.navigateTo(Screen.PatientConsultations)
                        },
                        icon = { Icon(imageVector = Icons.Default.Assignment, contentDescription = "استشاراتي") },
                        label = { Text("استشاراتي", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen is Screen.PatientNotifications,
                        onClick = {
                            viewModel.navigateTo(Screen.PatientNotifications)
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge(
                                            containerColor = Color(0xFFEF4444),
                                            contentColor = Color.White
                                        ) {
                                            Text(unreadCount.toString(), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (unreadCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = "التنبيهات"
                                )
                            }
                        },
                        label = { Text("التنبيهات", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentScreen) {
                is Screen.PatientHome -> {
                    PatientHomeContent(
                        doctors = doctors,
                        onSelectDoctor = { doctor ->
                            viewModel.navigateTo(Screen.PatientSubmitCase(doctor))
                        }
                    )
                }
                is Screen.PatientSubmitCase -> {
                    PatientSubmitCaseContent(
                        viewModel = viewModel,
                        doctor = currentScreen.doctor,
                        onSubmitSuccess = {
                            viewModel.navigateTo(Screen.PatientConsultations)
                        }
                    )
                }
                is Screen.PatientConsultations -> {
                    PatientConsultationsContent(
                        cases = cases
                    )
                }
                is Screen.PatientNotifications -> {
                    PatientNotificationsContent(
                        viewModel = viewModel,
                        notifications = notifications
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun PatientHomeContent(
    doctors: List<Doctor>,
    onSelectDoctor: (Doctor) -> Unit
) {
    var selectedSpecialtyFilter by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }

    val specialties = listOf(
        "الكل",
        "أمراض الباطنة العامة والغدد الصماء والسكري",
        "طب الأطفال، حديثي الولادة والأمراض الوراثية",
        "جراحة القلب المفتوح والأوعية الدموية (كبار وأطفال)",
        "جراحة الصدر والرئة ومناظير الصدر الدقيقة",
        "أمراض وزراعة الكلى والمسالك البولية والذكورة",
        "جراحة المخ والأعصاب والعمود الفقري ومناظير الدماغ",
        "أورام الكبار والعلاج الكيميائي والإشعاعي الموجه"
    )

    val filteredDoctors = doctors.filter { doctor ->
        val matchesSpecialty = selectedSpecialtyFilter == "الكل" || doctor.specialty == selectedSpecialtyFilter
        val matchesQuery = searchQuery.isEmpty() || 
                           doctor.name.contains(searchQuery, ignoreCase = true) || 
                           doctor.specialty.contains(searchQuery, ignoreCase = true) || 
                           doctor.background.contains(searchQuery, ignoreCase = true)
        matchesSpecialty && matchesQuery
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            
            // High Density Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "مرحباً بك في",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "جسر الصحة ",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0C4A6E),
                                fontSize = 20.sp
                            )
                        )
                        Text(
                            text = "| بوصلة الشفاء",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF0284C7),
                                fontSize = 18.sp
                            )
                        )
                    }
                }
                
                // User Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0F2FE))
                        .border(1.dp, Color(0xFFBAE6FD), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "الملف الشخصي",
                        tint = Color(0xFF0369A1),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Search Bar matching High Density design
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث عن تخصص أو طبيب استشاري...", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8))) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF0284C7),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedTextColor = Color(0xFF0F172A),
                    unfocusedTextColor = Color(0xFF0F172A)
                ),
                singleLine = true
            )
        }

        // Specialty filters horizontally scrollable row
        item {
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                items(specialties) { spec ->
                    val isSelected = selectedSpecialtyFilter == spec
                    val displayName = when (spec) {
                        "الكل" -> "الكل"
                        "أمراض الباطنة العامة والغدد الصماء والسكري" -> "الباطنية"
                        "طب الأطفال، حديثي الولادة والأمراض الوراثية" -> "طب الأطفال"
                        "جراحة القلب المفتوح والأوعية الدموية (كبار وأطفال)" -> "جراحة القلب"
                        "جراحة الصدر والرئة ومناظير الصدر الدقيقة" -> "جراحة الصدر"
                        "أمراض وزراعة الكلى والمسالك البولية والذكورة" -> "أمراض الكلى"
                        "جراحة المخ والأعصاب والعمود الفقري ومناظير الدماغ" -> "المخ والأعصاب"
                        "أورام الكبار والعلاج الكيميائي والإشعاعي الموجه" -> "علاج الأورام"
                        else -> spec
                    }
                    
                    Surface(
                        onClick = { selectedSpecialtyFilter = spec },
                        shape = RoundedCornerShape(24.dp),
                        color = if (isSelected) Color(0xFF0284C7) else Color.White,
                        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayName,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF475569)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Promotional / Second Opinion Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0C4A6E)
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    // Decorative circle 1
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = (-16).dp, y = 16.dp)
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.05f))
                    )
                    // Decorative circle 2
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 24.dp, y = (-24).dp)
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.05f))
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "بحاجة لرأي طبي ثانٍ؟ 🩺",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "ارفع تقاريرك الطبية الآن واحصل على توجيه دقيق من نخبة من كبار الاستشاريين مجاناً.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFE0F2FE),
                                    lineHeight = 16.sp,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Button(
                            onClick = {
                                if (doctors.isNotEmpty()) {
                                    onSelectDoctor(doctors.first())
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF0C4A6E)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = "تقديم حالة جديدة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "أطباء استشاريون متطوعون",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                )
                Text(
                    text = "عرض الكل",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0284C7)
                    ),
                    modifier = Modifier.clickable {
                        selectedSpecialtyFilter = "الكل"
                        searchQuery = ""
                    }
                )
            }
        }

        if (filteredDoctors.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا يوجد أطباء متطوعون يطابقون بحثك حالياً.",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredDoctors) { doctor ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("doctor_card_${doctor.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val isHeartSpecialty = doctor.specialty.contains("القلب") || doctor.specialty.contains("الأوعية")
                        val isPediatricSpecialty = doctor.specialty.contains("الأطفال")
                        val isInternalSpecialty = doctor.specialty.contains("الباطنة")
                        
                        val (avatarBgColor, iconColor, iconVector) = when {
                            isHeartSpecialty -> Triple(Color(0xFFFFF1F2), Color(0xFFF43F5E), Icons.Default.Favorite)
                            isPediatricSpecialty -> Triple(Color(0xFFFEF3C7), Color(0xFFD97706), Icons.Default.Person)
                            isInternalSpecialty -> Triple(Color(0xFFEEF2FF), Color(0xFF4F46E5), Icons.Default.LocalHospital)
                            else -> Triple(Color(0xFFECFDF5), Color(0xFF10B981), Icons.Default.MedicalServices)
                        }

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(avatarBgColor)
                                .border(1.dp, iconColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = doctor.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    fontSize = 14.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = doctor.specialty.substringBefore(" ("),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = iconColor,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(3.dp))
                            
                            // Availability indicator
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (doctor.isAvailable) Color(0xFF10B981) else Color(0xFF94A3B8))
                                )
                                Text(
                                    text = if (doctor.isAvailable) "متاح الآن" else "خارج المناوبة",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (doctor.isAvailable) Color(0xFF047857) else Color(0xFF64748B),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = doctor.background.substringBefore("،"),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 10.sp
                                    ),
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }

                        Button(
                            onClick = { onSelectDoctor(doctor) },
                            modifier = Modifier
                                .testTag("request_consultation_button_${doctor.id}"),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = avatarBgColor,
                                contentColor = iconColor
                            ),
                            border = BorderStroke(1.dp, iconColor.copy(alpha = 0.15f))
                        ) {
                            Text(
                                text = "طلب استشارة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PatientSubmitCaseContent(
    viewModel: MedicalViewModel,
    doctor: Doctor,
    onSubmitSuccess: () -> Unit
) {
    val name by viewModel.formPatientName.collectAsState()
    val age by viewModel.formPatientAge.collectAsState()
    val gender by viewModel.formPatientGender.collectAsState()
    val description by viewModel.formCaseDescription.collectAsState()
    val attachedFiles by viewModel.formAttachments.collectAsState()

    val scrollState = rememberScrollState()

    // Preset list of mock report options
    val fileOptions = listOf(
        "تقرير رنين مغناطيسي (MRI Report) 📁",
        "صورة أشعة سينية للصدر (Chest X-Ray) 🖼️",
        "تقرير خزعة نسيجية (Biopsy Report) 📄",
        "فحوصات وتحاليل الدم الشاملة (Lab Results) 🧪",
        "تخطيط صدى القلب (Echocardiogram) 🫀"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Recipient Doctor Info
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "تقديم الاستشارة إلى:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = doctor.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = doctor.specialty,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Text(
            text = "معلومات المريض الأساسية:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        // Patient Name Input
        OutlinedTextField(
            value = name,
            onValueChange = { viewModel.formPatientName.value = it },
            label = { Text("اسم المريض الرباعي (اختياري لكبار السن والقرى)") },
            placeholder = { Text("مثال: علي صالح محمد اليماني") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("patient_name_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient Age Input
            OutlinedTextField(
                value = age,
                onValueChange = { viewModel.formPatientAge.value = it },
                label = { Text("العمر") },
                placeholder = { Text("أرقام فقط") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .testTag("patient_age_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Gender Selector
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "الجنس:",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ذكر", "أنثى").forEach { gen ->
                        val isSelected = gender == gen
                        ElevatedCard(
                            onClick = { viewModel.formPatientGender.value = gen },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = gen,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Case Description
        OutlinedTextField(
            value = description,
            onValueChange = { viewModel.formCaseDescription.value = it },
            label = { Text("شرح مختصر للحالة والشكوى الصحية") },
            placeholder = { Text("اكتب هنا تاريخ المرض، الأعراض الحالية، متى بدأت، وأي توصيات سابقة تلقيتموها.") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .testTag("case_desc_input"),
            shape = RoundedCornerShape(12.dp),
            maxLines = 8
        )

        // Medical Reports & Scans Upload Mock Area
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "إرفاق تقارير طبية، تحاليل مخبرية وصور أشعة:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "حدد الملفات أو الأشعة والتحاليل المتوفرة لديك ليتمكن الدكتور الاستشاري من دراسة ملفك بشكل أدق واتخاذ رأي ثان صحيح.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)),
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // List checkable preset files representing scanning
                fileOptions.forEach { file ->
                    val isChecked = attachedFiles.contains(file)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.toggleAttachment(file) }
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { viewModel.toggleAttachment(file) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = file, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Submit Button
        Button(
            onClick = {
                viewModel.submitCase(doctor) {
                    onSubmitSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("submit_case_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "إرسال الحالة للاستشارة الخيرية",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private data class ProgressInfo(
    val value: Float,
    val percentage: String,
    val description: String,
    val color: Color
)

@Composable
fun PatientConsultationsContent(
    cases: List<ConsultationCase>
) {
    var activeDetailsCase by remember { mutableStateOf<ConsultationCase?>(null) }

    if (activeDetailsCase != null) {
        Dialog(onDismissRequest = { activeDetailsCase = null }) {
            ConsultationDetailDialog(
                consultationCase = activeDetailsCase!!,
                onDismiss = { activeDetailsCase = null }
            )
        }
    }

    if (cases.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "لا توجد لديك استشارات طبية حالياً.",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "عند طلب استشارة من لوحة الأطباء الاستشاريين، ستظهر تفاصيلها وحالة الرد عليها هنا مباشرة.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "سجل وأرشيف استشاراتك الطبية:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(cases) { c ->
                val progressInfo = when (c.status) {
                    "تم الرد" -> ProgressInfo(1.0f, "100%", "اكتمل التوجيه الطبي وصدر الرأي الثاني المعتمد من الاستشاري", Color(0xFF10B981))
                    "موعد اتصال مرئي" -> ProgressInfo(0.75f, "75%", "تمت الجدولة - بانتظار الاتصال المرئي المباشر مع الاستشاري", Color(0xFF0284C7))
                    "قيد المراجعة" -> ProgressInfo(0.40f, "40%", "قيد المراجعة - يقوم الاستشاري بدراسة تقارير الحالة والأشعة", Color(0xFFF59E0B))
                    "قيد التنسيق الخارجي" -> ProgressInfo(0.85f, "85%", "جاري التنسيق الخارجي للإحالة المباشرة لمركز طبي متقدم", Color(0xFF6366F1))
                    else -> ProgressInfo(0.20f, "20%", "تم توثيق الحالة واستلام الطلب بنجاح في جسر الصحة", Color(0xFF94A3B8))
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("case_item_card_${c.id}")
                        .clickable { activeDetailsCase = c },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = c.patientName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            // Status Badge linked with progress theme color
                            val badgeColor = progressInfo.color
                            Card(
                                colors = CardDefaults.cardColors(containerColor = badgeColor.copy(alpha = 0.12f)),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = c.status,
                                    color = badgeColor,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "التخصص المستهدف: " + c.specialty,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "شرح الحالة: " + if (c.description.length > 80) c.description.take(80) + "..." else c.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Precision Progress Tracker Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Assignment,
                                        contentDescription = null,
                                        tint = progressInfo.color,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "مؤشر تدقيق ومراجعة الحالة:",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF334155)
                                        )
                                    )
                                }
                                Text(
                                    text = progressInfo.percentage,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = progressInfo.color
                                    ),
                                    modifier = Modifier
                                        .background(progressInfo.color.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = progressInfo.value,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = progressInfo.color,
                                trackColor = progressInfo.color.copy(alpha = 0.15f)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = progressInfo.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                    lineHeight = 15.sp,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        if (c.assignedDoctorName != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "الاستشاري المتابع: " + c.assignedDoctorName,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        // Date format
                        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(c.timestamp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "تاريخ الطلب: $formattedDate",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConsultationDetailDialog(
    consultationCase: ConsultationCase,
    onDismiss: () -> Unit
) {
    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(consultationCase.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .heightIn(max = 600.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ملف الاستشارة الطبية",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            // Patient details
            Text(
                text = "معلومات المريض الأساسية:",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Text("الاسم: ${consultationCase.patientName}")
            Text("العمر والجنس: ${consultationCase.patientAge} سنة (${consultationCase.patientGender})")
            Text("تاريخ التقديم: $dateStr")

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "توصيف الحالة المرضية:",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = consultationCase.description,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Attached Files
            if (consultationCase.attachmentPath.isNotEmpty()) {
                Text(
                    text = "التقارير الطبية والأشعة المرفقة:",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                consultationCase.attachmentPath.split(", ").forEach { file ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Attachment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = file, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Doctor's Response Section
            Text(
                text = "التوجيه الطبي والرأي الاستشاري الثاني:",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))

            if (consultationCase.doctorGuidance == null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)), // Yellow pastel for waiting
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.WatchLater, contentDescription = null, tint = Color(0xFFF57F17))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "حالتك قيد الدراسة والتحليل حالياً من قبل الاستشاري المتطوع. يرجى الانتظار، سيتم إشعارك فور كتابة التقرير.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF5D4037), lineHeight = 18.sp)
                        )
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), // Green pastel for answer
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "توجيه طبي معتمد - بواسطة ${consultationCase.assignedDoctorName}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = consultationCase.doctorGuidance!!,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp, color = Color(0xFF1B5E20))
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFF2E7D32).copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Surgery urgent info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (consultationCase.isUrgentSurgicalIntervention) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (consultationCase.isUrgentSurgicalIntervention) Color.Red else Color(0xFF2E7D32),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (consultationCase.isUrgentSurgicalIntervention) "الحالة تتطلب تدخلاً جراحياً عاجلاً ⚠️" else "الحالة لا تتطلب تدخلاً جراحياً عاجلاً ✅",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        // External referral info
                        if (consultationCase.isReferredExternally) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ConnectingAirports,
                                    contentDescription = null,
                                    tint = Color(0xFF0288D1),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تم تحويل الملف للتنسيق الخارجي وعلاج الحالات المعقدة (مصر / الأردن) 🌍",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF01579B))
                                )
                            }
                        }

                        // Scheduled calls info
                        if (consultationCase.scheduledCallTime != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.VideoCall, contentDescription = null, tint = Color(0xFF0288D1))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "موعد اتصال مرئي مباشر: " + consultationCase.scheduledCallTime!!,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF01579B))
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("حسناً")
            }
        }
    }
}

fun formatRelativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "الآن"
        minutes < 60 -> "منذ $minutes دقيقة"
        hours < 24 -> "منذ $hours ساعة"
        days == 1L -> "أمس"
        else -> {
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale("ar"))
            sdf.format(Date(timestamp))
        }
    }
}

@Composable
fun PatientNotificationsContent(
    viewModel: MedicalViewModel,
    notifications: List<com.example.data.model.PatientNotification>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Actions Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "مركز التنبيهات والاشعارات",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            )
            
            if (notifications.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = { viewModel.markAllNotificationsAsRead() },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("قراءة الكل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = { viewModel.clearAllNotifications() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مسح الكل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Text(
                        text = "صندوق الوارد فارغ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                    )
                    Text(
                        text = "لا توجد تنبيهات جديدة حالياً. سنقوم بإشعارك فور قيام الاستشاري بالرد على طلباتك أو جدولة مواعيد اتصال مرئي مباشر لمناقشة التقرير الطبي.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF64748B),
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(notifications, key = { it.id }) { notification ->
                    val themeColor = when (notification.type) {
                        "REPLY" -> Color(0xFF10B981) // Green
                        "SCHEDULE" -> Color(0xFF0284C7) // Blue
                        "REFERRAL" -> Color(0xFF8B5CF6) // Purple
                        else -> Color(0xFF64748B)
                    }

                    val iconVector = when (notification.type) {
                        "REPLY" -> Icons.Default.MedicalServices
                        "SCHEDULE" -> Icons.Default.VideoCall
                        "REFERRAL" -> Icons.Default.Send
                        else -> Icons.Default.Notifications
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.markNotificationAsRead(notification.id)
                                viewModel.navigateTo(Screen.PatientConsultations)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (notification.isRead) Color.White else Color(0xFFF8FAFC)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (notification.isRead) Color(0xFFE2E8F0) else themeColor.copy(alpha = 0.3f)
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (notification.isRead) 0.dp else 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Row 1: Header (Icon, Title, Badge, Time)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(themeColor.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = iconVector,
                                        contentDescription = null,
                                        tint = themeColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = notification.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E293B)
                                            ),
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        
                                        if (!notification.isRead) {
                                            Box(
                                                modifier = Modifier
                                                    .background(themeColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "جديد",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = themeColor,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 9.sp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(2.dp))
                                    
                                    Text(
                                        text = formatRelativeTime(notification.timestamp),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Row 2: Message Content
                            Text(
                                text = notification.message,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF475569),
                                    lineHeight = 20.sp,
                                    fontSize = 13.sp
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Row 3: Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!notification.isRead) {
                                    TextButton(
                                        onClick = { viewModel.markNotificationAsRead(notification.id) },
                                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF475569))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("مقروء", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                }
                                
                                TextButton(
                                    onClick = { viewModel.deleteNotification(notification.id) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("حذف", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
