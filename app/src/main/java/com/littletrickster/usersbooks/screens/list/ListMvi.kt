package com.littletrickster.usersbooks.screens.list

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.navigation.NavController
import com.littletrickster.usersbooks.AllScreen
import com.littletrickster.usersbooks.FullBookScreen
import com.littletrickster.usersbooks.screens.books.BookScreenEffect
import com.littletrickster.usersbooks.screens.books.BooksMain
import com.littletrickster.usersbooks.screens.books.BooksScreenAction
import com.littletrickster.usersbooks.screens.books.BooksScreenState
import com.littletrickster.usersbooks.screens.books.BooksViewModel


@Composable
fun ListMviLink(
    navController: NavController,
    viewModel: ListViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ListScreenEffect.Back -> navController.popBackStack()
                is ListScreenEffect.ChangeBook -> navController.navigate(FullBookScreen(effect.book.id))
                is ListScreenEffect.Error -> Toast.makeText(
                    context,
                    effect.string,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    ListMviLink(
        state = viewModel.state.collectAsState().value,
        onAction = viewModel::onAction
    )


}

@Composable
private fun ListMviLink(
    state: ListScreenState,
    onAction: (ListScreenAction) -> Unit,
) = ListMain(
    title = state.title,
    list = state.books,
    onBack = { onAction(ListScreenAction.Back) },
    isRefreshing = state.isLoading,
    onRefresh = { onAction(ListScreenAction.Refresh) },
    onBookClick = { onAction(ListScreenAction.ChangeBook(it)) }
)
