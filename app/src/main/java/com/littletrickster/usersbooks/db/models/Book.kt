package com.littletrickster.usersbooks.db.models

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow


@Entity(
    indices = [Index("list_id")]
)
data class Book(
    @PrimaryKey
    val id: Int,
    @ColumnInfo("list_id")
    val listId: Int = 1,
    val title: String = "",
    val img: String = ""
)
data class BookWithListTitle(
    @Embedded val book: Book,
    @ColumnInfo(name = "list_title") val statusTitle: String?
)

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg nr: Book)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<Book>)

    @Delete
    fun delete(vararg book: Book)

    @Delete
    fun delete(list: List<Book>)

    @Query("DELETE FROM Book")
    suspend fun deleteAll()

    @Query("DELETE FROM Book WHERE id NOT IN(:ids)")
    suspend fun deleteNotIn(ids: List<Int>)

    @Query("SELECT * FROM Book")
    fun getAllBooksFlow(): Flow<List<Book>>

    @Query("SELECT * FROM Book where list_id = :listId ORDER BY id DESC")
    fun getAllBooksByListIdFlow(listId: Int): Flow<List<Book>>


    @Query(
        """
        WITH ranked AS (
            SELECT b.*,
                   ROW_NUMBER() OVER (PARTITION BY b.list_id ORDER BY b.id DESC) AS rn
            FROM Book b
        )
        SELECT r.id, r.list_id, r.title, r.img,
               s.title AS list_title
        FROM ranked r
        LEFT JOIN BookList s ON s.id = r.list_id
        WHERE r.rn <= :limit
        ORDER BY r.list_id, r.id DESC
    """
    )
    fun getBooksWithTitles(limit: Int = 5): Flow<List<BookWithListTitle>>


    @Query(
        """
    SELECT b.id, b.list_id, b.title, b.img,
           s.title AS list_title
    FROM Book b
    LEFT JOIN BookList s ON s.id = b.list_id
    WHERE b.id = :id
    """
    )
    fun getBookWithTitleById(id: Long): Flow<BookWithListTitle?>



    @Transaction
    suspend fun replaceAllWith(books: List<Book>) {
        if (books.isEmpty()) {
            deleteAll()
            return
        }

        // Upsert (updates existing, inserts new)
        insert(books)

        // Remove anything not in the provided set
        val ids = books.map { it.id }
        deleteNotIn(ids)
    }
}