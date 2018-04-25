package com.kotensky.testevo.model.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "products")
data class ProductEntity(@SerializedName("id")
                         @PrimaryKey
                         val id: Long,
                         @SerializedName("name")
                         val name: String,
                         @SerializedName("price_currency")
                         val priceCurrency: String,
                         @SerializedName("discounted_price")
                         val discountedPrice: String,
                         @SerializedName("price")
                         val price: String,
                         @SerializedName("url_main_image_200x200")
                         val imagePath: String? = null,
                         var isFavorite: Boolean = false)