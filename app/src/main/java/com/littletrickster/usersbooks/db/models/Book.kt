package com.littletrickster.usersbooks.db.models

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction

@Entity
data class Book(
    @PrimaryKey
    val id: Int,
    @ColumnInfo("list_id")
    val listId: Int = 1,
    val title: String = "",
    val img: String = ""
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