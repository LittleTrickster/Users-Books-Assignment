package com.littletrickster.usersbooks.api


import android.util.Log
import com.littletrickster.usersbooks.Modules
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class BooksApiIntegrationTest {

    private lateinit var server: MockWebServer
    private lateinit var api: BooksApi




    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.isLoggable(any(), any()) } returns false


        server = MockWebServer().apply { start() }

        api = Retrofit.Builder().client(Modules.provideOkHttpClient())
            .baseUrl(server.url("/"))
            .addConverterFactory(Modules.provideJson().asConverterFactory("application/json".toMediaType()))
            .build()
            .create(BooksApi::class.java)

    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `getBooks returns list and hits correct path`() = runTest {
        val body = """
            [
              {
                "id": 1,
                "list_id": 2,
                "title": "The old man and the sea",
                "img": "https://covers.openlibrary.org/b/id/7884851-L.jpg"
              },
              {
                "id": 2,
                "list_id": 3,
                "title": "The Great Gatsby",
                "img": "https://covers.openlibrary.org/b/id/9367345-L.jpg"
              },
              {
                "id": 3,
                "list_id": 3,
                "title": "Moby Dick",
                "img": "https://covers.openlibrary.org/b/id/11681548-L.jpg"
              }
            ]
        """.trimIndent()

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(body)
        )

        val result = api.getBooks()

        assertEquals(3, result.size)
        assertEquals(1, result[0].id)
        assertEquals(2, result[0].listId)
        assertEquals("The old man and the sea", result[0].title)
        assertEquals("https://covers.openlibrary.org/b/id/7884851-L.jpg", result[0].img)

        val request = server.takeRequest()
        assertEquals("/assignment/books", request.path)
        assertEquals("GET", request.method)
    }

    @Test
    fun `getLists returns lists and hits correct path`() = runTest {
        val body = """
            [
              {
                "id": 1,
                "title": "Reading"
              },
              {
                "id": 2,
                "title": "Read"
              },
              {
                "id": 3,
                "title": "Plan to read"
              }
            ]
        """.trimIndent()

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(body)
        )

        val lists = api.getLists()

        assertEquals(3, lists.size)
        assertEquals(1, lists[0].id)
        assertEquals("Reading", lists[0].title)

        val request = server.takeRequest()
        assertEquals("/assignment/lists", request.path)
        assertEquals("GET", request.method)
    }

    @Test
    fun `getFullBook returns object on 200 and hits path with id`() = runTest {
        val body = """
            {
              "id": 1,
              "list_id": 2,
              "isbn": "0684801221",
              "publication_date": "1995-05-05T00:00:00+03:00",
              "author": "Ernest Hemingway",
              "title": "The old man and the sea",
              "img": "https://covers.openlibrary.org/b/id/7884851-L.jpg",
              "description": "The Old Man and the Sea is one of Hemingway's most enduring works."
            }
        """.trimIndent()

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(body)
        )

        val full = api.getFullBook(1)
        requireNotNull(full)

        assertEquals(1, full.id)
        assertEquals(2, full.listId)
        assertEquals("0684801221", full.isbn)
        assertEquals("Ernest Hemingway", full.author)
        assertEquals("The old man and the sea", full.title)
        assertEquals("https://covers.openlibrary.org/b/id/7884851-L.jpg", full.img)
        assertTrue(full.description.startsWith("The Old Man and the Sea"))

        val request = server.takeRequest()
        assertEquals("/assignment/book/1", request.path)
        assertEquals("GET", request.method)
    }

    @Test
    fun `getFullBook returns null on 404 No Content`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json")
                .setBody("{}")
        )

        val result = try {
            api.getFullBook(999)
        } catch (e: HttpException) {
            if (e.code() == 404) null
            else throw e
        }

        assertNull(result)
//
        val request = server.takeRequest()
        assertEquals("/assignment/book/999", request.path)
        assertEquals("GET", request.method)
    }
}