package com.example.glancegalaxydroid

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import com.example.glancegalaxydroid.ui.PlayWidgetScreenRoot
import com.example.glancegalaxydroid.ui.StandbyWidgetScreen

class GalaxyGlanceAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { GalaxyContent() }
    }

    override val stateDefinition = GalaxyStateDefinition

    @Composable
    fun GalaxyContent() {
        when (val galaxyState = currentState<GalaxyState>()) {
            is GalaxyState.Standby -> {
                StandbyWidgetScreen()
            }

            is GalaxyState.Play -> {
                PlayWidgetScreenRoot(galaxyState)
            }
        }
    }
}