package com.example.glancegalaxydroid

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import kotlinx.coroutines.launch

class GalaxyGlanceAppWidgetReceiver : GlanceAppWidgetReceiver() {

    companion object {
        private const val ACTION_REQUEST_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE"

        fun createUpdateIntent(context: Context): Intent {
            return Intent(context, GalaxyGlanceAppWidgetReceiver::class.java).setAction(
                ACTION_REQUEST_UPDATE
            )
        }
    }

    override val glanceAppWidget: GlanceAppWidget
        get() = GalaxyGlanceAppWidget()

    private val scope = GalaxyApplication.applicationScope

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        update(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REQUEST_UPDATE) {
            update(context)
        }
    }

    private fun update(context: Context) {
        scope.launch {
            val ids =
                GlanceAppWidgetManager(context).getGlanceIds(GalaxyGlanceAppWidget::class.java)
            ids.forEach { id ->
                updateAppWidgetState(
                    context = context,
                    definition = GalaxyStateDefinition, glanceId = id,
                    updateState = {
                        val nextState =
                            (it as? GalaxyState.Play)?.copy(
                                enemyPositionList = updateEnemyPosition(it)
                            ) ?: it
                        nextState
                    }
                )
                GalaxyGlanceAppWidget().update(context, id)
            }
        }
    }

    private fun updateEnemyPosition(it: GalaxyState): List<EnemyPosition> {
        val nextEnemyPositionList = it.currentEnemyPositionList().map { enemyPosition ->
            enemyPosition.createNextFlameEnemyPosition()
        }
        return nextEnemyPositionList
    }
}
