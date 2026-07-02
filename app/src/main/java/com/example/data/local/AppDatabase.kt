package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ConsultationCase
import com.example.data.model.Doctor
import com.example.data.model.PatientNotification

@Database(entities = [Doctor::class, ConsultationCase::class, PatientNotification::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun doctorDao(): DoctorDao
    abstract fun consultationCaseDao(): ConsultationCaseDao
    abstract fun patientNotificationDao(): PatientNotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jisr_al_sehha_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
