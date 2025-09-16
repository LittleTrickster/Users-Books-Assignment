package com.littletrickster.usersbooks.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter


@Preview(showBackground = true, widthDp = 100)
@Composable
 fun BookImage(modifier: Modifier = Modifier, image: String? = "") {
    Box(
        modifier = modifier
            .aspectRatio(0.68F, true)
            .clip(RoundedCornerShape(5))
    ) {
        if (LocalInspectionMode.current) {
            PreviewImage(Modifier.matchParentSize())
        } else {
            NetworkImage(
                modifier = Modifier.matchParentSize(),
                model = image,
                contentDescription = null,
                contentScale = ContentScale.Fit,
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



@Composable
fun NetworkImage(
    modifier: Modifier = Modifier,
    model: Any?,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    val painter = rememberAsyncImagePainter(model)
    val state by painter.state.collectAsState()

    Box(
        modifier = modifier
    ) {
        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(placeholderColor)
            )
        }

        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.matchParentSize(),
            contentScale = contentScale
        )
    }
}

/*
* Helps from fast double clicking back button that pops back to white screen
* */
fun NavController.safePopBackStack(){
    if (this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        this.popBackStack()
    }
}