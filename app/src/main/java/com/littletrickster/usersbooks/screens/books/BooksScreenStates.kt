package com.littletrickster.usersbooks.screens.books

import com.littletrickster.usersbooks.db.models.Book


sealed class BooksScreenAction {
    data object Refresh : BooksScreenAction()
    data class ChangeBook(val book: Book) : BooksScreenAction()
    data class ShowAll(val listId: Int) : BooksScreenAction()
    data object Back : BooksScreenAction()

}


sealed class BookScreenEffect {
    data class Error(val string: String) : BookScreenEffect()
    data class ChangeBook(val book: Book) : BookScreenEffect()
    data class ShowAll(val listId: Int) : BookScreenEffect()
    data object Back : BookScreenEffect()
}


data class BooksScreenState(
    val isLoading: Boolean = false,
    val typeAndBooks: List<ListIdTitleBooks> = emptyList(),
)


data class ListIdTitleBooks(
    val listId: Int,
    val listTitle: String?,
    val list: List<Book>
)

