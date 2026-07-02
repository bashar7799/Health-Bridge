package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PatientNotification
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientNotificationDao {
    @Query("SELECT * FROM patient_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<PatientNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: PatientNotification)

    @Query("UPDATE patient_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)

    @Query("UPDATE patient_notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM patient_notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: Int)

    @Query("DELETE FROM patient_notifications")
    suspend fun clearAllNotifications()
}
