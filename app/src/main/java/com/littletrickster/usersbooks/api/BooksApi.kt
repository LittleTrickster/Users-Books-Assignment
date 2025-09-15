package com.littletrickster.usersbooks.api

import com.littletrickster.usersbooks.api.models.Book
import com.littletrickster.usersbooks.api.models.FullBook
import com.littletrickster.usersbooks.api.models.BookList
import retrofit2.http.GET
import retrofit2.http.Path

interface BooTksApi {
    @GET("assignment/books")
    suspend fun getBooks(): List<Book>

    @GET("assignment/book/{book_id}")
    suspend fun getFullBook(@Path("book_id") bookId: Int): FullBook?

    @GET("assignment/lists")
    suspend fun getLists(): List<BookList>
}