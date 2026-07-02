package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Doctor
import kotlinx.coroutines.flow.Flow

@Dao
interface DoctorDao {
    @Query("SELECT * FROM doctors WHERE isActive = 1")
    fun getActiveDoctors(): Flow<List<Doctor>>

    @Query("SELECT * FROM doctors WHERE id = :id")
    suspend fun getDoctorById(id: Int): Doctor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctor(doctor: Doctor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctors(doctors: List<Doctor>)
}
