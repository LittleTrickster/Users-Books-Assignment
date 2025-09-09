package com.littletrickster.usersbooks.api

import com.littletrickster.usersbooks.api.models.Book
import com.littletrickster.usersbooks.api.models.FullBook
import com.littletrickster.usersbooks.api.models.Status
import retrofit2.http.GET
import retrofit2.http.Path

interface BooksApi {
    @GET("assignment/books")
    suspend fun getBooks(): List<Book>

    @GET("assignment/book/{book_id}")
    suspend fun getBook(@Path("book_id") bookId: String): FullBook?

    @GET("assignment/lists")
    suspend fun getLists(): List<Status>
}