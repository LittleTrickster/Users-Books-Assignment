package com.littletrickster.usersbooks

import kotlinx.coroutines.CoroutineDispatcher

interface AppDispatchers {
    val Main: CoroutineDispatcher
    val IO: CoroutineDispatcher
    val Default: CoroutineDispatcher
}