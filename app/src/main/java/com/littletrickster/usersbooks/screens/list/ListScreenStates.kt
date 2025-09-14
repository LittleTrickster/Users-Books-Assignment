package com.littletrickster.usersbooks.screens.list

import com.littletrickster.usersbooks.db.models.Book


sealed class ListScreenAction {
    data object Refresh : ListScreenAction()
    data class ChangeBook(val book: Book) : ListScreenAction()
    data object Back : ListScreenAction()

}


sealed class ListScreenEffect {
    data class Error(val string: String) : ListScreenEffect()
    data class ChangeBook(val book: Book) : ListScreenEffect()
    data object Back : ListScreenEffect()
}


data class ListScreenState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val title:String = ""
)
