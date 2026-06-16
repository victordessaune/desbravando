package com.desbravando.app.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.desbravando.app.FavoriteLocation
import com.desbravando.app.FavoritesRepository
import com.desbravando.app.R
import com.desbravando.app.ui.theme.DarkBlue
import com.desbravando.app.ui.theme.DesbravandoTheme
import com.desbravando.app.ui.theme.Gray
import com.desbravando.app.ui.theme.Poppins
import com.desbravando.app.ui.theme.Purple
import com.desbravando.app.ui.theme.White

@Composable
fun FavoriteWideCard(
    favorite: FavoriteLocation,
    onRemove: () -> Unit = {}
) {
    var isFavorited by remember { mutableStateOf(false) }
    LaunchedEffect(favorite.id) {
        FavoritesRepository.isFavorited(favorite.id) { isFavorited = it }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .width(165.dp),
    ) {
        Box {
            AsyncImage(
                model = favorite.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(end = 5.dp)
                    .fillMaxWidth(),

                ){
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(color = White, shape = CircleShape)
                        .clickable {
                            isFavorited = !isFavorited

                            FavoritesRepository.toggleFavorite(
                                locationId = favorite.id,
                                locationData = mapOf(
                                    "imageUrl" to favorite.imageUrl,
                                    "name" to favorite.name,
                                    "city" to favorite.city,
                                    "tags" to favorite.tags
                                )
                            )
                            if (!isFavorited) {
                                onRemove()
                            }
                        }
                )
                {
                    Icon(
                        painter = painterResource(id = if (isFavorited) R.drawable.heart
                        else R.drawable.ic_heart_regular),
                        contentDescription = null,
                        tint = Purple,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.Center)

                    )

                }

            }
        }


        Column(modifier = Modifier.padding(2.dp)) {
            Text(
                text = favorite.name,
                fontSize = 12.sp,
                color = DarkBlue,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = favorite.city,
                fontFamily = Poppins,
                fontSize = 11.sp,
                color = Gray,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun FavoriteWidePreview() {
    DesbravandoTheme {
        val mockFavorite = FavoriteLocation(
            id = "1",
            name = "Praia do Forte",
            city = "Cabo Frio",
            imageUrl = "https://via.placeholder.com/150",
            tags = listOf("Praia", "Natureza")
        )

        FavoriteWideCard(
            favorite = mockFavorite,
            onRemove = {}
        )
    }
}