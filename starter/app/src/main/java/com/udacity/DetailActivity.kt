package com.udacity

import android.os.Bundle
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
            textView.text = bundle?.getString(FILE_NAME) ?: ""
        }
    }

    companion object {
        public const val INTENT_EXTRA = "intentExtra"
        public const val FILE_NAME = "fileName"
        public const val STATUS = "status"
    }

}
