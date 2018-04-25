package com.kotensky.testevo.model.network

import com.kotensky.testevo.model.entities.ProductsResponseEntity
import io.reactivex.Single
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface ApiRequestService {

    @POST(NetworkUrls.REQUEST)
    fun getProducts(@Query("limit") limit: Int,
                    @Query("offset") offset: Int,
                    @Query("category") category: Int,
                    @Query("sort") sort : String?,
                    @Body body: RequestBody) : Single<Response<ProductsResponseEntity>>

    @GET
    @Streaming
    fun loadImageFile(@Url imageUrl: String): Single<Response<ResponseBody>>

}