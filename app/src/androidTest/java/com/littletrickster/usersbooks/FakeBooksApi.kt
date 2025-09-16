package com.littletrickster.usersbooks

import com.littletrickster.usersbooks.api.BooksApi
import com.littletrickster.usersbooks.api.models.Book
import com.littletrickster.usersbooks.api.models.BookList
import com.littletrickster.usersbooks.api.models.FullBook
import kotlinx.serialization.json.Json



class FakeBooksApi : BooksApi {
    private var books: MutableList<Book> = mutableListOf()
    private var lists: MutableList<BookList> = mutableListOf()
    private var full: HashMap<Int, FullBook?> = hashMapOf()

    override suspend fun getBooks(): List<Book> = books
    override suspend fun getLists(): List<BookList> = lists
    override suspend fun getFullBook(bookId: Int): FullBook? = full[bookId]

    init {
        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
        json.decodeFromString<List<Book>>(
            """[
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
  },
  {
    "id": 4,
    "list_id": 2,
    "title": "The grapes of wrath",
    "img": "https://covers.openlibrary.org/b/id/9292315-L.jpg"
  },
  {
    "id": 5,
    "list_id": 3,
    "title": "Invisible Man",
    "img": "https://covers.openlibrary.org/b/id/9367108-L.jpg"
  },
  {
    "id": 6,
    "list_id": 2,
    "title": "The Lord of the Rings",
    "img": "https://covers.openlibrary.org/b/id/8314545-L.jpg"
  },
  {
    "id": 7,
    "list_id": 3,
    "title": "A Clockwork Orange",
    "img": "https://covers.openlibrary.org/b/id/8401469-L.jpg"
  },
  {
    "id": 8,
    "list_id": 2,
    "title": "Of Mice and Men",
    "img": "https://covers.openlibrary.org/b/id/8465280-L.jpg"
  },
  {
    "id": 9,
    "list_id": 2,
    "title": "The Stand",
    "img": "https://covers.openlibrary.org/b/id/8579743-L.jpg"
  },
  {
    "id": 10,
    "list_id": 1,
    "title": "The Hitchhikers Guide to the Galaxy",
    "img": "https://covers.openlibrary.org/b/id/11464688-L.jpg"
  },
  {
    "id": 11,
    "list_id": 2,
    "title": "Harry Potter and the Philosopher's Stone",
    "img": "https://covers.openlibrary.org/b/id/12376736-L.jpg"
  },
  {
    "id": 12,
    "list_id": 1,
    "title": "Dune",
    "img": "https://covers.openlibrary.org/b/id/12181264-L.jpg"
  }
]"""
        ).also(books::addAll)

        json.decodeFromString<List<BookList>>(
            """[
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
]"""
        ).also(lists::addAll)

        json.decodeFromString<FullBook>(
            """{
  "id": 12,
  "list_id": 1,
  "isbn": "0441013597",
  "publication_date": "1965-08-01T00:00:00+03:00",
  "author": "Frank Herbert",
  "title": "Dune",
  "img": "https://covers.openlibrary.org/b/id/12181264-L.jpg",
  "description": "Set on the desert planet Arrakis, Dune is the story of the boy Paul Atreides, heir to a noble family tasked with ruling an inhospitable world where the only thing of value is the \"spice\" melange, a drug capable of extending life and enhancing consciousness. Coveted across the known universe, melange is a prize worth killing for...\n\nWhen House Atreides is betrayed, the destruction of Paul's family will set the boy on a journey toward a destiny greater than he could ever have imagined. And as he evolves into the mysterious man known as Muad'Dib, he will bring to fruition humankind's most ancient and unattainable dream.\n\nA stunning blend of adventure and mysticism, environmentalism and politics, Dune won the first Nebula Award, shared the Hugo Award, and formed the basis of what is undoubtedly the grandest epic in science fiction."
}"""
        ).also { full[it.id] = it }


    }
}
