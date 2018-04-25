package com.kotensky.testevo.model.entities

import com.google.gson.annotations.SerializedName

data class ProductsCatalogEntity(@SerializedName("possible_sorts")
                                 val sortList: ArrayList<String>?,
                                 val results: ArrayList<ProductEntity>?)