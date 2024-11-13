package hu.bme.aut.szoftverarch.questly.graphics

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import hu.bme.aut.szoftverarch.questly.R

@Composable
fun StarRating(
    rating: Float,
    starSize: Dp = 24.dp,
    starColor: Color = Color.Yellow,
    emptyStarColor: Color = Color.Gray
) {
    val fullStars = rating.toInt()
    val partialStar = rating - fullStars
    val totalStars = 5

    Row {
        for (i in 0 until totalStars) {
            when {
                i < fullStars -> StarIcon(starSize, starColor, 1f)
                i == fullStars && partialStar > 0 -> StarIcon(starSize, starColor, partialStar)
                else -> StarIcon(starSize, emptyStarColor, 0f)
            }
        }
    }
}

@Composable
fun StarIcon(
    size: Dp,
    color: Color,
    fillRatio: Float
) {
    Box(modifier = Modifier.size(size)) {
        // Draw the empty star background
        Image(
            painter = painterResource(id = R.drawable.ic_star), // Replace with your star drawable
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(size),
            colorFilter = ColorFilter.tint(Color.Gray.copy(alpha = 0.2f))
        )

        // Draw the filled portion
        Image(
            painter = painterResource(id = R.drawable.ic_star), // Replace with your star drawable
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(size)
                .graphicsLayer {
                    clip = true
                    // Clip the filled portion based on fillRatio
                    shape = androidx.compose.ui.graphics.RectangleShape
                    alpha = fillRatio
                },
            colorFilter = ColorFilter.tint(color)
        )
    }
}
