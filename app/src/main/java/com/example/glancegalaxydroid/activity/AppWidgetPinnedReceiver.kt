package com.example.glancegalaxydroid.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AppWidgetPinnedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(
            context,
            "ウィジェットがHOME画面に追加されました。",
            Toast.LENGTH_SHORT
        ).show()
    }
}