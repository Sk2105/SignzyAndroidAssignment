package com.composeapp.signzy_android_assignment.domain.usecases

import com.composeapp.signzy_android_assignment.data.database.UserVerificationDao
import com.composeapp.signzy_android_assignment.domain.models.UserVerification
import javax.inject.Inject


class GetUsersVerificationUseCase @Inject constructor(
    private val userVerificationDao: UserVerificationDao)
{
    suspend operator fun invoke(): List<UserVerification> {
        return userVerificationDao.getAllUserVerifications()
    }
}