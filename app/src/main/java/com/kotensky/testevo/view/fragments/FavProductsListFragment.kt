package com.kotensky.testevo.view.fragments

import android.view.Menu
import android.view.MenuInflater
import com.kotensky.testevo.R
import com.kotensky.testevo.application.TestEvoApplication
import com.kotensky.testevo.listeners.OnChangeTitleListener
import com.kotensky.testevo.model.entities.ProductEntity
import com.kotensky.testevo.presenter.FavoriteProductsPresenter
import javax.inject.Inject


class FavProductsListFragment : ProductsListFragment(), OnChangeTitleListener {

    @Inject
    lateinit var favProductPresenter: FavoriteProductsPresenter

    override fun inject() {
        TestEvoApplication.applicationComponent.inject(this)
    }

    override fun initPresenter() {
        presenter = favProductPresenter
        presenter.view = this
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        activity?.menuInflater?.inflate(R.menu.menu_fav_products_list, menu)
        bindOptionsMenu(menu)
    }


    override fun onFavoriteStateChanged(productEntity: ProductEntity) {
        if (productEntity.isFavorite) {
            super.onFavoriteStateChanged(productEntity)
        } else {
            var position = 0
            if (products.contains(productEntity)) {
                position = products.indexOf(productEntity)
                products.remove(productEntity)
            }
            productsAdapter.notifyItemRemoved(position)
            productsAdapter.notifyItemRangeChanged(position, products.size - 1)
        }
    }

    override fun onLoadMore() {

    }

    override fun getTitle(): String {
        return activity?.getString(R.string.favorites).toString()
    }

}