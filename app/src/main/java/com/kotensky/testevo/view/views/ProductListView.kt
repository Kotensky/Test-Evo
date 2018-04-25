package com.kotensky.testevo.view.views

import com.kotensky.testevo.model.entities.ProductEntity

interface ProductListView {

    fun showProducts(products: ArrayList<ProductEntity>)

    fun onFavoriteStateChanged(productEntity: ProductEntity)

    fun showErrorMessage()

    fun hideLoading()

}