package com.littletrickster.usersbooks.screens.books

import com.littletrickster.usersbooks.AppDispatchers
import com.littletrickster.usersbooks.LibraryRepo
import com.littletrickster.usersbooks.api.BooksApi
import com.littletrickster.usersbooks.db.models.Book
import com.littletrickster.usersbooks.db.models.BookWithListTitle
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds
import com.littletrickster.usersbooks.api.models.Book as ApiBook
import com.littletrickster.usersbooks.api.models.BookList as ApiBookList
import com.littletrickster.usersbooks.api.models.FullBook as ApiFullBook

@OptIn(ExperimentalCoroutinesApi::class)
class BooksViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testDispatchers = object : AppDispatchers {
        override val Main = testDispatcher
        override val IO = testDispatcher
        override val Default = testDispatcher
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_observesBook_andStartsRefresh() = runTest(testDispatcher) {
        val fakeBookDao = InMemoryBookDao()
        val fakeBookListDao = InMemoryBookListDao()
        val fakeFullBookDao = InMemoryFullBookDao()
        val fakeApi = SucceedingApi()
        val repo = LibraryRepo(fakeApi, fakeBookDao, fakeBookListDao, fakeFullBookDao)

        // Pre-populate flow
        fakeBookDao.booksWithTitlesFlow.value = listOf(
            BookWithListTitle(Book(1, listId = 2, title = "A"), statusTitle = "Read"),
            BookWithListTitle(Book(2, listId = 3, title = "B"), statusTitle = "Plan to read"),
            BookWithListTitle(Book(3, listId = 3, title = "C"), statusTitle = "Plan to read"),
            BookWithListTitle(Book(4, listId = 2, title = "D"), statusTitle = "Read"),
            BookWithListTitle(Book(5, listId = 2, title = "E"), statusTitle = "Read"),
            BookWithListTitle(Book(10, listId = 1, title = "F"), statusTitle = "Reading"),

            )

        val vm = BooksViewModel(repo, testDispatchers)

        vm.onAction(BooksScreenAction.Refresh)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.state.value

        assertFalse("should not be loading after refresh completes", state.isLoading)
        assertEquals(3, state.typeAndBooks.size)
        val group = state.typeAndBooks.first { it.listId == 2 }
        assertEquals("Read", group.listTitle)
        assertEquals(listOf(1, 4, 5), group.list.map { it.id })
    }

    @Test
    fun refresh_error_emitsEffect_andStopsLoading() = runTest(testDispatcher) {
        val fakeBookDao = InMemoryBookDao()
        val fakeBookListDao = InMemoryBookListDao()
        val fakeFullBookDao = InMemoryFullBookDao()
        val failingApi = object : BooksApi {
            override suspend fun getBooks(): List<ApiBook> = throw RuntimeException("boom")
            override suspend fun getFullBook(bookId: Int): ApiFullBook? = null
            override suspend fun getLists(): List<ApiBookList> = emptyList()
        }
        val repo = LibraryRepo(failingApi, fakeBookDao, fakeBookListDao, fakeFullBookDao)
        val vm = BooksViewModel(repo, testDispatchers)


        assertEquals(vm.state.value.refreshTimes, 0)

        //subscribe first since no reply on effects
        val effectJob = async(start = CoroutineStart.UNDISPATCHED) { vm.effects.first() }
        vm.onAction(BooksScreenAction.Refresh)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(vm.state.value.refreshTimes, 1)


        // Verify loading is false after error
        assertFalse(vm.state.value.isLoading)


        val effect = effectJob.await()
        // Collect one effect
        assertTrue(effect is BookScreenEffect.Error)
        val msg = (effect as BookScreenEffect.Error).string
        assertTrue(msg.contains("boom"))
    }

    @Test
    fun action_effect_checking() = runTest(testDispatcher, timeout = 5.seconds) {
        val fakeBookDao = InMemoryBookDao()
        val fakeBookListDao = InMemoryBookListDao()
        val fakeFullBookDao = InMemoryFullBookDao()
        val fakeApi = SucceedingApi()
        val repo = LibraryRepo(fakeApi, fakeBookDao, fakeBookListDao, fakeFullBookDao)
        val vm = BooksViewModel(repo, testDispatchers)

        val changeBookJob = async(start = CoroutineStart.UNDISPATCHED) { vm.effects.first() }
        vm.onAction(BooksScreenAction.ChangeBook(Book(1)))
        assertTrue(changeBookJob.await() is BookScreenEffect.ChangeBook)

        val showAllJob = async(start = CoroutineStart.UNDISPATCHED) { vm.effects.first() }
        vm.onAction(BooksScreenAction.ShowAll(1))
        assertTrue(showAllJob.await() is BookScreenEffect.ShowAll)

        val backJob = async(start = CoroutineStart.UNDISPATCHED) { vm.effects.first() }
        vm.onAction(BooksScreenAction.Back)
        assertTrue(backJob.await() is BookScreenEffect.Back)

    }


}
