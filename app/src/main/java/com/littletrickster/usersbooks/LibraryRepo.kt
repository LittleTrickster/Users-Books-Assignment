package com.littletrickster.usersbooks

import com.littletrickster.usersbooks.api.BooksApi
import com.littletrickster.usersbooks.screens.books.ListIdTitleBooks
import com.littletrickster.usersbooks.db.models.Book
import com.littletrickster.usersbooks.db.models.BookDao
import com.littletrickster.usersbooks.db.models.BookList
import com.littletrickster.usersbooks.db.models.BookListDao
import com.littletrickster.usersbooks.db.models.FullBook
import com.littletrickster.usersbooks.db.models.FullBookDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import kotlin.collections.map

class LibraryRepo(
    private val booksApi: BooksApi,
    private val bookDao: BookDao,
    private val bookListDao: BookListDao,
    private val fullBookDao: FullBookDao,
) {


    //will not have empty list
    val bookFlow = bookDao.getBooksWithTitles(5)
        .map { books ->
            books.groupBy { it.book.listId }.map { (key, value) ->
                ListIdTitleBooks(
                    listId = key,
                    listTitle = value.firstOrNull()?.statusTitle,
                    list = value.map { it.book })
            }
        }.flowOn(Dispatchers.IO)


    fun booksByListId(listId: Int) = bookDao.getAllBooksByListIdFlow(listId)
    fun fullBookById(id: Int) = fullBookDao.getByIdFlow(id)
    fun fullBookByIdWithTitle(id: Int) = fullBookDao.getBookWithTitleById(id)

    fun listById(id: Int) = bookListDao.getById(id)

    suspend fun fetchBooksAndLists(): Unit = coroutineScope {
        val booksJob = async {
            booksApi.getBooks()
        }
        val statusListJob = async {
            booksApi.getLists()
        }
        val books = booksJob.await()
        val booksDbList = books.map {
            Book(id = it.id, listId = it.listId, title = it.title, img = it.img)
        }
        bookDao.replaceAllWith(booksDbList)
        val statusList = statusListJob.await()
        val bookListDb = statusList.map {
            BookList(id = it.id, title = it.title)
        }
        bookListDao.replaceAllWith(bookListDb)

    }

    suspend fun fetchFullBook(id: Int): Boolean {
        val book = try {
            booksApi.getFullBook(id) ?: return false
        } catch (e: HttpException) {
            if (e.code() == 404) return false
            else throw e
        }


        val fullBook = FullBook(
            id = book.id,
            listId = book.listId,
            isbn = book.isbn,
            publicationDate = book.publicationDate,
            author = book.author,
            title = book.title,
            img = book.img,
            description = book.description
        )
        fullBookDao.insert(fullBook)
        return true
    }


}