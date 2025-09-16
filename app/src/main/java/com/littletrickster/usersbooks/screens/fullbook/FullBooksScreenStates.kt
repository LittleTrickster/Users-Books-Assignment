package com.littletrickster.usersbooks.screens.fullbook

import com.littletrickster.usersbooks.db.models.FullBookWithListTitle


sealed class FullBookScreenAction {
    data object Refresh : FullBookScreenAction()
    data object Back : FullBookScreenAction()

}


sealed class FullBookScreenEffect {
    data class Error(val string: String) : FullBookScreenEffect()
    data object Back : FullBookScreenEffect()
}


data class FullBookScreenState(
    val isLoading: Boolean = false,
    val fullBookWithListTitle: FullBookWithListTitle? = null,
    val refreshTimes: Int = 0
)