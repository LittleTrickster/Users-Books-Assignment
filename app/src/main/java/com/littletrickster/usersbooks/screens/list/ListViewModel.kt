package com.littletrickster.usersbooks.screens.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.littletrickster.usersbooks.AllScreen
import com.littletrickster.usersbooks.FullBookScreen
import com.littletrickster.usersbooks.LibraryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ListViewModel
@Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val libraryRepo: LibraryRepo
) : ViewModel() {

    private val listId: Int = savedStateHandle.toRoute<AllScreen>().listId


    private val _effects = MutableSharedFlow<ListScreenEffect>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effects = _effects.asSharedFlow()

    private val _state = MutableStateFlow(ListScreenState())
    val state = _state.asStateFlow()

    init {
        observeBooks()
    }

    fun onAction(action: ListScreenAction) {
        when (action) {
            //handle rest in ui
            ListScreenAction.Back -> _effects.tryEmit(ListScreenEffect.Back)
            is ListScreenAction.ChangeBook -> _effects.tryEmit(ListScreenEffect.ChangeBook(action.book))
            ListScreenAction.Refresh -> refresh()
        }
    }

    private fun observeBooks() {
        libraryRepo.booksByListId(listId)
            .onEach { new ->
                _state.update { current ->
                    current.copy(books = new)
                }
            }
            .launchIn(viewModelScope)

        libraryRepo.listById(listId).onEach { new ->
            _state.update { current ->
                current.copy(title = new?.title ?: "")
            }
        }
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
                    _effects.tryEmit(ListScreenEffect.Error(it))
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

