package com.littletrickster.usersbooks

import android.content.Context
import androidx.room.Room
import com.littletrickster.usersbooks.db.Db
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DbModule::class]
)
object InMemoryDatabaseModule {
    @Provides
    @Singleton
    fun provideInMemoryDb(@ApplicationContext context: Context): Db =
        Room.inMemoryDatabaseBuilder(context, Db::class.java)
            .allowMainThreadQueries()
            .build()
}