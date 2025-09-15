package com.littletrickster.usersbooks.api

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.OffsetDateTime

class OffsetDateTimeSerializerTest {

    @Serializable
    data class Wrapper(
        @Serializable(with = OffsetDateTimeSerializer::class)
        val ts: OffsetDateTime
    )

    private val json = Json { encodeDefaults = true }

    @Test
    fun serializeDeserialize_roundTrip() {
        val value = OffsetDateTime.parse("2025-09-15T17:15:30+02:00")
        val wrapper = Wrapper(value)
        val str = json.encodeToString(Wrapper.serializer(), wrapper)
        val decoded = json.decodeFromString(Wrapper.serializer(), str)
        assertEquals(wrapper, decoded)
    }

    @Test
    fun directSerializer_roundTrip() {
        val serializer: KSerializer<OffsetDateTime> = OffsetDateTimeSerializer
        val value = OffsetDateTime.parse("2025-09-15T17:15:30+02:00")
        val str = json.encodeToString(serializer, value)
        val decoded = json.decodeFromString(serializer, str)
        assertEquals(value, decoded)
    }
}
