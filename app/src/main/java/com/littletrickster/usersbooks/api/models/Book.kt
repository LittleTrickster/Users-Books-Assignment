package com.littletrickster.usersbooks.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: Int,
    @SerialName("list_id")
    val listId: Int = 1,
    val title: String = "",
    val img: String = ""
)