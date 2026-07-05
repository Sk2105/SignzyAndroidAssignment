package com.composeapp.signzy_android_assignment.data.repo

import android.util.Log
import com.composeapp.signzy_android_assignment.domain.models.UsersResponse
import com.composeapp.signzy_android_assignment.domain.repo.UserRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient
): UserRepository {
    override suspend fun getUsers(): Result<UsersResponse> {
        try{
            val response = httpClient.get("/users")
            Log.d("UserRepository",response.body())
            return Result.success(response.body())
        }catch (e: Exception){
            return Result.failure(e)
        }
    }
}