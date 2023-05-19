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
        val galaxyState = currentState<GalaxyState>()
        val myX = galaxyState.currentMyPositionX()
        val enemyPositionList = galaxyState.currentEnemyPositionList()

        ShootingWidgetUi(enemyPositionList, myX)
    }

    @Composable
    fun ShootingWidgetUi(
        enemyPositionList: List<EnemyPosition>,
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

        Box {
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
                        enemyPositionList.forEach {
                            this[it.y][it.x] = 2
                        }

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
            RefreshButton()
        }
    }

    @Composable
    private fun RefreshButton() {
        Image(
            provider = ImageProvider(
                R.drawable.baseline_refresh_24
            ),
            contentDescription = null,
            modifier = GlanceModifier.padding(8.dp).size(36.dp)
                .clickable(actionRunCallback<RefreshAction>())
        )
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
                val nextState =
                    (it as? GalaxyState.Success)?.copy(
                        myPositionX = moveLeft(it)
                    ) ?: it
                nextState
            }
        )
    }

    private fun moveLeft(it: GalaxyState): Int {
        val currentX = it.currentMyPositionX()
        return if (currentX > 0) currentX - 1 else currentX
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
                val nextState =
                    (it as? GalaxyState.Success)?.copy(
                        myPositionX = moveRight(it)
                    ) ?: it
                nextState
            }
        )
        GalaxyGlanceAppWidget().update(context, glanceId)
    }

    private fun moveRight(it: GalaxyState): Int {
        val currentX = it.currentMyPositionX()
        return if (currentX < GalaxyGlanceAppWidget.FIELD_ROW_MAX_INDEX) currentX + 1 else currentX
    }
}

class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(
            context = context,
            definition = GalaxyStateDefinition, glanceId = glanceId,
            updateState = {
                GalaxyState.Success()
            }
        )
        GalaxyGlanceAppWidget().update(context, glanceId)
    }
}