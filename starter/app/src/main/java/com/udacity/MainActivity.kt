package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.databinding.ContentMainBinding
import com.udacity.util.sendNotification
import com.udacity.viewmodel.DownloadClient
import com.udacity.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var viewBinding: ContentMainBinding
    private lateinit var fileName: String
    private var downloadStatus: Boolean = false

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory(application)
    }

    companion object {
        const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
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
                viewBinding.customButton.updateButtonState(ButtonState.Loading)
                viewModel.download()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.select_download_library),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.retrofitDownloadFile.observe(this, {
            if (it != null && it.isSuccessful) {
                viewBinding.customButton.updateButtonState(ButtonState.Completed)
                downloadStatus = it.isSuccessful
                fileName = getString(R.string.radio_button_retrofit_label)
                notificationManager.sendNotification(
                    fileName, downloadStatus,
                    applicationContext
                )
            }
        })

        viewModel.glideDownloadFile.observe(this, {
            if (it != null) {
                viewBinding.customButton.updateButtonState(ButtonState.Completed)
                downloadStatus = true
                fileName = getString(R.string.radio_button_glide_label)
                notificationManager.sendNotification(
                    fileName, downloadStatus,
                    applicationContext
                )
            }
        })

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

                .apply { setShowBadge(false) }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun isRadioButtonChecked(): Boolean {
        return radio_button_glide.isChecked
                || radio_button_dm.isChecked
                || radio_button_retrofit.isChecked
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            viewBinding.customButton.updateButtonState(ButtonState.Completed)
//            viewBinding.customButton.updateProgressAnimation()

            downloadStatus = id != -1L
            fileName = getString(R.string.radio_button_dm_label)

            notificationManager.sendNotification(
                fileName, downloadStatus,
                applicationContext
            )
        }
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
