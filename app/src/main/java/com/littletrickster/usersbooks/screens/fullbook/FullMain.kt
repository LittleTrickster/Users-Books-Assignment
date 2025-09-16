@file:OptIn(ExperimentalMaterial3Api::class)

package com.littletrickster.usersbooks.screens.fullbook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littletrickster.usersbooks.screens.BookImage

@Composable
fun FullBookMain(
    modifier: Modifier = Modifier,
    title: String = "",
    author: String = "",
    isbn: String = "",
    date: String = "",
    description: String = "",
    bookListStatus: String = "",
    image: String? = "",
    onBack: () -> Unit = {},
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "backIcon")
                    }
                },


                modifier = Modifier.fillMaxWidth(),

                )
        }

    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            isRefreshing = isRefreshing,
            onRefresh = onRefresh
        ) {
            FullBookColumn(
                modifier = Modifier.fillMaxSize(),
                title = title,
                author = author,
                isbn = isbn,
                date = date,
                bookListStatus = bookListStatus,
                description = description,
                image = image
            )
        }
    }
}

@Preview
@Composable
private fun FullBookPreview() {
    FullBookMain(
        title = "Test Title",
        author = "Author",
        isbn = "isbn",
        date = "2025-09-14",
        bookListStatus = "Read",
        description = "description"
    )
}


@Composable
private fun FullBookColumn(
    modifier: Modifier = Modifier,
    title: String = "",
    author: String = "",
    isbn: String = "",
    date: String = "",
    bookListStatus: String = "",
    description: String = "",
    image: String? = ""
) {


    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(
                top = 10.dp,
                start = 10.dp,
                end = 10.dp,
                bottom = 0.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        BookImage(
            Modifier
                .fillMaxWidth(0.5f),
            image = image
        )
        Spacer(Modifier.height(10.dp))
        Column(
            Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                ),

                )
            Text(
                text = "Author: $author",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                )
            Text(
                text = "Status: $bookListStatus",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                )
            Text(
                text = "ISBN: $isbn",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),

                )
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),

                )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                fontWeight = FontWeight.Bold
            )
        }
    }

}

@Preview(widthDp = 400, heightDp = 800, showBackground = true)
@Composable
private fun FullBookColumnPreview() {
    FullBookColumn(
        title = "Test Title",
        author = "Author",
        isbn = "isbn",
        date = "2025-09-14",
        bookListStatus = "Read",
        description = "description"
    )
}
