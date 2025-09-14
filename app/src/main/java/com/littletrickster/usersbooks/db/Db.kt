package com.littletrickster.usersbooks.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.littletrickster.usersbooks.db.models.Book
import com.littletrickster.usersbooks.db.models.BookDao
import com.littletrickster.usersbooks.db.models.FullBook
import com.littletrickster.usersbooks.db.models.FullBookDao
import com.littletrickster.usersbooks.db.models.BookList
import com.littletrickster.usersbooks.db.models.BookListDao

@TypeConverters(OffsetDateTimeConverter::class)
@Database(entities = [Book::class, FullBook::class, BookList::class], version = 1)
abstract class Db : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun statusDao(): BookListDao
    abstract fun fullBookDao(): FullBookDao

    companion object {
        fun make(context: Context): Db {
            return Room
                .databaseBuilder(context, Db::class.java, "books_database")
                .fallbackToDestructiveMigration(true)
                .build()
        }

    }

}