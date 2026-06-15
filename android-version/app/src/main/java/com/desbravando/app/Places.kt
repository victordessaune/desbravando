package com.desbravando.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desbravando.app.ui.theme.Blue
import com.desbravando.app.ui.theme.DesbravandoTheme
import com.desbravando.app.ui.theme.Gray
import com.desbravando.app.ui.theme.OffWhite
import com.desbravando.app.ui.theme.Purple
import com.desbravando.app.ui.theme.White
import com.google.firebase.firestore.FirebaseFirestore

class Places : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // futuramente virá da navegação
        val placeId = "123"

        setContent {

            DesbravandoTheme {

                var place by remember {
                    mutableStateOf<PlaceInfo?>(null)
                }

                LaunchedEffect(Unit) {

                    FirebaseFirestore
                        .getInstance()
                        .collection("locations")
                        .document(placeId)
                        .get()
                        .addOnSuccessListener { document ->

                            place = document.toObject(
                                PlaceInfo::class.java
                            )
                        }
                }

                if (place == null) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        CircularProgressIndicator()

                    }

                } else {

                    PlaceDetailsScreen(
                        place = place!!
                    )
                    DescriptionPlace(
                        place = place!!
                    )

                }
            }
        }
    }
}
@Composable
fun PlaceDetailsScreen(
    place: PlaceInfo
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhite)
    ) {

        item {
            HeaderSection(place)
        }
        item {
            DescriptionPlace(place)
        }
    }
}
@Composable
fun HeaderSection(
    place: PlaceInfo
) {

    Column {

        Image(
            painter = painterResource(id = R.drawable.cb3),
            contentDescription = "Foto do lugar",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.padding(start = 20.dp, top = 15.dp, end = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = place.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            painter = painterResource(
                                id = R.drawable.location_dot_solid_full
                            ),
                            contentDescription = "Location",
                            tint = Blue,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(5.dp)
                        )

                        Text(
                            text = place.city,
                            color = Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(20.dp)
                )

                Text(
                    text = place.tags,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = Purple,
                    modifier = Modifier
                        .width(90.dp)
                        .background(
                            color = Blue.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(
                            horizontal = 10.dp,
                            vertical = 2.dp
                        )
                )
                Spacer(
                    modifier = Modifier.height(20.dp)
                )
            }

        }
    }
}
@Composable
fun DescriptionPlace(
    place: PlaceInfo
){
    Column(

        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally

    ){

        Card(
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth(0.95f)
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = White
            )
        ){
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_info
                        ),
                        contentDescription = "Location",
                        tint = Blue,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    Text(
                        text = "Sobre o local",
                        fontSize = 15.sp,
                        fontWeight = FontWeight(600)
                    )

                }

            }


        }

    }

}

data class PlaceInfo(
    val id: String = "",
    val name: String = "",
    val city: String = "",
    val tags: String = ""
)

@Preview(showBackground = true)
@Composable
fun PlacesPreview() {
    DesbravandoTheme {
        PlaceDetailsScreen(
            place = PlaceInfo(
                id = "1",
                name = "Convento da Penha",
                city = "Vila Velha",
                tags = "GatroBar"
            )
        )
    }
}