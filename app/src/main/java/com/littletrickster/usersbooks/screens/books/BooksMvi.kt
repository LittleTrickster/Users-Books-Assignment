package com.littletrickster.usersbooks.screens.books

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.navigation.NavController
import com.littletrickster.usersbooks.AllScreen
import com.littletrickster.usersbooks.FullBookScreen


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
                BookScreenEffect.Back -> navController.popBackStack()
                is BookScreenEffect.ChangeBook -> navController.navigate(FullBookScreen(effect.book.id))
                is BookScreenEffect.ShowAll -> navController.navigate(AllScreen(effect.listId))
            }
        }
    }


    BooksMviLink(
        state = viewModel.state.collectAsState().value,
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
