package com.kotensky.testevo.di.components

import android.content.Context
import com.kotensky.testevo.di.modules.ApplicationModule
import com.kotensky.testevo.di.modules.NetworkModule
import com.kotensky.testevo.managers.ImageLoaderManager
import com.kotensky.testevo.managers.PermissionManager
import com.kotensky.testevo.managers.SharedPreferencesManager
import com.kotensky.testevo.model.network.ApiRequestService
import com.kotensky.testevo.view.fragments.FavProductsListFragment
import com.kotensky.testevo.view.fragments.ProductsListFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(ApplicationModule::class), (NetworkModule::class)])
interface ApplicationComponent {

    fun context(): Context
    fun apiRequestService(): ApiRequestService
    fun sharedPreferencesManager(): SharedPreferencesManager
    fun permissionManager(): PermissionManager
    fun imageLoaderManager(): ImageLoaderManager

    fun inject(productsListFragment: ProductsListFragment)
    fun inject(favProductsListFragment: FavProductsListFragment)

}