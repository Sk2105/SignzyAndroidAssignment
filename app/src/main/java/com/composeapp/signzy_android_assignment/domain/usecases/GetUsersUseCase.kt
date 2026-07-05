package com.composeapp.signzy_android_assignment.domain.usecases

import com.composeapp.signzy_android_assignment.domain.repo.UserRepository
import javax.inject.Inject


class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() = userRepository.getUsers()
}