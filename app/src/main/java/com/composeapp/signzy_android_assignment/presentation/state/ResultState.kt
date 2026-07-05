package com.composeapp.signzy_android_assignment.presentation.state

sealed interface ResultState<T> {
    data class Success<T>(val data: T) : ResultState<T>
    data class Error<T>(val message: String, val data: T? = null) : ResultState<T>
    class Loading<T> : ResultState<T>
}