package com.composeapp.signzy_android_assignment.domain.usecases

import com.composeapp.signzy_android_assignment.data.database.UserVerificationDao
import com.composeapp.signzy_android_assignment.domain.models.UserVerification
import jakarta.inject.Inject

class UpdateUserVerificationUseCase @Inject constructor(
 private val userVerificationDao: UserVerificationDao
){
    suspend operator fun invoke(userVerification: UserVerification) {
        userVerificationDao.updateUserVerification(userVerification)
    }
}
