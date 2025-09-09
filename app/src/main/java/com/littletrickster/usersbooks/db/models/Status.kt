package com.littletrickster.usersbooks.db.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction

@Entity
data class Status(
    @PrimaryKey
    val id: Int,
    val title: String
)

@Dao
interface StatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg status: Status)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(statusList: List<Status>)

    @Delete
    fun delete(vararg status: Status)

    @Delete
    fun delete(statusList: List<Status>)

    @Query("DELETE FROM Status WHERE id NOT IN(:ids)")
    fun deleteNotIn(ids: List<Int>)

    @Query("DELETE FROM Status")
    fun deleteAll()

    @Transaction
    fun replaceAllWith(statuses: List<Status>) {
        if (statuses.isEmpty()) {
            deleteAll()
            return
        }

        insert(statuses)

        val ids = statuses.map { it.id }
        deleteNotIn(ids)
    }
}