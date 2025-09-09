package com.littletrickster.usersbooks.books

import com.littletrickster.usersbooks.api.models.Book
import kotlin.Int
import kotlin.Pair
import kotlin.collections.List

data class BooksScreenState(
    val isLoading: Boolean = false,
    val typeAndBooks: List<Pair<Int, List<Book>>> = emptyList(),
    val titleMap: Map<Int, String> = emptyMap(),
)