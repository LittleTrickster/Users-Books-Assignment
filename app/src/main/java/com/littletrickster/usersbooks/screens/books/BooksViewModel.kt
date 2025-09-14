package com.littletrickster.usersbooks.screens.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littletrickster.usersbooks.LibraryRepo
import com.littletrickster.usersbooks.screens.books.BooksScreenAction.Back
import com.littletrickster.usersbooks.screens.books.BooksScreenAction.ChangeBook
import com.littletrickster.usersbooks.screens.books.BooksScreenAction.Refresh
import com.littletrickster.usersbooks.screens.books.BooksScreenAction.ShowAll
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BooksViewModel
@Inject constructor(
    private val libraryRepo: LibraryRepo
) : ViewModel() {

    private val _effects = MutableSharedFlow<BookScreenEffect>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effects = _effects.asSharedFlow()

    private val _state = MutableStateFlow(BooksScreenState())
    val state = _state.asStateFlow()

    init {
        observeBooks()
        refresh()
    }

    fun onAction(action: BooksScreenAction) {
        when (action) {
            Refresh -> refresh()
            //handle rest in ui
            Back -> _effects.tryEmit(BookScreenEffect.Back)
            is ChangeBook -> _effects.tryEmit(BookScreenEffect.ChangeBook(action.book))
            is ShowAll -> _effects.tryEmit(BookScreenEffect.ShowAll(action.listId))
        }
    }

    private fun observeBooks() {
        libraryRepo.bookFlow
            .onEach { new ->
                _state.update { current ->
                    current.copy(typeAndBooks = new)
                }
            }.flowOn(Dispatchers.Main)
            .launchIn(viewModelScope)
    }


    private fun refresh() {
        _state.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                libraryRepo.fetchBooksAndLists()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                e.printStackTrace()
                e.message?.also {
                    _effects.tryEmit(BookScreenEffect.Error(it))
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }


}

