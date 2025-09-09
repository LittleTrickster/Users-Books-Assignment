package com.littletrickster.usersbooks.books

import com.littletrickster.usersbooks.api.models.Book

sealed class BooksScreenAction {
    data object Back : BooksScreenAction()
    data class ChangeBook(val book: Book) : BooksScreenAction()
    data class ShowAll(val id: Long) : BooksScreenAction()
}