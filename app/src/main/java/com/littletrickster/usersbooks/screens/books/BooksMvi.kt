package com.littletrickster.usersbooks.screens.books

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.navigation.NavController
import com.littletrickster.usersbooks.AllScreen
import com.littletrickster.usersbooks.FullBookScreen
import com.littletrickster.usersbooks.screens.safePopBackStack


@Composable
fun BooksMviLink(
    navController: NavController,
    viewModel: BooksViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when(effect){
                is BookScreenEffect.Error -> Toast.makeText(context, effect.string, Toast.LENGTH_SHORT).show()
                BookScreenEffect.Back -> navController.safePopBackStack()
                is BookScreenEffect.ChangeBook -> navController.navigate(FullBookScreen(effect.book.id))
                is BookScreenEffect.ShowAll -> navController.navigate(AllScreen(effect.listId))
            }
        }
    }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(null) {
        if (state.refreshTimes == 0) viewModel.onAction(BooksScreenAction.Refresh)
    }


    BooksMviLink(
        state = state,
        onAction = viewModel::onAction
    )


}

@Composable
fun BooksMviLink(
    state: BooksScreenState,
    onAction: (BooksScreenAction) -> Unit,
) = BooksMain(
    booksList = state.typeAndBooks,
    onAllClick = { listId ->
        onAction(BooksScreenAction.ShowAll(listId))
    },
    onRefresh = {
        onAction(BooksScreenAction.Refresh)
    },
    onBookClick = {
        onAction(BooksScreenAction.ChangeBook(it))
    },
    isRefreshing = state.isLoading
)
