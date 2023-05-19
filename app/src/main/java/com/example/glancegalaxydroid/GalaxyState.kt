package com.example.glancegalaxydroid

import kotlinx.serialization.Serializable

@Serializable
sealed interface GalaxyState {

    @Serializable
    object Standby : GalaxyState

    @Serializable
    data class Play(
        val displayString: String = "",

        val myPositionX: Int = 0,
        val enemyPositionList: List<EnemyPosition> = listOf(EnemyPosition.createInitialEnemyPosition())
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

@Serializable
class EnemyPosition private constructor(val x: Int, val y: Int) {

    companion object {
        fun createInitialEnemyPosition() =
            EnemyPosition((0..GalaxyGlanceAppWidget.FIELD_ROW_MAX_INDEX).random(), 0)
    }

    fun createNextFlameEnemyPosition(): EnemyPosition {
        val nextY = y + 1
        return if (nextY > GalaxyGlanceAppWidget.FIELD_COLUMN_MAX_INDEX) {
            EnemyPosition(
                x = (0..GalaxyGlanceAppWidget.FIELD_ROW_MAX_INDEX).random(),
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