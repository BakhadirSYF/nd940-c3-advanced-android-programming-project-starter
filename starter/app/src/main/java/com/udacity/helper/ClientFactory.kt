package com.udacity.helper

import android.app.Application
import android.app.DownloadManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.udacity.MainActivity
import com.udacity.R

class ClientFactory {


}

fun getClient(application: Application): DownloadManager {
    return application.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
}

fun getRequest(application: Application): DownloadManager.Request {
    return DownloadManager.Request(Uri.parse(MainActivity.URL))
        .setTitle(application.getString(R.string.app_name))
        .setDescription(application.getString(R.string.app_description))
        .setRequiresCharging(false)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)
}
