package com.composeapp.signzy_android_assignment.domain.usecases

import com.composeapp.signzy_android_assignment.data.database.UserVerificationDao
import com.composeapp.signzy_android_assignment.domain.models.UserVerification
import javax.inject.Inject

class InsertUserVerificationUseCase @Inject constructor(
    private val userVerificationDao: UserVerificationDao
) {
    suspend operator fun invoke(userVerification: UserVerification) {
        userVerificationDao.insertUserVerification(userVerification)
    }
}

