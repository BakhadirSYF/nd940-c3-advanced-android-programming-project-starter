package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.udacity.databinding.ActivityMainBinding
import com.udacity.databinding.ContentMainBinding
import com.udacity.viewmodel.DownloadClient
import com.udacity.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.Dispatchers


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var viewBinding: ContentMainBinding

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        viewBinding = mainBinding.contentMain

        viewBinding.customButton.setOnClickListener {

            if (isRadioButtonChecked()) {
                viewBinding.customButton.startProgressAnimation()
                viewModel.download()
            } else {
                Toast.makeText(this, getString(R.string.select_download_library), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.retrofitDownloadFile.observe(this, Observer {
            if(it != null && it.isSuccessful) {
                viewBinding.customButton.updateProgressAnimation()
            }
        })

        viewModel.glideDownloadFile.observe(this, Observer {
            if(it != null) {
                viewBinding.customButton.updateProgressAnimation()
            }
        })
    }

    private fun isRadioButtonChecked(): Boolean {
        return radio_button_glide.isChecked
                || radio_button_dm.isChecked
                || radio_button_retrofit.isChecked
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            viewBinding.customButton.updateProgressAnimation()
            Toast.makeText(context, "id = $id", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.getId()) {
                R.id.radio_button_glide ->
                    if (checked) {
                        viewModel.setClient(DownloadClient.GLIDE)
                    }
                R.id.radio_button_dm ->
                    if (checked) {
                        viewModel.setClient(DownloadClient.DM)
                    }
                R.id.radio_button_retrofit ->
                    if (checked) {
                        viewModel.setClient(DownloadClient.RETROFIT)
                    }
            }
        }
    }

}
