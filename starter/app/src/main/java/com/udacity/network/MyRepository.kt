package com.udacity.network

import android.app.DownloadManager


class MyRepository(client: DownloadManager) {

    val _client = client

    fun download(request: DownloadManager.Request) {
        _client.enqueue(request)
    }

}