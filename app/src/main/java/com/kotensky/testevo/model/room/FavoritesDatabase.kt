package com.kotensky.testevo.model.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.kotensky.testevo.model.entities.ProductEntity
import com.kotensky.testevo.model.room.dao.FavoritesDao

@Database(entities = [(ProductEntity::class)], version = 1, exportSchema = false)
abstract class FavoritesDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao
}