package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "consultation_cases")
data class ConsultationCase(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientName: String,
    val patientAge: Int,
    val patientGender: String, // ذكر / أنثى
    val specialty: String, // التخصص المطلوب
    val description: String, // شرح الحالة الصحية
    val attachmentPath: String = "", // مسار ملفات التقارير والأشعة
    val status: String = "قيد المراجعة", // قيد المراجعة / تم الرد / موعد اتصال
    val timestamp: Long = System.currentTimeMillis(),
    val doctorGuidance: String? = null, // التوجيه الطبي المكتوب
    val isUrgentSurgicalIntervention: Boolean = false, // تدخل جراحي عاجل أم لا
    val isReferredExternally: Boolean = false, // تحويل للتنسيق الخارجي
    val scheduledCallTime: String? = null, // موعد الاتصال المرئي/الصوتي
    val assignedDoctorId: Int? = null, // الطبيب الاستشاري الذي تولى الحالة
    val assignedDoctorName: String? = null // اسم الطبيب المتابع للحالة لسهولة العرض
)
