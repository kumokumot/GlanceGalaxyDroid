package com.example.glancegalaxydroid

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class GalaxyApplication : Application() {
    companion object {
        val applicationScope = CoroutineScope(Dispatchers.IO + Job())
    }
}