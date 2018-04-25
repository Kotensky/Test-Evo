package com.kotensky.testevo.di.modules

import android.arch.persistence.room.Room
import android.content.Context
import com.kotensky.testevo.application.TestEvoApplication
import com.kotensky.testevo.model.room.FavoritesDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule constructor(private val application: TestEvoApplication) {

    @Provides
    @Singleton
    fun provideApplication(): TestEvoApplication = application

    @Provides
    @Singleton
    fun provideApplicationContext(): Context = application

    @Provides
    fun providesAppDatabase(context: Context): FavoritesDatabase =
            Room.databaseBuilder(context, FavoritesDatabase::class.java, "test_evo_fav_db").build()

    @Provides
    fun providesFavoritesDao(database: FavoritesDatabase) = database.favoritesDao()

}