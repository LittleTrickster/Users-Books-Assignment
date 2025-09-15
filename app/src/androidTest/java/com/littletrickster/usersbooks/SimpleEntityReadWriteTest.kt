package com.littletrickster.usersbooks

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.littletrickster.usersbooks.db.Db
import com.littletrickster.usersbooks.db.OffsetDateTimeConverter
import com.littletrickster.usersbooks.db.models.Book
import com.littletrickster.usersbooks.db.models.BookDao
import com.littletrickster.usersbooks.db.models.BookList
import com.littletrickster.usersbooks.db.models.BookListDao
import com.littletrickster.usersbooks.db.models.FullBook
import com.littletrickster.usersbooks.db.models.FullBookDao
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.OffsetDateTime

@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
    private lateinit var bookDao: BookDao
    private lateinit var fullBookDao: FullBookDao
    private lateinit var listsDao: BookListDao

    private lateinit var db: Db

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, Db::class.java
        ).build()
        bookDao = db.bookDao()
        fullBookDao = db.fullBookDao()
        listsDao = db.listsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun used_query_checks() = runTest {

        val book = Book(id = 1, listId = 2)
        bookDao.insert(book)
        val fromDb1 = bookDao.getAllBooksFlow().first().first()
        assertEquals(book, fromDb1)

        val fromDb2 = bookDao.getAllBooksByListIdFlow(2).first().first()
        assertEquals(book, fromDb2)

        val newBook = Book(id = 1, listId = 2)
        bookDao.replaceAllWith(listOf(newBook))
        val afterReplace = bookDao.getAllBooksFlow().first()
        assertEquals(1, afterReplace.size)
        assertEquals(newBook, afterReplace.first())


        //Title join check
        assertEquals(null, bookDao.getBooksWithTitles(1).first().first().statusTitle)
        listsDao.insert(BookList(2, "Read"))
        assertEquals("Read", bookDao.getBooksWithTitles(1).first().first().statusTitle)



        listsDao.replaceAllWith(listOf(BookList(3, "Plan to read")))
        val bookLists1 = listsDao.getAllStatusFlow().first()
        assertEquals(1, bookLists1.size)
        assertEquals("Plan to read", bookLists1.first().title)


        val fullBook = FullBook(
            id = 1,
            listId = 2,
            isbn = "isbn",
            publicationDate = OffsetDateTime.now(),
            author = "Me",
            title = "Hello world",
            img = "",
            description = "1"
        )

        fullBookDao.insert(fullBook)

        assertEquals(fullBook, fullBookDao.getByIdFlow(1).first())

        fullBookDao.deleteAll()
        assertEquals(0, fullBookDao.all().first().size)

        bookDao.deleteAll()
        assertEquals(0, bookDao.getAllBooksFlow().first().size)

        listsDao.deleteAll()
        assertEquals(0, listsDao.getAllStatusFlow().first().size)




    }
}