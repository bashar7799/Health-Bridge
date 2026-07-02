package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patient_notifications")
data class PatientNotification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val caseId: Int,
    val patientName: String,
    val doctorName: String,
    val title: String,
    val message: String,
    val type: String, // "REPLY" | "SCHEDULE" | "REFERRAL"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
