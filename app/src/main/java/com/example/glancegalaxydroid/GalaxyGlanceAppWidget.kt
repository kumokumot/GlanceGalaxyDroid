package com.example.glancegalaxydroid

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.unit.ColorProvider
import com.example.glancegalaxydroid.ui.ShootingWidgetScreenRoot
import com.example.glancegalaxydroid.ui.StandbyWidgetScreen
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GalaxyGlanceAppWidget : GlanceAppWidget() {

    companion object {
        // フィールドのサイズ（Yの長さ）
        const val SIZE_FIELD_COLUMN = 9
        const val FIELD_COLUMN_MAX_INDEX = SIZE_FIELD_COLUMN - 1

        // フィールドのサイズ（Xの長さ）
        const val SIZE_FIELD_ROW = 10
        const val FIELD_ROW_MAX_INDEX = SIZE_FIELD_ROW - 1
    }

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
                ShootingWidgetScreenRoot(galaxyState)
            }
        }
    }
}