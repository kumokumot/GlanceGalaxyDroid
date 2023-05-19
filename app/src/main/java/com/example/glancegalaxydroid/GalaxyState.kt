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
        val enemyPositionList: List<EnemyPosition> = listOf(EnemyPosition(0,0))
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
class EnemyPosition(val x: Int, val y: Int)