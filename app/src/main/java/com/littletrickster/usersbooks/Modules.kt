package com.littletrickster.usersbooks

import android.app.Application
import com.littletrickster.usersbooks.api.BooksApi
import com.littletrickster.usersbooks.db.Db
import com.littletrickster.usersbooks.db.models.BookDao
import com.littletrickster.usersbooks.db.models.BookListDao
import com.littletrickster.usersbooks.db.models.FullBookDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Modules {

    @Provides
    @Singleton
    fun providesDispatchers(): AppDispatchers {
        return object : AppDispatchers {
            override val Main = Dispatchers.Main
            override val IO = Dispatchers.IO
            override val Default = Dispatchers.Default
        }
    }


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient
        .Builder()
        .build()

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit = Retrofit.Builder().client(okHttpClient)
        .baseUrl("https://my-json-server.typicode.com/KeskoSenukaiDigital/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()


    @Provides
    @Singleton
    fun provideBooksApi(retrofit: Retrofit): BooksApi = retrofit.create<BooksApi>()

    @Provides
    @Singleton
    fun provideDb(context: Application): Db = Db.make(context)

    @Provides
    @Singleton
    fun provideBookDao(db: Db): BookDao = db.bookDao()

    @Provides
    @Singleton
    fun provideStatusDao(db: Db): BookListDao = db.listsDao()

    @Provides
    @Singleton
    fun provideFullBookDao(db: Db): FullBookDao = db.fullBookDao()

    @Provides
    @Singleton
    fun provideLibraryRepo(
        booksApi: BooksApi,
        bookDao: BookDao,
        bookListDao: BookListDao,
        fullBookDao: FullBookDao
    ): LibraryRepo = LibraryRepo(
        booksApi = booksApi,
        bookDao = bookDao,
        bookListDao = bookListDao,
        fullBookDao = fullBookDao
    )


}