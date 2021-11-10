package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import com.udacity.util.cancelNotifications
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        clearNotification()

        if (intent.hasExtra(INTENT_EXTRA)) {
            val bundle = intent.getBundleExtra(INTENT_EXTRA)
            fileName.text = bundle?.getString(FILE_NAME) ?: "-"

            when (bundle?.getBoolean(STATUS) ?: false) {
                true -> updateForSuccess()
                else -> updateForFailure()
            }
        }

        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionCompleted(motionLayout: MotionLayout?, id: Int) {
                if (id == R.id.scaleEnd) {
                    motionLayout?.setTransition(R.id.moveStart, R.id.moveEnd)
                    motionLayout?.transitionToEnd()
                }
            }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                // stub
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                // stub
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                // stub
            }

        })

    }

    private fun clearNotification() {
        val notificationManager =
            ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.cancelNotifications()
    }

    private fun updateForFailure() {
        status.text = getString(R.string.status_failure)
        status.setTextColor(resources.getColor(R.color.red, null))
        statusIcon.setImageResource(R.drawable.ic_outline_cancel_24)
    }

    private fun updateForSuccess() {
        status.text = getString(R.string.status_success)
        status.setTextColor(resources.getColor(R.color.green, null))
        statusIcon.setImageResource(R.drawable.ic_outline_check_circle_24)
    }

    companion object {
        const val INTENT_EXTRA = "intentExtra"
        const val FILE_NAME = "fileName"
        const val STATUS = "status"
    }

    fun openMain(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}
