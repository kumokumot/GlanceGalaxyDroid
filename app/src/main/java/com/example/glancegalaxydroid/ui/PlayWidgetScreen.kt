package com.example.glancegalaxydroid.ui

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
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.glancegalaxydroid.EnemyPosition
import com.example.glancegalaxydroid.GalaxyApplication
import com.example.glancegalaxydroid.GalaxyGlanceAppWidget
import com.example.glancegalaxydroid.GalaxyGlanceAppWidgetReceiver
import com.example.glancegalaxydroid.GalaxyState
import com.example.glancegalaxydroid.GalaxyStateDefinition
import com.example.glancegalaxydroid.R
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// フィールドのサイズ（Yの長さ）
const val PLAY_FIELD_COLUMN_SIZE = 9
const val PLAY_FIELD_COLUMN_MAX_INDEX = PLAY_FIELD_COLUMN_SIZE - 1

// フィールドのサイズ（Xの長さ）
const val PLAY_FIELD_ROW_SIZE = 10
const val PLAY_FIELD_ROW_MAX_INDEX = PLAY_FIELD_ROW_SIZE - 1

@Composable
fun PlayWidgetScreenRoot(galaxyState: GalaxyState.Play) {
    val playScore = galaxyState.playScore
    val stock = galaxyState.stock
    val myX = galaxyState.myPositionX
    val enemyPositionList = galaxyState.enemyPositionList
    val isGameOver = galaxyState.isGameOver
    PlayWidgetScreen(playScore, stock, enemyPositionList, myX, isGameOver)
}

@Composable
fun PlayWidgetScreen(
    playScore: Int,
    stock: Int,
    enemyPositionList: List<EnemyPosition>,
    myX: Int,
    isGameOver: Boolean
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
            val fieldRowList: List<MutableList<Int>> = List(PLAY_FIELD_COLUMN_SIZE) {
                MutableList(
                    PLAY_FIELD_ROW_SIZE
                ) { 0 }
            }
                .apply {
                    // 敵位置の適用
                    enemyPositionList.forEach {
                        this[it.y][it.x] = 2
                    }

                    // 自位置の適用
                    this[PLAY_FIELD_COLUMN_MAX_INDEX][myX] = 1
                }
            fieldRowList.forEach { row ->
                Row(
                    modifier = GlanceModifier.defaultWeight()
                ) {
                    row.forEach {
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
                                if (!isGameOver) {
                                    Image(
                                        provider = ImageProvider(
                                            R.drawable.jetdroid
                                        ),
                                        contentDescription = null,
                                    )
                                }
                            } else {
                                Box(modifier = GlanceModifier.width(108.dp)) {
                                }
                            }
                        }

                    }
                }
            }
            // 敵機と自機が衝突した場合
            if (!isGameOver && enemyPositionList.any { it.y == PLAY_FIELD_COLUMN_MAX_INDEX && it.x == myX }) {
                LaunchedEffect(true) {
                    updateAppWidgetState(
                        context = context,
                        definition = GalaxyStateDefinition,
                        glanceId = GlanceAppWidgetManager(context).getGlanceIds(
                            GalaxyGlanceAppWidget::class.java
                        ).first(),
                        updateState = {
                            val nextState =
                                (it as? GalaxyState.Play)?.copy(
                                    stock = it.stock - 1,
                                ) ?: it
                            nextState
                        }
                    )
                }
                Image(
                    provider = ImageProvider(
                        R.drawable.explosion
                    ),
                    contentDescription = null,
                )
            }

            // プレイヤー操作UI
            if (!isGameOver) {
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
        StatusBar(playScore, stock)

        if (isGameOver) {
            Box(GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column() {
                    Text(text = "Game Over", style = TextStyle(color = ColorProvider(Color.White)))
                    Text(
                        text = "Score: $playScore",
                        style = TextStyle(color = ColorProvider(Color.White))
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBar(playScore: Int, stock: Int) {
    Row(modifier = GlanceModifier.fillMaxWidth()) {
        Box(
            contentAlignment = Alignment.CenterStart
        ) {
            RefreshButton()
        }
        Spacer(modifier = GlanceModifier.defaultWeight())
        Box(
            modifier = GlanceModifier.defaultWeight(),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                PlayScore(playScore)
                Row(modifier = GlanceModifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                    repeat((1..stock).count()) {
                        Image(
                            provider = ImageProvider(
                                R.drawable.jetdroid
                            ),
                            contentDescription = null,
                            modifier = GlanceModifier.size(16.dp)
                        )
                    }
                }
            }
        }
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

@Composable
private fun PlayScore(playScore: Int) {
    Text(
        text = "Score: $playScore",
        style = TextStyle(color = ColorProvider(Color.White)),
        modifier = GlanceModifier.padding(8.dp)
    )
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
                    (it as? GalaxyState.Play)?.copy(
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
                    (it as? GalaxyState.Play)?.copy(
                        myPositionX = moveRight(it)
                    ) ?: it
                nextState
            }
        )
        GalaxyGlanceAppWidget().update(context, glanceId)
    }

    private fun moveRight(it: GalaxyState): Int {
        val currentX = it.currentMyPositionX()
        return if (currentX < PLAY_FIELD_ROW_MAX_INDEX) currentX + 1 else currentX
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
                GalaxyState.Standby
            }
        )
        GalaxyGlanceAppWidget().update(context, glanceId)
    }
}