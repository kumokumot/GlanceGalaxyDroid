package com.example.glancegalaxydroid

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object GalaxyStateDefinition : GlanceStateDefinition<GalaxyState> {

    private const val DATA_STORE_FILENAME = "galaxyState"
    private val Context.datastore by dataStore(DATA_STORE_FILENAME, GalaxyStateSerializer)

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<GalaxyState> {
        return context.datastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATA_STORE_FILENAME)
    }
}

object GalaxyStateSerializer : Serializer<GalaxyState> {
    override val defaultValue = GalaxyState.Standby

    override suspend fun readFrom(input: InputStream): GalaxyState = try {
        Json.decodeFromString(
            GalaxyState.serializer(),
            input.readBytes().decodeToString()
        )
    } catch (exception: SerializationException) {
        throw CorruptionException("Could not read sample state: ${exception.message}")
    }

    override suspend fun writeTo(t: GalaxyState, output: OutputStream) {
        output.use {
            it.write(
                Json.encodeToString(GalaxyState.serializer(), t).encodeToByteArray()
            )
        }
    }
}