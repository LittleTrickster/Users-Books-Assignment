@file:OptIn(ExperimentalMaterial3Api::class)

package com.littletrickster.usersbooks.screens.books


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
fun BooksMain(
    booksList: List<ListIdTitleBooks>,
    onAllClick: (Int) -> Unit = {},
    onBookClick: (Book) -> Unit = {},
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Books")
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
                items(booksList) { (id, title, books) ->
                    BooksGroupItem(
                        title = title ?: "$id",
                        modifier = Modifier.fillMaxWidth(),
                        books = books,
                        onAllClick = { onAllClick(id) },
                        onBookClick = onBookClick,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun BooksMainPreview() {
    BooksMain(
        booksList = List(5) {
            ListIdTitleBooks(
                it,
                "$it Title",
                List(5) { Book(id = it, title = "Book $it") })
        }
    )
}


@Composable
private fun BooksGroupItem(
    modifier: Modifier = Modifier,
    title: String = "Title",
    books: List<Book>,
    onAllClick: () -> Unit = {},
    onBookClick: (Book) -> Unit = {}
) {
    Card(modifier, border = BorderStroke(1.dp, Color.Gray)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            BooksHeader(
                modifier = Modifier.padding(vertical = 5.dp),
                title = title,
                onAllClick = onAllClick
            )
            BookRows(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp),
                books = books,
                onBookClick = onBookClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BooksGroupItemPreview() {
    BooksGroupItem(
        modifier = Modifier.fillMaxWidth(),
        books = List(5) { Book(id = it, title = "Book $it") }
    )
}

@Preview(showBackground = true)
@Composable
private fun BooksGroupEmptyItemPreview() {
    BooksGroupItem(
        modifier = Modifier.fillMaxWidth(),
        books = emptyList()
    )
}


@Preview(showBackground = true)
@Composable
private fun BooksHeader(
    modifier: Modifier = Modifier,
    title: String = "Title",
    onAllClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(Modifier.weight(1f))
        OutlinedButton(
            onClick = onAllClick,
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
//            shape = RoundedCornerShape(8.dp)
        ) {
            Text("ALL")
        }
    }
}

@Composable
private fun BookRows(
    modifier: Modifier = Modifier,
    books: List<Book>,
    onBookClick: (book: Book) -> Unit = {}
) {
    if (books.isEmpty())
        Box(
            Modifier
                .fillMaxWidth()
                .height(165.dp), contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Empty",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 25.sp),
            )
        }
    else {
        LazyRow(
            modifier = modifier.defaultMinSize(minHeight = 165.dp),
            contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(books) { book ->
                BookSmallCard(
                    title = book.title,
                    onClick = { onBookClick(book) },
                    image = book.img
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 300)
@Composable
private fun BookRowsPreview() {
    BookRows(books = List(5) { Book(id = it, title = "Book $it") })
}


@Preview
@Composable
private fun BookSmallCard(
    modifier: Modifier = Modifier,
    title: String = "Title",
    image: String? = "",
    onClick: () -> Unit = {}
) {
    ElevatedCard(
        onClick = { onClick() },
        modifier = modifier.width(130.dp)
    ) {
        Column(Modifier.padding(10.dp)) {
            BookImage(
                modifier = Modifier.fillMaxWidth(),
                image = image
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}



