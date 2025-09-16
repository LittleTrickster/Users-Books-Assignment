@file:OptIn(ExperimentalTestApi::class)

package com.littletrickster.usersbooks

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.littletrickster.usersbooks.api.BooksApi
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(ApiModules::class)
@RunWith(AndroidJUnit4::class)
class FullSystemTestWithMockApi {

    @get:Rule(order = 0) val hilt = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createAndroidComposeRule<MainActivity>()


    @BindValue
    @JvmField
    val fakeApi: BooksApi = FakeBooksApi()
    @Before fun setUp() { hilt.inject() }


    @Test
    fun list_to_details_with_fake_api() {
        // Books screen shows data
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("Dune").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("The Hitchhikers Guide to the Galaxy").assertIsDisplayed()

        // Navigate to FullBook
        composeRule.onNodeWithText("Dune").performClick()

        // Wait until details load
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("Frank Herbert").fetchSemanticsNodes().isNotEmpty()
        }

        // Assert details
        composeRule.onNodeWithText("Frank Herbert").assertIsDisplayed()
        composeRule.onNodeWithText("0441013597").assertIsDisplayed()

        // Going back
        composeRule.onNodeWithContentDescription("backIcon").performClick()

        // Wait until we're back on list
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("Dune").fetchSemanticsNodes().isNotEmpty()
        }

        // Switch list tab
        composeRule.onAllNodesWithText("ALL")[0].performClick()

        // Wait until new list shows
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("The Hitchhikers Guide to the Galaxy")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onAllNodesWithText("The Hitchhikers Guide to the Galaxy")[0].performClick()
    }
}