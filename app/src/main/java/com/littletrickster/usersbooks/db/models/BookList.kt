package com.littletrickster.usersbooks.db.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Entity
data class BookList(
    @PrimaryKey
    val id: Int,
    val title: String
)


@Dao
interface BookListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg bookLists: BookList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bookListList: List<BookList>)

    @Delete
    fun delete(vararg bookLists: BookList)

    @Delete
    fun delete(bookListList: List<BookList>)

    @Query("DELETE FROM BookList WHERE id NOT IN(:ids)")
    fun deleteNotIn(ids: List<Int>)


    @Query("DELETE FROM BookList")
    fun deleteAll()

    @Query("SELECT * FROM BookList")
    fun getAllStatusFlow(): Flow<List<BookList>>

    @Query("SELECT * FROM BookList WHERE id = :id")
    fun getById(id:Int): Flow<BookList?>

    @Transaction
    fun replaceAllWith(bookLists: List<BookList>) {
        if (bookLists.isEmpty()) {
            deleteAll()
            return
        }

        insert(bookLists)

        val ids = bookLists.map { it.id }
        deleteNotIn(ids)
    }
}