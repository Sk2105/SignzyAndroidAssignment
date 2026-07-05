package com.composeapp.signzy_android_assignment.presentation.holder

import com.composeapp.signzy_android_assignment.presentation.state.ResultState

data class DataHolder<T>(
    val resultState: ResultState<T> = ResultState.Loading(),
)