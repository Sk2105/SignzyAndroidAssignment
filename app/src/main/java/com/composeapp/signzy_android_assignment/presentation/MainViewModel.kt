package com.composeapp.signzy_android_assignment.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composeapp.signzy_android_assignment.domain.models.User
import com.composeapp.signzy_android_assignment.domain.models.UserVerification
import com.composeapp.signzy_android_assignment.domain.models.UsersResponse
import com.composeapp.signzy_android_assignment.domain.usecases.GetUsersUseCase
import com.composeapp.signzy_android_assignment.domain.usecases.GetUsersVerificationUseCase
import com.composeapp.signzy_android_assignment.domain.usecases.InsertUserVerificationUseCase
import com.composeapp.signzy_android_assignment.domain.usecases.UpdateUserVerificationUseCase
import com.composeapp.signzy_android_assignment.presentation.holder.DataHolder
import com.composeapp.signzy_android_assignment.presentation.state.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val updateUserVerificationUseCase: UpdateUserVerificationUseCase,
    private val getUserVerificationUseCase: GetUsersVerificationUseCase,
    private val insertUserVerificationUseCase: InsertUserVerificationUseCase

) : ViewModel() {

    private val _users = MutableStateFlow(DataHolder<List<User>>())
    private val _userVerification = MutableStateFlow(DataHolder<List<UserVerification>>())
    val userVerification = _userVerification.asStateFlow()

    val users = _users.asStateFlow()

    init {
        fetchUsers()
        fetchUserVerification()
    }

    fun fetchUsers() {
        Log.d("MainViewModel", "Fetching users...")
        viewModelScope.launch {
            _users.update {
                DataHolder(resultState = ResultState.Loading())
            }

            getUsersUseCase()
                .onSuccess { response ->
                    Log.d("MainViewModel", "Users fetched successfully: ${response}")
                    _users.value = DataHolder(ResultState.Success(response.users))
                }
                .onFailure { exception ->
                    Log.e("MainViewModel", "Error fetching users: ${exception.message}")
                    _users.update {
                        DataHolder(
                            resultState = ResultState.Error(
                                exception.message ?: "Unknown error occurred"
                            )
                        )
                    }
                }
        }
    }


    // User Verification
    fun fetchUserVerification(){
        viewModelScope.launch {
            _userVerification.update {
                DataHolder(resultState = ResultState.Loading())
            }
            val result = getUserVerificationUseCase()

                _userVerification.update {
                    DataHolder(resultState = ResultState.Success(result))
                }

        }

    }

    // Update User Verification
    fun updateUserVerification(userId: Int, verificationStatus: UserVerification){
        viewModelScope.launch {
            updateUserVerificationUseCase( verificationStatus)
        }
    }


    fun insertUserVerification(userVerification: UserVerification){
        viewModelScope.launch {
            insertUserVerificationUseCase(userVerification)
        }
    }

}