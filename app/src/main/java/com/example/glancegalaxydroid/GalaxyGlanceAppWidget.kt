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
                PlayWidgetScreenRoot(galaxyState)
            }
        }
    }
}