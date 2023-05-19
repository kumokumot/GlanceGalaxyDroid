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
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GalaxyGlanceAppWidget : GlanceAppWidget() {

    companion object {
        // 敵機の位置
        const val KEY_PREFERENCES_ENEMY_1_X: String = "key_preferences_enemy_1_x"
        const val KEY_PREFERENCES_ENEMY_1_Y: String = "key_preferences_enemy_1_y"
        const val KEY_PREFERENCES_ENEMY_2_X: String = "key_preferences_enemy_2_x"
        const val KEY_PREFERENCES_ENEMY_2_Y: String = "key_preferences_enemy_2_y"
        const val KEY_PREFERENCES_ENEMY_3_X: String = "key_preferences_enemy_3_x"
        const val KEY_PREFERENCES_ENEMY_3_Y: String = "key_preferences_enemy_3_y"

        // 自機の位置
        const val KEY_PREFERENCES_MY_X: String = "key_preferences_my_x"

        // フィールドのサイズ（Yの長さ）
        const val SIZE_FIELD_COLUMN = 9
        const val FIELD_COLUMN_MAX_INDEX = SIZE_FIELD_COLUMN - 1

        // フィールドのサイズ（Xの長さ）
        const val SIZE_FIELD_ROW = 10
        const val FIELD_ROW_MAX_INDEX = SIZE_FIELD_ROW - 1
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { ShootingWidgetUiRoot() }
    }

    override val stateDefinition = GalaxyStateDefinition

    @Composable
    fun ShootingWidgetUiRoot() {
//        val prefs = currentState<Preferences>()
//
//        val enemy1X = prefs[intPreferencesKey(KEY_PREFERENCES_ENEMY_1_X)] ?: 0
//        val enemy1Y = prefs[intPreferencesKey(KEY_PREFERENCES_ENEMY_1_Y)] ?: 0
//        val enemy2X = prefs[intPreferencesKey(KEY_PREFERENCES_ENEMY_2_X)] ?: 0
//        val enemy2Y = prefs[intPreferencesKey(KEY_PREFERENCES_ENEMY_2_Y)] ?: 0
//        val enemy3X = prefs[intPreferencesKey(KEY_PREFERENCES_ENEMY_3_X)] ?: 0
//        val enemy3Y = prefs[intPreferencesKey(KEY_PREFERENCES_ENEMY_3_Y)] ?: 0
        val enemy1X = 0
        val enemy1Y = 0
        val enemy2X = 0
        val enemy2Y = 0
        val enemy3X = 0
        val enemy3Y = 0

//        val myX = prefs[intPreferencesKey(KEY_PREFERENCES_MY_X)] ?: 0

        val galaxyState = currentState<GalaxyState>()
        val myX = galaxyState.currentMyPositionX()

        ShootingWidgetUi(enemy1X, enemy1Y, enemy2X, enemy2Y, enemy3X, enemy3Y, myX)
    }

    @Composable
    fun ShootingWidgetUi(
        enemy1X: Int,
        enemy1Y: Int,
        enemy2X: Int,
        enemy2Y: Int,
        enemy3X: Int,
        enemy3Y: Int,
        myX: Int
    ) {
        val context = LocalContext.current
        val scope = GalaxyApplication.applicationScope

        LaunchedEffect(true) {
            while (true) {
                scope.coroutineContext.cancelChildren()
                scope.launch {
                    delay(300)
                    context.sendBroadcast(GalaxyGlanceAppWidgetReceiver.createUpdateIntent(context))
                }.join()
            }
        }

        Column(
            modifier = GlanceModifier.background(
                imageProvider = ImageProvider(R.drawable.hoshizora59),
                contentScale = ContentScale.Crop
            )
        ) {

            // 敵機 と自機
            val fieldRowList = List(SIZE_FIELD_COLUMN) { MutableList(SIZE_FIELD_ROW) { 0 } }
                .apply {
                    // 敵位置の適用
                    this[enemy1Y][enemy1X] = 2
                    if (enemy2Y > 0) this[enemy2Y][enemy2X] = 2
                    if (enemy3Y > 0) this[enemy3Y][enemy3X] = 2

                    // 自位置の適用
                    this[FIELD_COLUMN_MAX_INDEX][myX] = 1
                }
            fieldRowList.forEach {
                Row(
                    modifier = GlanceModifier.defaultWeight()
                ) {
                    it.forEach {
                        Box(modifier = GlanceModifier.defaultWeight())
                        {
                            if (it == 2) { // 暫定で2が敵機
                                Image(
                                    provider = ImageProvider(
                                        R.drawable.android_robot
                                    ),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(ColorProvider(Color.Cyan))
                                )
                            } else if (it == 1) { // 自機
                                Image(
                                    provider = ImageProvider(
                                        R.drawable.jetdroid
                                    ),
                                    contentDescription = null,
                                )
                            } else {
                                Box(modifier = GlanceModifier.width(108.dp)) {
                                }
                            }
                        }

                    }
                }
            }

            // プレイヤー操作UI
            Row(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = GlanceModifier.fillMaxWidth().padding(20.dp)
                    .background(Color.White.copy(alpha = 0.1f))
                    .height(88.dp)
            ) {
                // Left
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = GlanceModifier.defaultWeight()
                ) {
                    Image(
                        provider = ImageProvider(
                            R.drawable.baseline_arrow_left_24
                        ),
                        contentDescription = null,
                        modifier = GlanceModifier.size(80.dp)
                            .clickable(actionRunCallback<LeftAction>())
                    )
                }
                //  Right
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = GlanceModifier.defaultWeight()
                ) {
                    Image(
                        provider = ImageProvider(
                            R.drawable.baseline_arrow_right_24
                        ),
                        contentDescription = null,
                        modifier = GlanceModifier.size(80.dp)
                            .clickable(actionRunCallback<RightAction>())
                    )
                }
            }
        }
    }
}

class LeftAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(
            context = context,
            definition = GalaxyStateDefinition, glanceId = glanceId,
            updateState = {
                val currentX = it.currentMyPositionX()
                val nextX = if (currentX > 0) currentX - 1 else currentX
                GalaxyState.Success(displayString = "セットしたい文字列", nextX)
            }
        )
    }
}

class RightAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(
            context = context,
            definition = GalaxyStateDefinition, glanceId = glanceId,
            updateState = {
                val currentX = it.currentMyPositionX()
                val nextX =
                    if (currentX < GalaxyGlanceAppWidget.FIELD_ROW_MAX_INDEX) currentX + 1 else currentX
                GalaxyState.Success(displayString = "セットしたい文字列", nextX)
            }
        )
        GalaxyGlanceAppWidget().update(context, glanceId)
    }
}