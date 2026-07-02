package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Doctor
import com.example.ui.MedicalViewModel
import com.example.ui.theme.SkyBluePrimary

@Composable
fun RoleSelectionScreen(
    viewModel: MedicalViewModel,
    doctors: List<Doctor>,
    onSelectPatient: () -> Unit,
    onSelectDoctor: (Doctor) -> Unit
) {
    var showDoctorSelectionDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // App Logo Icon Container
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0F2FE))
                .border(1.dp, Color(0xFFBAE6FD), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Healing,
                contentDescription = "جسر الصحة",
                tint = Color(0xFF0284C7),
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Title Row
        Text(
            text = "جسر الصحة",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0C4A6E),
                letterSpacing = 0.5.sp
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = "بوصلة الشفاء للأمل الطبي",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color(0xFF0284C7),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Hero Image Illustration
        val context = LocalContext.current
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("/app/src/main/res/drawable/medical_charity_hero.jpg")
                    .crossfade(true)
                    .build(),
                contentDescription = "جسر الصحة التوجيه الطبي الخيري",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Subtitle/Brief Intro
        Text(
            text = "مبادرة إنسانية تطوعية تربط المرضى في المناطق الأشد عوزاً ونأياً بنخبة من الاستشاريين والأطباء الأكاديميين للحصول على رأي طبي ثانٍ وتوجيه علاجي مجاني.",
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF475569)
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Selector Title
        Text(
            text = "الرجاء اختيار صفة الدخول:",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Patient Card Choice
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("patient_role_card")
                .clickable { onSelectPatient() },
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFF1F5F9))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF0F9FF))
                        .border(1.dp, Color(0xFF0284C7).copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "بوابة المريض",
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "بوابة المريض (طالب الاستشارة)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "طلب استشارة جديدة، رفع تقارير وأشعة، ومتابعة الردود الطبية.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B),
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Doctor Card Choice
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("doctor_role_card")
                .clickable { showDoctorSelectionDialog = true },
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFF1F5F9))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFECFDF5))
                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = "بوابة الطبيب الاستشاري",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "بوابة الطبيب الاستشاري (المتطوع)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "مراجعة ملفات الحالات، كتابة التوجيه الطبي، وإحالة الحالات المعقدة.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B),
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Doctor Selection Dialog
    if (showDoctorSelectionDialog) {
        Dialog(onDismissRequest = { showDoctorSelectionDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "تسجيل دخول كطبيب استشاري متطوع",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0C4A6E)
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "اختر طبيباً لمحاكاة لوحة تحكم الاستشاريين والرد على الحالات",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B)
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (doctors.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.size(36.dp), color = Color(0xFF0284C7))
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 280.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            doctors.forEach { doctor ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showDoctorSelectionDialog = false
                                            onSelectDoctor(doctor)
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val isHeartSpecialty = doctor.specialty.contains("القلب") || doctor.specialty.contains("الأوعية")
                                        val iconBg = if (isHeartSpecialty) Color(0xFFF0F9FF) else Color(0xFFECFDF5)
                                        val iconColor = if (isHeartSpecialty) Color(0xFF0284C7) else Color(0xFF10B981)

                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(iconBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AssignmentInd,
                                                contentDescription = null,
                                                tint = iconColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = doctor.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF0F172A)
                                                )
                                            )
                                            Text(
                                                text = doctor.specialty.substringBefore(" ("),
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = iconColor,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showDoctorSelectionDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF1F5F9),
                            contentColor = Color(0xFF475569)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "إلغاء",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
