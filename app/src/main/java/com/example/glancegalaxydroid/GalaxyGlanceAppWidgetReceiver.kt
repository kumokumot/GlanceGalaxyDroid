package com.example.glancegalaxydroid

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import com.example.glancegalaxydroid.GalaxyGlanceAppWidget.Companion.FIELD_COLUMN_MAX_INDEX
import com.example.glancegalaxydroid.GalaxyGlanceAppWidget.Companion.FIELD_ROW_MAX_INDEX
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
//                updateAppWidgetState(context, id) { pref ->
//                    updateEnemyPosition(pref)
//                }
                GalaxyGlanceAppWidget().update(context, id)
            }
        }
    }

    private fun updateEnemyPosition(pref: MutablePreferences) {
        val enemy1YKey = intPreferencesKey(GalaxyGlanceAppWidget.KEY_PREFERENCES_ENEMY_1_Y)
        val enemy1DefaultY = 0
        val enemy1XKey = intPreferencesKey(GalaxyGlanceAppWidget.KEY_PREFERENCES_ENEMY_1_X)

        val enemy2YKey = intPreferencesKey(GalaxyGlanceAppWidget.KEY_PREFERENCES_ENEMY_2_Y)
        val enemy2DefaultY = -3
        val enemy2XKey = intPreferencesKey(GalaxyGlanceAppWidget.KEY_PREFERENCES_ENEMY_2_X)

        val enemy3YKey = intPreferencesKey(GalaxyGlanceAppWidget.KEY_PREFERENCES_ENEMY_3_Y)
        val enemy3DefaultY = -6
        val enemy3XKey = intPreferencesKey(GalaxyGlanceAppWidget.KEY_PREFERENCES_ENEMY_3_X)

        val keyPairList = listOf(
            Triple(enemy1YKey, enemy1DefaultY, enemy1XKey),
            Triple(enemy2YKey, enemy2DefaultY, enemy2XKey),
            Triple(enemy3YKey, enemy3DefaultY, enemy3XKey)
        )

        keyPairList.forEach {
            val enemyYKey = it.first
            val enemyYDefault = it.second
            val enemyXKey = it.third

            val currentEnemyY = pref[enemyYKey] ?: enemyYDefault
            if (currentEnemyY < FIELD_COLUMN_MAX_INDEX) { // リストの末尾より小さかったら
                pref[enemyYKey] = currentEnemyY + 1
            } else {
                pref[enemyYKey] = 0

                // Y座標が0になったとき（敵が出現したとき）のみランダムにX座標を変更する
                pref[enemyXKey] = (0..FIELD_ROW_MAX_INDEX).random()
            }
        }
    }
}