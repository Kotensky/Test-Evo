package com.kotensky.testevo.application

import android.app.Application
import com.kotensky.testevo.di.components.ApplicationComponent
import com.kotensky.testevo.di.components.DaggerApplicationComponent
import com.kotensky.testevo.di.modules.ApplicationModule
import com.kotensky.testevo.managers.PermissionManager

class TestEvoApplication : Application() {

    companion object {
        lateinit var applicationComponent: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        initAppComponent()
    }

    private fun initAppComponent() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }

    fun getPermissionManager(): PermissionManager {
        return applicationComponent.permissionManager()
    }
}