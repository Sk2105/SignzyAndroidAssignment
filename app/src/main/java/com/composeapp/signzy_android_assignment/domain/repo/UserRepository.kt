package com.composeapp.signzy_android_assignment.domain.repo

import com.composeapp.signzy_android_assignment.domain.models.User
import com.composeapp.signzy_android_assignment.domain.models.UsersResponse

interface UserRepository {
    suspend fun getUsers(): Result<UsersResponse>
}