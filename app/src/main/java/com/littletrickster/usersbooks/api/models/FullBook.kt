package com.littletrickster.usersbooks.api.models

import com.littletrickster.usersbooks.api.OffsetDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class FullBook(
    val id: Int,
    @SerialName("list_id")
    val listId: Int,
    val isbn: String,
    @SerialName("publication_date")
    @Serializable(with = OffsetDateTimeSerializer::class)
    val publicationDate: OffsetDateTime,
    val author: String,
    val title: String,
    val img: String,
    val description: String
)