package com.udacity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        if (intent.hasExtra(INTENT_EXTRA)) {
            val bundle = intent.getBundleExtra(INTENT_EXTRA)
            fileName.text = bundle?.getString(FILE_NAME) ?: "-"

            val statusText = when (bundle?.getBoolean(STATUS) ?: false) {
                true -> "Success"
                else -> "Fail"
            }

            status.text = statusText
        }
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
