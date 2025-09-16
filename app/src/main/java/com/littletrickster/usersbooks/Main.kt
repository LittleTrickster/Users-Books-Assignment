package com.littletrickster.usersbooks

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.littletrickster.usersbooks.screens.books.BooksMviLink
import com.littletrickster.usersbooks.screens.fullbook.FullBookMviLink
import com.littletrickster.usersbooks.screens.list.ListMviLink
import kotlinx.serialization.Serializable

@Composable
fun Main() {

    val navController = rememberNavController()

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = BooksScreen,
//        enterTransition = { slideInHorizontally() },
//        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
    ) {
        composable<BooksScreen> {backStackEntry->
            BooksMviLink(navController = navController)
        }
        composable<AllScreen> {backStackEntry->
            ListMviLink(navController = navController)
        }
        composable<FullBookScreen> {backStackEntry->
            FullBookMviLink(navController = navController)
        }
    }
}


sealed interface AppDestination

@Serializable
object BooksScreen : AppDestination

@Serializable
data class AllScreen(val listId: Int) : AppDestination

@Serializable
data class FullBookScreen(val id: Int) : AppDestination



