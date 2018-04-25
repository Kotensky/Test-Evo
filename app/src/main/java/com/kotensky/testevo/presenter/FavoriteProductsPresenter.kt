package com.kotensky.testevo.presenter

import com.kotensky.testevo.managers.ImageLoaderManager
import com.kotensky.testevo.managers.SharedPreferencesManager
import com.kotensky.testevo.model.entities.ProductEntity
import com.kotensky.testevo.model.network.ApiRequestService
import com.kotensky.testevo.model.room.dao.FavoritesDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class FavoriteProductsPresenter @Inject constructor(apiRequestService: ApiRequestService,
                                                    sharedPreferencesManager: SharedPreferencesManager,
                                                    imageLoaderManager: ImageLoaderManager,
                                                    private val favoritesDao: FavoritesDao)
    : ProductsPresenter(apiRequestService, sharedPreferencesManager, imageLoaderManager, favoritesDao) {

    override fun loadProducts(isPagination: Boolean) {
        compositeDisposable?.add(favoritesDao.getFavorites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.hideLoading()
                    originalList.clear()
                    originalList.addAll(it)
                    filterAndShowProducts()
                }, {
                    view?.hideLoading()
                    view?.showErrorMessage()
                })
        )
    }

    override fun changeFavoriteState(productEntity: ProductEntity) {
        super.changeFavoriteState(productEntity)
        if (productEntity.isFavorite)
            return

        if (originalList.contains(productEntity)) {
            originalList.remove(productEntity)
        }

    }
}