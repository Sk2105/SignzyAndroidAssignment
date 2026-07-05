package com.composeapp.signzy_android_assignment.domain.models

import kotlinx.serialization.Serializable


@Serializable
data class UsersResponse(
    val users: List<User>
)