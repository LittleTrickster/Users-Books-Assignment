package com.littletrickster.usersbooks.ui.theme

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Composable
fun Main() {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = Start,
        enterTransition = { slideInHorizontally() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
    ) {
        composable<Start> {

        }
    }
}

@Serializable
object Start
