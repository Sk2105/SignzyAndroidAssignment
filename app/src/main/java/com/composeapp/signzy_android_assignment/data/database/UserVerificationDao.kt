package com.composeapp.signzy_android_assignment.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.composeapp.signzy_android_assignment.domain.models.UserVerification

@Dao
interface UserVerificationDao {

    @Query("SELECT * FROM user_verifications")
    suspend fun getAllUserVerifications(): List<UserVerification>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserVerification(userVerification: UserVerification)

    @Query("DELETE FROM user_verifications WHERE userId = :userId")
    suspend fun deleteUserVerification(userId: Int)


    // update
    @Update
    suspend fun updateUserVerification(userVerification: UserVerification)

}