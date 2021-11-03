package com.udacity.viewmodel

import android.app.Application
import android.app.DownloadManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.udacity.MainActivity
import com.udacity.R
import com.udacity.network.RetrofitApi
import com.udacity.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

enum class DownloadClient {
    GLIDE,
    DM,
    RETROFIT
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _application = application

    private val retrofitApi: RetrofitApi = RetrofitClient.provideApi()

    private val downloadManager =
        application.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
    private val downloadRequest = {
        DownloadManager.Request(Uri.parse(MainActivity.URL))
            .setTitle(application.getString(R.string.app_name))
            .setDescription(application.getString(R.string.app_description))
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
    }
    private var downloadID: Long = 0
    private lateinit var downloadClient: DownloadClient

    // The internal MutableLiveData to store downloaded retrofit file
    private val _retrofitDownloadFile = MutableLiveData<Response<ResponseBody>>()

    // The external immutable LiveData for the downloaded retrofit file
    val retrofitDownloadFile: LiveData<Response<ResponseBody>>
        get() = _retrofitDownloadFile

    // The internal MutableLiveData to store downloaded glide image
    private val _glideDownloadFile = MutableLiveData<FutureTarget<Bitmap>>()

    // The external immutable LiveData for the downloaded glide image
    val glideDownloadFile: LiveData<FutureTarget<Bitmap>>
        get() = _glideDownloadFile


    fun download() {
        when (downloadClient) {
            DownloadClient.RETROFIT -> downloadWithRetrofit()
            DownloadClient.GLIDE -> downloadWithGlide()
            DownloadClient.DM -> downloadWithDownloadManager()
        }
    }

    fun setClient(client: DownloadClient) {
        downloadClient = client
    }

    private fun downloadWithRetrofit() {
        viewModelScope.launch {
            _retrofitDownloadFile.value = retrofitApi.downloadFile(MainActivity.URL)
        }
    }

    private fun downloadWithGlide() {
        viewModelScope.launch {
            _glideDownloadFile.value = Glide
                .with(_application.applicationContext)
                .asBitmap()
                .load("https://apod.nasa.gov/apod/image/2110/DarkMatter_KipacAmnh_1200.jpg")
                .submit()
        }
    }

    private fun downloadWithDownloadManager() {
        downloadManager.enqueue(downloadRequest())
    }

    /**
     * Factory for constructing MainViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}