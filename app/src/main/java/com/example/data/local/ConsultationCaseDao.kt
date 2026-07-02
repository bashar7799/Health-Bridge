package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ConsultationCase
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsultationCaseDao {
    @Query("SELECT * FROM consultation_cases ORDER BY timestamp DESC")
    fun getAllCases(): Flow<List<ConsultationCase>>

    @Query("SELECT * FROM consultation_cases WHERE assignedDoctorId = :doctorId ORDER BY timestamp DESC")
    fun getCasesForDoctor(doctorId: Int): Flow<List<ConsultationCase>>

    @Query("SELECT * FROM consultation_cases WHERE id = :id")
    suspend fun getCaseById(id: Int): ConsultationCase?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(consultationCase: ConsultationCase)

    @Update
    suspend fun updateCase(consultationCase: ConsultationCase)

    @Query("DELETE FROM consultation_cases WHERE id = :id")
    suspend fun deleteCaseById(id: Int)
}
