package com.littletrickster.usersbooks.books

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littletrickster.usersbooks.EffectsState
import com.littletrickster.usersbooks.api.BooksApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel
@Inject constructor(
    private val booksApi: BooksApi
) : ViewModel() {

    private val _effects = MutableSharedFlow<EffectsState>()
    val effects = _effects.asSharedFlow()

    var state = mutableStateOf(BooksScreenState())
        private set


    fun onAction(action:BooksScreenAction){

    }

    private fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val booksJob = async {
                booksApi.getBooks()
            }
            val statusListJob = async {
                booksApi.getLists()
            }

            try {
                val books = booksJob.await()
                val statusList = statusListJob.await()
            } catch (e: Exception) {

            } finally {

            }
        }
    }


}