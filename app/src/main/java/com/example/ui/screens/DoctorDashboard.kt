package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
fun DoctorDashboard(
    viewModel: MedicalViewModel,
    loggedInDoctor: Doctor,
    cases: List<ConsultationCase>,
    currentScreen: Screen,
    onNavigateBack: () -> Unit
) {
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
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (currentScreen is Screen.DoctorCaseDetails) {
                            viewModel.navigateTo(Screen.DoctorHome)
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "لوحة الاستشاري المتطوع",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = loggedInDoctor.name + " (" + loggedInDoctor.specialty.substringBefore(" (") + ")",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
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
                is Screen.DoctorHome -> {
                    DoctorHomeContent(
                        loggedInDoctor = loggedInDoctor,
                        cases = cases,
                        onSelectCase = { caseItem ->
                            viewModel.navigateTo(Screen.DoctorCaseDetails(caseItem))
                        }
                    )
                }
                is Screen.DoctorCaseDetails -> {
                    DoctorCaseDetailsContent(
                        viewModel = viewModel,
                        consultationCase = currentScreen.consultationCase,
                        onActionCompleted = {
                            viewModel.navigateTo(Screen.DoctorHome)
                        }
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun DoctorHomeContent(
    loggedInDoctor: Doctor,
    cases: List<ConsultationCase>,
    onSelectCase: (ConsultationCase) -> Unit
) {
    val incomingCases = cases.filter { it.assignedDoctorId == loggedInDoctor.id }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Elegant High Density Welcome Card for Doctor
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C4A6E)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "شكراً لتطوعكم النبيل 🕊️",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "تساعدون المرضى في تذليل مشاق السفر وتوفير توجيه طبي أكاديمي ينقذ حياتهم.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                lineHeight = 16.sp,
                                color = Color(0xFFE0F2FE),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "الحالات الواردة المنتظرة للفرز:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        fontSize = 15.sp
                    )
                )
                Surface(
                    color = Color(0xFFF0F9FF),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFBAE6FD))
                ) {
                    Text(
                        text = "${incomingCases.size} حالات بانتظارك",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        if (incomingCases.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "رائع! لا توجد حالات بانتظارك حالياً.",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857),
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "تم الرد على جميع استشارات المرضى في عيادتك الخيرية.",
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        } else {
            items(incomingCases) { c ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("doctor_case_item_${c.id}")
                        .clickable { onSelectCase(c) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "المريض: " + c.patientName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    fontSize = 14.sp
                                )
                            )
                            
                            val (badgeBg, badgeText, badgeLabel) = when (c.status) {
                                "تم الرد" -> Triple(Color(0xFFECFDF5), Color(0xFF047857), "تم الرد")
                                "قيد المراجعة" -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "قيد المراجعة")
                                "موعد اتصال مرئي" -> Triple(Color(0xFFF0F9FF), Color(0xFF0369A1), "موعد مرئي")
                                else -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), c.status)
                            }
                            
                            Surface(
                                color = badgeBg,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, badgeText.copy(alpha = 0.15f))
                            ) {
                                Text(
                                    text = badgeLabel,
                                    color = badgeText,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "العمر: ${c.patientAge} سنة | الجنس: ${c.patientGender}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "شرح الشكوى: " + if (c.description.length > 100) c.description.take(100) + "..." else c.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                lineHeight = 16.sp,
                                color = Color(0xFF334155)
                            )
                        )

                        if (c.attachmentPath.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Attachment,
                                    contentDescription = null,
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "يحتوي على مرفقات طبية وأشعة 📎",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0284C7),
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(c.timestamp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "تاريخ الإرسال: $formattedDate",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorCaseDetailsContent(
    viewModel: MedicalViewModel,
    consultationCase: ConsultationCase,
    onActionCompleted: () -> Unit
) {
    val scrollState = rememberScrollState()

    var showGuidanceDialog by remember { mutableStateOf(false) }
    var showCallDialog by remember { mutableStateOf(false) }

    // Dialog form states
    var writtenGuidance by remember { mutableStateOf("") }
    var isUrgentSurgery by remember { mutableStateOf(false) }
    var callTime by remember { mutableStateOf("اليوم الساعة 08:00 مساءً") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section title
        Text(
            text = "قسم ملف الحالة الطبية 📄",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        )

        // Case basic info card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "بيانات المريض:",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text("• الاسم الكامل: ${consultationCase.patientName}")
                Text("• العمر: ${consultationCase.patientAge} سنة")
                Text("• الجنس: ${consultationCase.patientGender}")
                Text("• التخصص المطلوب: ${consultationCase.specialty}")
                Text("• حالة الملف الحالية: ${consultationCase.status}")
            }
        }

        // Diagnostic Description
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "شرح الحالة والأعراض السريرية:",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = consultationCase.description,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }

        // Reports and Scans file display
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "التقارير الطبية والأشعة المرفقة:",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (consultationCase.attachmentPath.isEmpty()) {
                    Text(
                        text = "لا توجد ملفات مرفقة مع هذه الحالة.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                } else {
                    consultationCase.attachmentPath.split(", ").forEach { file ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.InsertDriveFile,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = file, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = "جاهز للمعاينة والمطالعة الطبية", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "معاينة",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Existing recommendation if any
        if (consultationCase.doctorGuidance != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "تقرير التوجيه الطبي الصادر سابقاً:",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = consultationCase.doctorGuidance!!, color = Color(0xFF1B5E20))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Professional interactive buttons alongside the case
        Text(
            text = "الإجراءات الطبية المتاحة للاستشاري:",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )

        // Action 1: Write Guidance report
        Button(
            onClick = { showGuidanceDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action_write_guidance_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.RateReview, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("كتابة التوجيه الطبي (رأي استشاري)", fontWeight = FontWeight.Bold)
        }

        // Action 2: External referral to Egypt or domestic coordination
        Button(
            onClick = {
                viewModel.referCaseExternally(consultationCase.id) {
                    onActionCompleted()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action_refer_external_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.ConnectingAirports, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("تحويل للتنسيق الخارجي وعلاج معقد", fontWeight = FontWeight.Bold)
        }

        // Action 3: Schedule audio/video consultation call
        OutlinedButton(
            onClick = { showCallDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action_schedule_call_button"),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(imageVector = Icons.Default.VideoCall, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("بدء استشارة مرئية/صوتية (تحديد موعد)", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Guidance Dialog Form
    if (showGuidanceDialog) {
        Dialog(onDismissRequest = { showGuidanceDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "صياغة تقرير التوجيه الطبي الخيرى",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = writtenGuidance,
                        onValueChange = { writtenGuidance = it },
                        label = { Text("اكتب رأيك الطبي بالتفصيل") },
                        placeholder = { Text("مثال: بناءً على مراجعة تقرير الرنين المغناطيسي المرفق، يتبين وجود... ننصح بإجراء... وتجنب...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .testTag("guidance_text_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isUrgentSurgery = !isUrgentSurgery }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isUrgentSurgery,
                            onCheckedChange = { isUrgentSurgery = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "الحالة تتطلب تدخلاً جراحياً عاجلاً ⚠️",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { showGuidanceDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("إلغاء")
                        }
                        Button(
                            onClick = {
                                viewModel.provideMedicalGuidance(
                                    consultationCase.id,
                                    writtenGuidance,
                                    isUrgentSurgery
                                ) {
                                    showGuidanceDialog = false
                                    onActionCompleted()
                                }
                            },
                            modifier = Modifier
                                .weight(2f)
                                .testTag("save_guidance_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("حفظ وإرسال الرد", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Schedule Call Dialog
    if (showCallDialog) {
        Dialog(onDismissRequest = { showCallDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "جدولة موعد اتصال مرئي/صوتي",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "لتوجيه المريض مباشرة عبر مكالمة صوتية أو مرئية مدعومة من غرف التنسيق.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = callTime,
                        onValueChange = { callTime = it },
                        label = { Text("تاريخ ووقت الاتصال المقترح") },
                        placeholder = { Text("مثال: غداً الساعة 04:00 عصراً") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("call_time_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { showCallDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("إلغاء")
                        }
                        Button(
                            onClick = {
                                viewModel.scheduleVirtualCall(
                                    consultationCase.id,
                                    callTime
                                ) {
                                    showCallDialog = false
                                    onActionCompleted()
                                }
                            },
                            modifier = Modifier
                                .weight(2f)
                                .testTag("save_call_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("تأكيد الموعد", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
