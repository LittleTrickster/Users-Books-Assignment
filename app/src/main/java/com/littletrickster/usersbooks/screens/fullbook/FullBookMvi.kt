package com.littletrickster.usersbooks.screens.fullbook

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.littletrickster.usersbooks.screens.safePopBackStack
import java.time.format.DateTimeFormatter


@Composable
fun FullBookMviLink(
    navController: NavController,
    viewModel: FullBookViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                FullBookScreenEffect.Back -> navController.safePopBackStack()
                is FullBookScreenEffect.Error ->
                    Toast.makeText(context, effect.string, Toast.LENGTH_SHORT).show()
            }
        }
    }


    val state by viewModel.state.collectAsState()

    LaunchedEffect(null) {
        if (state.refreshTimes == 0) viewModel.onAction(FullBookScreenAction.Refresh)
    }

    FullBookMviLink(
        state = state,
        onAction = viewModel::onAction
    )


}

@Composable
fun FullBookMviLink(
    state: FullBookScreenState,
    onAction: (FullBookScreenAction) -> Unit,
) {
    val bookAndStatus = state.fullBookWithListTitle
    val book = bookAndStatus?.book
    val bookListState = bookAndStatus?.listTitle

    val formatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val formattedDate =
        remember(book?.publicationDate) {
            book?.publicationDate?.let { formatter.format(it) }
        }

    FullBookMain(
        title = book?.title ?: "",
        author = book?.author ?: "",
        isbn = book?.isbn ?: "",
        date = formattedDate ?: "",
        bookListStatus = bookListState ?: "",
        description = book?.description ?: "",
        image = book?.img,
        isRefreshing = state.isLoading,
        onRefresh = { onAction(FullBookScreenAction.Refresh) },
        onBack = { onAction(FullBookScreenAction.Back) })
}