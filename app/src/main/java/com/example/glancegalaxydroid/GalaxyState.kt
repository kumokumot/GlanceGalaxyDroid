package com.example.glancegalaxydroid

import kotlinx.serialization.Serializable

@Serializable
sealed interface GalaxyState {

    @Serializable
    object Loading : GalaxyState

    @Serializable
    data class Success(
        val displayString: String,

        val myPositionX: Int = 0
    ) : GalaxyState

    @Serializable
    data class Error(val message: String) : GalaxyState

    fun currentMyPositionX(): Int {
        return when (this) {
            is Loading -> 0
            is Success -> myPositionX
            is Error -> 0
        }
    }
}