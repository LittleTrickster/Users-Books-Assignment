package com.littletrickster.usersbooks.screens.books

import com.littletrickster.usersbooks.api.BooksApi
import com.littletrickster.usersbooks.db.models.Book
import com.littletrickster.usersbooks.db.models.BookDao
import com.littletrickster.usersbooks.db.models.BookList
import com.littletrickster.usersbooks.db.models.BookListDao
import com.littletrickster.usersbooks.db.models.BookWithListTitle
import com.littletrickster.usersbooks.db.models.FullBook
import com.littletrickster.usersbooks.db.models.FullBookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import kotlin.collections.addAll
import com.littletrickster.usersbooks.api.models.Book as ApiBook
import com.littletrickster.usersbooks.api.models.BookList as ApiBookList
import com.littletrickster.usersbooks.api.models.FullBook as ApiFullBook


// Fakes/in-memory implementations
class InMemoryBookDao : BookDao {
    val books = mutableListOf<Book>()
    val booksFlow = MutableStateFlow<List<Book>>(emptyList())
    val booksWithTitlesFlow = MutableStateFlow<List<BookWithListTitle>>(emptyList())

    override fun insert(vararg book: Book) {
        books.addAll(book)
        booksFlow.value = books.toList()
    }

    override fun insert(list: List<Book>) {
        books.addAll(list)
        booksFlow.value = books.toList()
    }

    override fun delete(vararg book: Book) {
        books.removeAll(book.toSet())
        booksFlow.value = books.toList()
    }

    override fun delete(list: List<Book>) {
        books.removeAll(list.toSet())
        booksFlow.value = books.toList()
    }

    override suspend fun deleteAll() {
        books.clear()
        booksFlow.value = emptyList()
    }

    override suspend fun deleteNotIn(ids: List<Int>) {
        books.removeAll { it.id !in ids }
        booksFlow.value = books.toList()
    }

    override fun getAllBooksFlow(): Flow<List<Book>> = booksFlow

    override fun getAllBooksByListIdFlow(listId: Int): Flow<List<Book>> =
        booksFlow.map { it.filter { it.listId == listId } }

    override fun getBooksWithTitles(limit: Int): Flow<List<BookWithListTitle>> = booksWithTitlesFlow

    override fun getBookWithTitleById(id: Long): Flow<BookWithListTitle?> =
        MutableStateFlow(booksWithTitlesFlow.value.firstOrNull { it.book.id.toLong() == id })
}

class InMemoryBookListDao : BookListDao {
    private val list = mutableListOf<BookList>()
    private val flow = MutableStateFlow<List<BookList>>(emptyList())

    override fun insert(vararg bookLists: BookList) {
        list.addAll(bookLists)
        flow.value = list.toList()
    }

    override fun insert(bookListList: List<BookList>) {
        list.addAll(bookListList)
        flow.value = list.toList()
    }

    override fun delete(vararg bookLists: BookList) {
        list.removeAll(bookLists.toSet())
        flow.value = list.toList()
    }

    override fun delete(bookListList: List<BookList>) {
        list.removeAll(bookListList.toSet())
        flow.value = list.toList()
    }

    override fun deleteNotIn(ids: List<Int>) {
        list.removeAll { it.id !in ids }
        flow.value = list.toList()
    }

    override fun deleteAll() {
        list.clear()
        flow.value = emptyList()
    }

    override fun getAllStatusFlow(): Flow<List<BookList>> = flow

    override fun getById(id: Int): Flow<BookList?> = flow.map { it.firstOrNull { it.id == id } }

    override fun replaceAllWith(bookLists: List<BookList>) {
        list.addAll(bookLists)
        flow.value = list.toList()
    }
}

class InMemoryFullBookDao : FullBookDao {
    private val books = mutableListOf<FullBook>()
    private val flow = MutableStateFlow<List<FullBook>>(emptyList())


    override fun insert(vararg fullBook: FullBook) {
        val new = books + fullBook
        books.addAll(new)
        flow.value = new
    }

    override fun insert(fullBook: List<FullBook>) {
        val new = books + fullBook
        books.addAll(new)
        flow.value = new
    }

    override fun delete(fullBook: FullBook) {
        books.remove(fullBook)
        flow.value = books
    }

    override fun delete(books: List<FullBook>) {
        books.forEach { delete(it) }
    }

    override fun getByIdFlow(id: Int): Flow<FullBook?> = flow.map { it.firstOrNull { it.id == id } }

    override fun all(): Flow<List<FullBook>> = flow

    override fun deleteNotIn(ids: List<Int>) {
        books.removeAll { it.id !in ids }
        flow.value = books.toList()
    }

    override fun deleteAll() {
        books.clear()
        flow.value = books
    }
}

class SucceedingApi : BooksApi {
    override suspend fun getBooks(): List<ApiBook> = listOf(
        ApiBook(1, listId = 2, title = "A"),
        ApiBook(2, listId = 3, title = "B"),
        ApiBook(3, listId = 3, title = "C"),
        ApiBook(4, listId = 2, title = "D"),
        ApiBook(5, listId = 2, title = "E"),
        ApiBook(10, listId = 1, title = "F"),
    )

    override suspend fun getFullBook(bookId: Int): ApiFullBook? = ApiFullBook(
        id = bookId,
        listId = 2,
        isbn = "isbn",
        publicationDate = OffsetDateTime.parse("2025-09-15T17:15:30+02:00"),
        author = "author",
        title = "title",
        img = "",
        description = "desc"
    )

    override suspend fun getLists(): List<ApiBookList> = listOf(
        ApiBookList(id = 1, title = "Reading"),
        ApiBookList(id = 2, title = "Read"),
        ApiBookList(id = 3, title = "Plan to read"),

        )
}