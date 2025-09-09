@file:OptIn(ExperimentalMaterial3Api::class)

package com.littletrickster.usersbooks


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.littletrickster.usersbooks.api.models.Book

@Composable
fun BooksMain(
    typeAndBooks: List<Pair<Int, List<Book>>>,
    titleMap: Map<Int, String>,
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
                    Text("App")
                },
                modifier = Modifier.fillMaxWidth(),

                )
        }

    ) { innerPadding ->
        PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = onRefresh) {
            LazyColumn(
                Modifier.padding(innerPadding),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)

            ) {
                items(typeAndBooks) { (id, books) ->
                    BooksGroupItem(
                        title = titleMap[id] ?: "$id",
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
fun BooksMainPreview() {
    BooksMain(
        typeAndBooks = List(5) { it to List(5) { Book(id = it, title = "Book $it") } },
        titleMap = hashMapOf(0 to "Type 1")
    )
}


@Composable
fun BooksGroupItem(
    modifier: Modifier = Modifier,
    title: String = "Title",
    books: List<Book>,
    onAllClick: () -> Unit = {},
    onBookClick: (Book) -> Unit = {}
) {
    Card(modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            BooksHeader(title = title, onAllClick = onAllClick)
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
fun BooksGroupItemPreview() {
    BooksGroupItem(
        modifier = Modifier.fillMaxWidth(),
        books = List(5) { Book(id = it, title = "Book $it") }
    )
}

@Preview(showBackground = true)
@Composable
fun BooksGroupEmptyItemPreview() {
    BooksGroupItem(
        modifier = Modifier.fillMaxWidth(),
        books = emptyList()
    )
}


@Preview(showBackground = true)
@Composable
fun BooksHeader(
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
fun BookRows(
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
        //by requirements up to 5
        val booksLimited = remember(books) { books.take(5) }

        LazyRow(
            modifier = modifier.defaultMinSize(minHeight = 165.dp),
            contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(booksLimited) { book ->
                BookSmallCard(
                    title = book.title,
                    onClick = { onBookClick(book) })
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 300)
@Composable
fun BookRowsPreview() {
    BookRows(books = List(5) { Book(id = it, title = "Book $it") })
}


@Preview
@Composable
fun BookSmallCard(
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(5))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (LocalInspectionMode.current) {
                    PreviewImage(modifier.matchParentSize())
                } else {
                    AsyncImage(
                        modifier = Modifier.matchParentSize(),
                        model = image,
                        contentDescription = null,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 100, heightDp = 100)
@Composable
fun PreviewImage(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val size = size
        val pad = 5.dp.toPx()
        drawLine(
            color = Color.Gray,
            start = Offset(pad, pad),
            end = Offset(size.width - pad, size.height - pad),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = Color.Gray,
            start = Offset(size.width - pad, pad),
            end = Offset(pad, size.height - pad),
            strokeWidth = 2.dp.toPx()
        )
    }
}