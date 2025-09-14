package com.littletrickster.usersbooks.db.models

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Entity
data class FullBook(
    @PrimaryKey
    val id: Int,
    @ColumnInfo("list_id")
    val listId: Int,
    val isbn: String,
    @ColumnInfo("publication_date")
    val publicationDate: OffsetDateTime,
    val author: String,
    val title: String,
    val img: String,
    val description: String
)

@Dao
interface FullBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg fullBook: FullBook)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fullBook: List<FullBook>)


    @Delete
    fun delete(fullBook: FullBook)
    @Delete
    fun delete(books: List<FullBook>)

    @Query("SELECT * FROM FullBook WHERE id =:id")
    fun getByIdFlow(id: Int): Flow<FullBook?>


    @Query("DELETE FROM FullBook WHERE id NOT IN(:ids)")
    fun deleteNotIn(ids: List<Int>)

    @Query("DELETE FROM FullBook")
    fun deleteAll()
}