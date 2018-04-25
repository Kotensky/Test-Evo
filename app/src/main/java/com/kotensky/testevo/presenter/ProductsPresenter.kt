package com.kotensky.testevo.presenter

import com.kotensky.testevo.managers.ImageLoaderManager
import com.kotensky.testevo.managers.SharedPreferencesManager
import com.kotensky.testevo.model.entities.ProductEntity
import com.kotensky.testevo.model.entities.ProductsCatalogEntity
import com.kotensky.testevo.model.network.ApiRequestService
import com.kotensky.testevo.model.network.NetworkUrls.Companion.BODY_FULL
import com.kotensky.testevo.model.network.NetworkUrls.Companion.CATEGORY
import com.kotensky.testevo.model.network.NetworkUrls.Companion.PAGE_LIMIT
import com.kotensky.testevo.model.room.dao.FavoritesDao
import com.kotensky.testevo.view.views.ProductListView
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject


open class ProductsPresenter @Inject constructor(private val apiRequestService: ApiRequestService,
                                                 private val sharedPreferencesManager: SharedPreferencesManager,
                                                 private val imageLoaderManager: ImageLoaderManager,
                                                 private val favoritesDao: FavoritesDao) : IProductsPresenter {


    override var view: ProductListView? = null
    override var sortList: ArrayList<String>? = null
    override var sort: String? = null
    override var filter: String? = null
        set(value) {
            field = value
            filterAndShowProducts()
        }

    protected val originalList: ArrayList<ProductEntity> = ArrayList()
    private val filteredList: ArrayList<ProductEntity> = ArrayList()

    protected val compositeDisposable: CompositeDisposable? = CompositeDisposable()

    private fun handleFavorites(catalog: ProductsCatalogEntity): Single<ProductsCatalogEntity> {
        return favoritesDao.getFavoriteIds()
                .doOnSuccess {

                    for (product in catalog.results!!) {
                        if (it.contains(product.id)) {
                            product.isFavorite = true
                        }
                    }
                }
                .map { catalog }
    }

    fun filterAndShowProducts() {
        filteredList.clear()

        if (filter.isNullOrEmpty()) {
            filteredList.addAll(originalList)
        } else {
            for (item in originalList) {
                if (item.name.toLowerCase().contains(filter!!.toLowerCase().trim())) {
                    filteredList.add(item)
                }
            }
        }
        view?.showProducts(filteredList)
    }

    override fun loadProducts(isPagination: Boolean) {
//        if (isPagination) view?.showPaginationLoading()
        val offset = if (isPagination) originalList.size else 0
        val requestBody = RequestBody.create(MediaType.parse("text/plain"), BODY_FULL)
        compositeDisposable?.add(
                apiRequestService.getProducts(PAGE_LIMIT, offset, CATEGORY, sort, requestBody)
                        .filter { response ->
                            response.isSuccessful && response.body()?.catalog?.results != null &&
                                    response.body()?.catalog?.sortList != null
                        }
                        .map { response ->
                            response.body()!!.catalog
                        }
                        .flatMapSingle { catalog ->
                            handleFavorites(catalog)
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            view?.hideLoading()

                            if (!isPagination) originalList.clear()
                            originalList.addAll(it.results!!)
                            filterAndShowProducts()
                            sortList = it.sortList
                        }, {
                            view?.hideLoading()
                            view?.showErrorMessage()
                        })
        )
    }

    override fun changeFavoriteState(productEntity: ProductEntity) {
        productEntity.isFavorite = !productEntity.isFavorite

        if (productEntity.isFavorite && productEntity.imagePath != null) {
            imageLoaderManager.saveImage(productEntity.id, productEntity.imagePath)
        }
        compositeDisposable?.add(

                Completable.fromAction {
                    if (productEntity.isFavorite)
                        favoritesDao.insertFavorite(productEntity)
                    else
                        favoritesDao.removeFromFavorite(productEntity)
                }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            view?.onFavoriteStateChanged(productEntity)
                        }, {
                            it.printStackTrace()
                        })
        )
    }

    override fun isGridViewType(): Boolean = sharedPreferencesManager.isGridViewType()

    override fun setIsGridViewType(gridViewType: Boolean) {
        sharedPreferencesManager.setIsGridViewType(gridViewType)
    }

    override fun destroy() {
        if (!(compositeDisposable?.isDisposed as Boolean)) {
            compositeDisposable.dispose()
        }
        view = null
    }

}