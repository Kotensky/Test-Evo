package com.kotensky.testevo.model.room.dao

import android.arch.persistence.room.*
import com.kotensky.testevo.model.entities.ProductEntity
import io.reactivex.Single

@Dao
interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(task: ProductEntity)

    @Delete
    fun removeFromFavorite(product: ProductEntity)

    @Query("SELECT * FROM products")
    fun getFavorites(): Single<List<ProductEntity>>

    @Query("SELECT id FROM products")
    fun getFavoriteIds(): Single<List<Long>>

    @Query("UPDATE products SET imagePath = :image WHERE id = :productId")
    fun updateNewsImagePath(productId: Long, image: String?)

}