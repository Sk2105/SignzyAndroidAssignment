package com.composeapp.signzy_android_assignment.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.composeapp.signzy_android_assignment.domain.models.UserVerification

@Database(
    entities = [UserVerification::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract  fun getUserVerificationDao(): UserVerificationDao
}