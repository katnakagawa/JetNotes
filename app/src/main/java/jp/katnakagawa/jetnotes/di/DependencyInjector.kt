package jp.katnakagawa.jetnotes.di

import android.content.Context
import androidx.room.Room
import jp.katnakagawa.jetnotes.data.database.AppDatabase
import jp.katnakagawa.jetnotes.data.database.dbmapper.DbMapper
import jp.katnakagawa.jetnotes.data.database.dbmapper.DbMapperImpl
import jp.katnakagawa.jetnotes.data.repository.Repository
import jp.katnakagawa.jetnotes.data.repository.RepositoryImpl

class DependencyInjector(applicationContext: Context) {

    val repository: Repository by lazy { provideRepository(database) }

    private val database: AppDatabase by lazy { provideDatabase(applicationContext) }

    private val dbMapper: DbMapper = DbMapperImpl()

    private fun provideDatabase(applicationContext: Context): AppDatabase =
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()

    private fun provideRepository(database: AppDatabase): Repository {
        val noteDao = database.noteDao()
        val colorDao = database.colorDao()

        return RepositoryImpl(noteDao, colorDao, dbMapper)
    }
}