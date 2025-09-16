package com.littletrickster.usersbooks.screens.books

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.littletrickster.usersbooks.AppDispatchers
import com.littletrickster.usersbooks.FullBookScreen
import com.littletrickster.usersbooks.LibraryRepo
import com.littletrickster.usersbooks.api.BooksApi
import com.littletrickster.usersbooks.screens.fullbook.FullBookScreenAction
import com.littletrickster.usersbooks.screens.fullbook.FullBookScreenEffect
import com.littletrickster.usersbooks.screens.fullbook.FullBookViewModel
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds
import com.littletrickster.usersbooks.api.models.Book as ApiBook
import com.littletrickster.usersbooks.api.models.BookList as ApiBookList
import com.littletrickster.usersbooks.api.models.FullBook as ApiFullBook

@OptIn(ExperimentalCoroutinesApi::class)
class FullBookViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testDispatchers = object : AppDispatchers {
        override val Main = testDispatcher
        override val IO = testDispatcher
        override val Default = testDispatcher
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic("androidx.navigation.SavedStateHandleKt")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic("androidx.navigation.SavedStateHandleKt")
    }


    @Test
    fun init_observesBooks_andStartsRefresh() = runTest(testDispatcher) {

        val fakeBookDao = InMemoryBookDao()
        val fakeBookListDao = InMemoryBookListDao()
        val fakeFullBookDao = InMemoryFullBookDao()
        val fakeApi = SucceedingApi()
        val repo = LibraryRepo(fakeApi, fakeBookDao, fakeBookListDao, fakeFullBookDao)


        val handle = SavedStateHandle()
        every { handle.toRoute<FullBookScreen>() } returns FullBookScreen(id = 1)

        val vm = FullBookViewModel(handle, repo, testDispatchers)

        val effectJob = async { vm.effects.first() }

        vm.onAction(FullBookScreenAction.Refresh)
        val effect = withTimeoutOrNull(2000) {
            effectJob.await()
        }
        effectJob.cancel()

        assertEquals(null, effect)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.state.value

        assertFalse("should not be loading after refresh completes", state.isLoading)

        assertNotNull(state.book)
        assertEquals(1, state.book!!.id)
    }

    @Test
    fun refresh_error_emitsEffect_andStopsLoading() = runTest(testDispatcher) {
        val fakeBookDao = InMemoryBookDao()
        val fakeBookListDao = InMemoryBookListDao()
        val fakeFullBookDao = InMemoryFullBookDao()
        val failingApi = object : BooksApi {
            override suspend fun getBooks(): List<ApiBook> = throw RuntimeException("boom")
            override suspend fun getFullBook(bookId: Int): ApiFullBook? = when (bookId) {
                1 -> throw RuntimeException("boom")
                else -> null
            }

            override suspend fun getLists(): List<ApiBookList> = emptyList()
        }
        val repo = LibraryRepo(failingApi, fakeBookDao, fakeBookListDao, fakeFullBookDao)
        val handle = SavedStateHandle()
        every { handle.toRoute<FullBookScreen>() } returns FullBookScreen(id = 1)



        val vm = FullBookViewModel(handle, repo, testDispatchers)

        //subscribe first since no reply on effects
        val effectJob = async { vm.effects.first() }
        vm.onAction(FullBookScreenAction.Refresh)

        testDispatcher.scheduler.advanceUntilIdle()

        // Verify loading is false after error
        assertFalse(vm.state.value.isLoading)


        val effect = effectJob.await()
        // Collect one effect
        assertTrue(effect is FullBookScreenEffect.Error)
        val msg = (effect as FullBookScreenEffect.Error).string
        assertEquals("boom", msg)


    }

    @Test
    fun action_effect_checking() = runTest(testDispatcher, timeout = 5.seconds) {
        val fakeBookDao = InMemoryBookDao()
        val fakeBookListDao = InMemoryBookListDao()
        val fakeFullBookDao = InMemoryFullBookDao()
        val fakeApi = SucceedingApi()
        val repo = LibraryRepo(fakeApi, fakeBookDao, fakeBookListDao, fakeFullBookDao)


        val handle = SavedStateHandle()
        every { handle.toRoute<FullBookScreen>() } returns FullBookScreen(id = 1)

        val vm = FullBookViewModel(handle, repo, testDispatchers)


        val backJob = async(start = CoroutineStart.UNDISPATCHED) { vm.effects.first() }
        vm.onAction(FullBookScreenAction.Back)
        assertTrue(backJob.await() is FullBookScreenEffect.Back)

    }


}
