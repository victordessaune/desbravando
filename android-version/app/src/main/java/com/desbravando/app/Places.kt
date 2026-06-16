package com.desbravando.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.desbravando.app.ui.theme.DarkBlue
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext
import com.desbravando.app.ui.components.BottomBarWithNavigation
import androidx.compose.foundation.layout.padding

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

                    place?.let {
                        PlaceDetailsScreen(place = it)
                    }

                }
            }
        }
    }
}
@Composable
fun PlaceDetailsScreen(
    place: PlaceInfo
) {
    
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            BottomBarWithNavigation(
                selectedRoute = "explore",
                context = context
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(OffWhite)
        ) {

            item { HeaderSection(place) }
            item { DescriptionPlace(place) }
            /*item { AddressSection(place) }
            item { InformationSection(place) }
            item { ImageSection(place) }*/

        }
    }
}
@Composable
fun HeaderSection(
    place: PlaceInfo
) {

    Column(

    ){

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
    SectionCard(
    title = "Sobre o local",
    icon = R.drawable.ic_info

    ) {
        Text(
            text = place.bio,
            textAlign = TextAlign.Justify,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun AddressSection(
    place: PlaceInfo
){
    SectionCard(
        title = "Endereço",
        icon = R.drawable.ic_map_pin

    ) {
        Text(
            text = place.address,
            textAlign = TextAlign.Justify,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight(500)
        )
    }
}

@Composable
fun InformationSection(
    place: PlaceInfo
){
    SectionCard(
        title = "Infraestrutura",
        icon = R.drawable.ic_building
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            userScrollEnabled = false,
            modifier = Modifier
                .heightIn(max = 500.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(place.infrastructure){ item ->
                InfrastructureItem(
                    title = item
                )
            }
        }

    }
}

@Composable
fun ImageSection(
    place: PlaceInfo
){
    SectionCard(
        title = "Galeria de fotos",
        icon = R.drawable.ic_camera
    ){

    }

}

@Composable
fun ScheduleSection(
    place: PlaceInfo
){
    SectionCard(
        title = "Horário de Funcionamento",
        icon = R.drawable.ic_clock
    ){

    }
}

//Função para definifr o item apresentação na seção de infraestrutura
@Composable
fun InfrastructureItem(
    title: String
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = OffWhite
        )
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(
                    id = getInfrastructureIcon(title)
                ),
                contentDescription = null,
                tint = Blue,
                modifier = Modifier.size(18.dp)
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight(500)
            )
        }
    }
}

@DrawableRes
fun getInfrastructureIcon(name: String): Int {
    return when(name) {
        "Estacionamento" -> R.drawable.ic_parking
        "Segurança" -> R.drawable.ic_shield
        "Familiar" -> R.drawable.ic_family
        "Lanchonetes" -> R.drawable.ic_utensils
        "Toaletes" -> R.drawable.ic_restroom
        "Climatizado" -> R.drawable.ic_snowflake
        "Playground" -> R.drawable.ic_playground
        "Monitoramento" -> R.drawable.ic_video
        "Iluminação" -> R.drawable.ic_light
        "Bicicletas" -> R.drawable.ic_bicycle
        "Na Sombra" -> R.drawable.ic_tree
        "Acessível" -> R.drawable.ic_wheelchair
        else -> R.drawable.ic_info
    }
}

//Função para o modelo do card
@Composable
fun SectionCard(
    title: String,
    icon: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = White
            )
        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = Purple,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                content()
            }
        }
    }
}


data class PlaceInfo(
    val id: String = "",
    val name: String = "",
    val city: String = "",
    val tags: String = "",
    val bio: String = "",
    val address: String = "",
    val infrastructure: List<String> = emptyList()
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
                tags = "GatroBar",
                bio = "O Coco Bambu é uma renomada rede de restaurantes brasileira especializada em frutos do mar, conhecida por pratos fartos, cardápio variado e ambiente sofisticado, mas acessível. Fundada em 2001, destaca-se pelo Camarão Internacional, adegas climatizadas, áreas para eventos e atendimento de alta qualidade.",
                address = "Rua Vasco Coutinho, s/n, Prainha, na cidade de Vila Velha, ES",
                infrastructure = listOf(
                    "Estacionamento",
                    "Segurança",
                    "Toaletes",
                    "Na Sombra",
                    "Bicicletas"
                )
            )
        )
    }
}