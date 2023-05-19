package com.example.glancegalaxydroid.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.glancegalaxydroid.GalaxyGlanceAppWidget
import com.example.glancegalaxydroid.GalaxyState
import com.example.glancegalaxydroid.GalaxyStateDefinition
import com.example.glancegalaxydroid.R

@Composable
fun StandbyWidgetScreen() {
    Column(
        modifier = GlanceModifier.fillMaxSize().background(
            imageProvider = ImageProvider(R.drawable.hoshizora59),
            contentScale = ContentScale.Crop
        )
    )
    {
        Box(
            modifier = GlanceModifier.defaultWeight().fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Galaxy Droid",
                style = TextStyle(color = ColorProvider(Color.White))
            )
        }
        Box(
            modifier = GlanceModifier.defaultWeight().fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = GlanceModifier.size(128.dp),
                provider = ImageProvider(
                    R.drawable.android_robot
                ),
                contentDescription = null,
                colorFilter = ColorFilter.tint(ColorProvider(Color.Cyan))
            )
        }
        Box(
            modifier = GlanceModifier.defaultWeight().fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = GlanceModifier.clickable(actionRunCallback<GameStartAction>()),
                text = "GAME START",
                style = TextStyle(color = ColorProvider(Color.White))
            )
        }
    }
}

class GameStartAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(
            context = context,
            definition = GalaxyStateDefinition, glanceId = glanceId,
            updateState = {
                GalaxyState.Play()
            }
        )
        GalaxyGlanceAppWidget().update(context, glanceId)
    }
}