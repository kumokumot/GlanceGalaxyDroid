package com.example.glancegalaxydroid.activity

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.glance.layout.Column as Column1

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val context = LocalContext.current
                val widgetManager = AppWidgetManager.getInstance(context)
                // Get a list of our app widget providers to retrieve their info
                val widgetProviders =
                    widgetManager.getInstalledProvidersForPackage(context.packageName, null)
                Scaffold(
                    topBar = {
                        TopAppBar(title = {
                            Text(
                                text = "Glance Galaxy Droid",
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        })
                    }
                ) { paddingValues ->
                    Column(modifier = Modifier.padding(paddingValues)) {

                        widgetProviders.forEach {
                            WidgetInfoCard(it)
                        }


                    }


                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun WidgetInfoCard(providerInfo: AppWidgetProviderInfo) {
        val context = LocalContext.current
        val label = providerInfo.loadLabel(context.packageManager)
        val description = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            providerInfo.loadDescription(context).toString()
        } else {
            "Description not available"
        }
//        val preview = painterResource(id = providerInfo.previewImage)
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                providerInfo.pin(context)
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
//                Image(painter = preview, contentDescription = description)
            }
        }
    }


    /**
     * Extension method to request the launcher to pin the given AppWidgetProviderInfo
     *
     * Note: the optional success callback to retrieve if the widget was placed might be unreliable
     * depending on the default launcher implementation. Also, it does not callback if user cancels the
     * request.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun AppWidgetProviderInfo.pin(context: Context) {
        val successCallback = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, AppWidgetPinnedReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        AppWidgetManager.getInstance(context).requestPinAppWidget(provider, null, successCallback)
    }
}