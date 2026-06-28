package com.desbravando.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.desbravando.app.R // Certifique-se de que o R está importado corretamente
import com.desbravando.app.SavedItinerary
import com.desbravando.app.ui.theme.DarkBlue
import com.desbravando.app.ui.theme.DesbravandoTheme
import com.desbravando.app.ui.theme.Gray
import com.desbravando.app.ui.theme.Poppins
import com.desbravando.app.ui.theme.Purple
import com.desbravando.app.ui.theme.White

@Composable
fun PublicItineraryCard(
    itinerary: SavedItinerary,
    onClick: () -> Unit = {}
) {
    // 💡 Estado que controla se este card específico está favoritado
    var isFavorited by remember { mutableStateOf(true) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .width(140.dp)
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Column {
            Box {
                AsyncImage(
                    model = itinerary.imageUrl,
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
                        .padding(end = 10.dp)
                        .fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(color = White, shape = CircleShape)
                            .clickable { isFavorited = !isFavorited }
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isFavorited) R.drawable.ic_heart_regular else R.drawable.heart
                            ),
                            contentDescription = "Favoritar",
                            tint = Purple,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp)) {
                Text(
                    text = itinerary.title,
                    fontSize = 12.sp,
                    color = DarkBlue,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Text(
                    text = "${itinerary.locationsCount} locais",
                    fontFamily = Poppins,
                    fontSize = 11.sp,
                    color = Gray,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PublicItineraryCardPreview() {
    DesbravandoTheme {

            PublicItineraryCard(
                itinerary = SavedItinerary(
                    id = "preview_123",
                    title = "Praias de VV",
                    imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e",
                    locationsCount = 5
                ),
                onClick = {}
            )

    }
}