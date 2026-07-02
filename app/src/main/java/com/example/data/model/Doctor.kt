package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctors")
data class Doctor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val specialty: String, // التخصص الدقيق
    val background: String, // السيرة الذاتية والمؤهلات الأكاديمية والسريرية
    val isActive: Boolean = true,
    val isAvailable: Boolean = true,
    val avatarUrl: String = ""
)
