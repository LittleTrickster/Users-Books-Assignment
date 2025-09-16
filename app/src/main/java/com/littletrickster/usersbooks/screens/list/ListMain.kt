@file:OptIn(ExperimentalMaterial3Api::class)

package com.littletrickster.usersbooks.screens.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littletrickster.usersbooks.db.models.Book
import com.littletrickster.usersbooks.screens.BookImage

@Composable
fun ListMain(
    title: String = "",
    list: List<Book>,
    onBack: () -> Unit = {},
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onBookClick: (Book) -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                actions = {
                    IconButton(onClick = {
                        onRefresh()
                    }) {
                        Icon(Icons.Default.Refresh, "refresh")
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
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)

            ) {
                items(list) { book ->
                    BookRowCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = book.title,
                        author = "",
                        image = book.img,
                        onClick = { onBookClick(book) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ListMainPreview() {
    ListMain(
        title = "Test Title",
        list = List(5) { Book(id = it, 1, "Title $it") },
    )
}

@Preview(widthDp = 400)
@Composable
private fun BookRowCard(
    modifier: Modifier = Modifier,
    title: String = "Title",
    author: String = "Author",
    image: String? = "",
    onClick: () -> Unit = {}
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { onClick() },
        colors = CardDefaults.elevatedCardColors(),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Row(Modifier.padding(10.dp)) {
            BookImage(Modifier.width(80.dp), image)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = author,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    maxLines = 1,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

