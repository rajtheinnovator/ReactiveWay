package com.enpassio.reactiveway.instantsearch.view

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import com.enpassio.reactiveway.R


class InstantSearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instant_search)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // white background notification bar
        whiteNotificationBar(toolbar)
        val localSearchButton: Button = findViewById(R.id.btn_local_search)
        localSearchButton.setOnClickListener { view -> startActivity(Intent(this@InstantSearchActivity, LocalSearchActivity::class.java)) }

        val remoteSearchButton: Button = findViewById(R.id.btn_remote_search)
        remoteSearchButton.setOnClickListener { view -> startActivity(Intent(this@InstantSearchActivity, LocalSearchActivity::class.java)) }


    }

    private fun whiteNotificationBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.getSystemUiVisibility()
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.setSystemUiVisibility(flags)
            window.statusBarColor = Color.WHITE
        }
    }
}