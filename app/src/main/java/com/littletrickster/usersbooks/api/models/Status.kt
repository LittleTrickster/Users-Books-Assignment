package com.littletrickster.usersbooks.api.models

import kotlinx.serialization.Serializable

@Serializable
data class Status(
    val id: Int,
    val title: String
)