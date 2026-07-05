package com.composeapp.signzy_android_assignment.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.composeapp.signzy_android_assignment.presentation.MainViewModel
import com.composeapp.signzy_android_assignment.presentation.home.HomeScreen
import com.composeapp.signzy_android_assignment.presentation.kyc.KycScreen
import com.composeapp.signzy_android_assignment.presentation.profile.ProfileScreen

@Composable
fun AppNavigation() {
    val navHostController = rememberNavController()
    val viewModel = hiltViewModel<MainViewModel>()

    NavHost(
        navHostController,
        AppGraph.Home
    ) {
        composable<AppGraph.Home> {
            HomeScreen(
                viewModel = viewModel,
                navHostController
            )
        }

        composable<AppGraph.Profile> { it ->
            val userId = it.toRoute<AppGraph.Profile>().userId
            ProfileScreen(
                userId,
                viewModel,
                navHostController
            )
        }

        composable<AppGraph.KycScreen> {
            val userId = it.toRoute<AppGraph.KycScreen>().userId
            KycScreen(userId, viewModel, navHostController)
        }
    }
}
