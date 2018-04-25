package com.kotensky.testevo.di.modules

import com.kotensky.testevo.model.network.ApiRequestService
import com.kotensky.testevo.model.network.NetworkUrls
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class NetworkModule {

    companion object {
        private const val GENERAL_TIMEOUT = 30L
        private const val CONNECT_TIMEOUT = GENERAL_TIMEOUT
        private const val WRITE_TIMEOUT = GENERAL_TIMEOUT
        private const val READ_TIMEOUT = GENERAL_TIMEOUT
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build()
    }

    @Provides
    @Singleton
    fun provideApiRequestService(retrofit: Retrofit): ApiRequestService {
        return retrofit.create<ApiRequestService>(ApiRequestService::class.java)
    }


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(NetworkUrls.MAIN_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }
}