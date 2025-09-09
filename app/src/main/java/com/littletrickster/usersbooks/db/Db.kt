package com.littletrickster.usersbooks.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.littletrickster.usersbooks.db.models.Book
import com.littletrickster.usersbooks.db.models.FullBook
import com.littletrickster.usersbooks.db.models.Status

@TypeConverters(OffsetDateTimeConverter::class)
@Database(entities = [Book::class, FullBook::class, Status::class], version = 1)
abstract class Db : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        fun make(context: Context): Db {
            return Room
                .databaseBuilder(context, Db::class.java, "books_database")
                .fallbackToDestructiveMigration(true)
                .build()
        }

    }

}