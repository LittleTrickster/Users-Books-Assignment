package com.littletrickster.usersbooks.screens.fullbook

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.littletrickster.usersbooks.AppDispatchers
import com.littletrickster.usersbooks.FullBookScreen
import com.littletrickster.usersbooks.LibraryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
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
class FullBookViewModel
@Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val libraryRepo: LibraryRepo,
    private val appDispatchers: AppDispatchers
) : ViewModel() {

    private val bookId: Int = savedStateHandle.toRoute<FullBookScreen>().id


    private val _effects = MutableSharedFlow<FullBookScreenEffect>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effects = _effects.asSharedFlow()

    private val _state = MutableStateFlow(FullBookScreenState())
    val state = _state.asStateFlow()

    init {
        observeBooks()
    }

    fun onAction(action: FullBookScreenAction) {
        when (action) {
            FullBookScreenAction.Back -> _effects.tryEmit(FullBookScreenEffect.Back)
            FullBookScreenAction.Refresh -> refresh()
        }
    }

    private fun observeBooks() {
        libraryRepo.fullBookByIdWithTitle(bookId)
            .onEach { new ->
                _state.update { current ->
                    current.copy(fullBookWithListTitle = new)
                }
            }
            .launchIn(viewModelScope)
    }



    private fun refresh() {
        _state.update {
            it.copy(isLoading = true, refreshTimes = it.refreshTimes + 1)
        }
        viewModelScope.launch(appDispatchers.IO) {
            try {
                val status = libraryRepo.fetchFullBook(bookId)
                if (!status) {
                    _effects.tryEmit(FullBookScreenEffect.Error("Book Not found"))
                    _effects.tryEmit(FullBookScreenEffect.Back)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
//                e.printStackTrace()
                e.message?.also {
                    _effects.tryEmit(FullBookScreenEffect.Error(it))
                }
            } finally {
                withContext(appDispatchers.Main) {
                    _state.update {
                        it.copy(isLoading = false)
                    }
                }

            }
        }
    }


}

