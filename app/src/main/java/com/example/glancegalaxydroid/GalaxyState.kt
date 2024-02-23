package com.example.glancegalaxydroid

import com.example.glancegalaxydroid.ui.PLAY_FIELD_COLUMN_MAX_INDEX
import com.example.glancegalaxydroid.ui.PLAY_FIELD_ROW_MAX_INDEX
import kotlinx.serialization.Serializable

@Serializable
sealed interface GalaxyState {

    @Serializable
    object Standby : GalaxyState

    @Serializable
    data class Play(
        val playScore: Int = 0,
        val flameCount: Int = 0,
        val gameLevel: GameLevel = GameLevel.LEVEL_1,
        val myPositionX: Int = 0,
        val enemyPositionList: List<EnemyPosition> = listOf(EnemyPosition.createInitialEnemyPosition()),
        val stock: Int = 3, // 残機
        val isGameOver: Boolean = false,
    ) : GalaxyState

    fun currentMyPositionX(): Int {
        return when (this) {
            is Standby -> 0
            is Play -> myPositionX
        }
    }

    fun currentEnemyPositionList(): List<EnemyPosition> {
        return when (this) {
            is Standby -> emptyList()
            is Play -> enemyPositionList
        }
    }
}

private const val LEVEL_INCREMENTAL = 2

@Serializable
enum class GameLevel(val enemyCount: Int, private val levelUpThreshold: Int) {
    LEVEL_1(1, 3),
    LEVEL_2(2, LEVEL_1.levelUpThreshold + LEVEL_INCREMENTAL),
    LEVEL_3(3, LEVEL_2.levelUpThreshold + LEVEL_INCREMENTAL),
    LEVEL_4(4, LEVEL_3.levelUpThreshold + LEVEL_INCREMENTAL),
    LEVEL_5(5, LEVEL_4.levelUpThreshold + LEVEL_INCREMENTAL),
    LEVEL_6(6, LEVEL_5.levelUpThreshold + LEVEL_INCREMENTAL),
    LEVEL_7(7, LEVEL_6.levelUpThreshold + LEVEL_INCREMENTAL),
    LEVEL_8(8, LEVEL_7.levelUpThreshold + LEVEL_INCREMENTAL),
    LEVEL_9(9, LEVEL_8.levelUpThreshold + LEVEL_INCREMENTAL),
    LEVEL_10(10, LEVEL_9.levelUpThreshold + LEVEL_INCREMENTAL),
    LEVEL_11(11, LEVEL_10.levelUpThreshold + LEVEL_INCREMENTAL),
    LEVEL_MAX(12, Int.MAX_VALUE),
    ;

    private fun levelUp(): GameLevel {
        return values().let {
            val nextLevelInt = this.ordinal + 1
            if (nextLevelInt >= it.size) {
                it.last()
            } else {
                it[nextLevelInt]
            }
        }
    }

    fun nextFlameGameLevel(flameCount: Int): GameLevel {
        return if (flameCount < levelUpThreshold) {
            this
        } else {
            levelUp()
        }
    }
}

@Serializable
class EnemyPosition private constructor(val x: Int, val y: Int) {

    companion object {
        fun createInitialEnemyPosition() =
            EnemyPosition((0..PLAY_FIELD_ROW_MAX_INDEX).random(), 0)
    }

    fun createNextFlameEnemyPosition(): EnemyPosition {
        val nextY = y + 1
        return if (nextY > PLAY_FIELD_COLUMN_MAX_INDEX) {
            EnemyPosition(
                x = (0..PLAY_FIELD_ROW_MAX_INDEX).random(),
                y = 0,
            )
        } else {
            EnemyPosition(
                x = x,
                y = nextY,
            )
        }
    }
}