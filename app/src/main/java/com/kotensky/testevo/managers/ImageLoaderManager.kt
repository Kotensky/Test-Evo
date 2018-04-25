package com.kotensky.testevo.managers

import android.os.Environment
import com.kotensky.testevo.model.network.ApiRequestService
import com.kotensky.testevo.model.room.dao.FavoritesDao
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import okio.Okio
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class ImageLoaderManager @Inject constructor(private val apiRequestService: ApiRequestService,
                                             private val favoritesDao: FavoritesDao) {

    private val appDirectory = "TestEvoFavorites"

    fun saveImage(productId: Long, imageUrl: String) {

        apiRequestService.loadImageFile(imageUrl)
                .flatMap { response ->
                    Single.just(handleResponse(productId.toString(), response))
                }
                .filter {
                    !it.isEmpty()
                }
                .flatMapSingle {
                    Single.just(favoritesDao.updateNewsImagePath(productId, it))
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                },{
                    it.printStackTrace()
                })
    }

    private fun handleResponse(fileName: String, response: Response<ResponseBody>): String {
        val imageRoot = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appDirectory)

        if (response.isSuccessful && response.body() != null) {

            val file = File(imageRoot, "$fileName.jpg")
            try {
                if (!imageRoot.exists()) {
                    imageRoot.mkdir()
                }

                file.createNewFile()
                val sink = Okio.buffer(Okio.sink(file))
                sink.writeAll(response.body()!!.source())
                sink.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return file.canonicalPath
        } else {
            return ""
        }
    }

}