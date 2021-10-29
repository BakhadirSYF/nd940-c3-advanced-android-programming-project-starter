package com.udacity.viewmodel

import android.app.Application
import android.app.DownloadManager
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.udacity.MainActivity
import com.udacity.R
import com.udacity.helper.getClient
import com.udacity.helper.getRequest
import com.udacity.network.MyRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val client = getClient(application)
    private val repository = MyRepository(client)


    private val _application = application
    private var downloadID: Long = 0

    fun download() {
        repository.download(getRequest(_application))
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