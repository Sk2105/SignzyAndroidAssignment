package com.composeapp.signzy_android_assignment.presentation.navigation

import kotlinx.serialization.Serializable


sealed class AppGraph  {


    @Serializable
    object Home : AppGraph()


    @Serializable
    data class Profile(val userId: Int) : AppGraph()
    @Serializable
    data class Settings(val userId: Int) : AppGraph()
    @Serializable
    data class KycScreen(val userId: Int) : AppGraph()







}