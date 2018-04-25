package com.kotensky.testevo.presenter

import com.kotensky.testevo.model.entities.ProductEntity
import com.kotensky.testevo.view.views.ProductListView

interface IProductsPresenter {

    var view: ProductListView?

    var sortList: ArrayList<String>?

    var sort: String?

    var filter: String?

    fun loadProducts(isPagination: Boolean = false)

    fun changeFavoriteState(productEntity: ProductEntity)

    fun isGridViewType(): Boolean

    fun setIsGridViewType(gridViewType: Boolean)

    fun destroy()

}