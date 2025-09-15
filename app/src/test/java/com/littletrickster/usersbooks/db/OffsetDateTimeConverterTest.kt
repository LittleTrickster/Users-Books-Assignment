package com.littletrickster.usersbooks.db

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.OffsetDateTime

class OffsetDateTimeConverterTest {

    private val converter = OffsetDateTimeConverter()

    @Test
    fun toString_null_returnsNull() {
        assertNull(converter.toString(null))
    }

    @Test
    fun fromString_null_returnsNull() {
        assertNull(converter.fromString(null))
    }

    @Test
    fun roundTrip_preservesValue() {
        val now = OffsetDateTime.parse("2025-09-15T17:15:30+02:00")
        val str = converter.toString(now)
        val parsed = converter.fromString(str)
        assertEquals(now, parsed)
    }
}
