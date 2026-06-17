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
import androidx.compose.ui.res.stringResource
import coil.util.CoilUtils.result
import android.util.Log
import androidx.compose.foundation.lazy.LazyRow
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable

class Places : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val placeId =
            intent.getStringExtra("PLACE_ID") ?: ""

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

                            //Busca documento do Firestore, converte para PlaceInfo e salva em place
                            //Além disso mostra mensagem de erro
                            try {

                                place = document.toObject(
                                    PlaceInfo::class.java
                                )

                            } catch (e: Exception) {

                                Log.e("PLACES", getString(R.string.error_convert_document), e)
                            }
                        }
                        .addOnFailureListener { e ->

                            Log.e("PLACES", getString(R.string.error_firestore), e)
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
    val activity = LocalContext.current as? ComponentActivity

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
            item { HeaderSection(
                    place = place,
                    onBackClick = {
                        activity?.finish()
                    }
                )
            }
            item { DescriptionPlace(place) }
            item { AddressSection(place) }
            item { InfrastructureSection(place) }
            item { ImageSection(place) }
            item {ScheduleSection(place) }
            item { ServicesSection(place) }
            item { PriceSection(place) }
            //item { InformationSection(place) }

        }
    }
}
@Composable
fun HeaderSection(
    place: PlaceInfo,
    onBackClick: () -> Unit
) {
    Column(
    ){
        Box(){
            AsyncImage(
                model = place.cover,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = stringResource(R.string.title_return),
                tint = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .size(28.dp)
                    .align(Alignment.TopStart)
                    .clickable {
                        onBackClick()
                    }
            )

        }

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
                    text = place.tags.joinToString(", "),
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
    title = stringResource(R.string.about_place),
    icon = R.drawable.ic_info

    ) {
        Text(
            text = place.description,
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
    val address =
        "${place.street}, ${place.numero}, ${place.neighborhood}, ${place.city} - ${place.uf} (CEP: ${place.cep})"
    SectionCard(
        title = stringResource(R.string.address),
        icon = R.drawable.ic_map_pin

    ) {
        Text(
            text = address,
            textAlign = TextAlign.Justify,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight(500)
        )
    }
}

@Composable
fun InfrastructureSection(
    place: PlaceInfo
){
    SectionCard(
        title = stringResource(R.string.infrastructure),
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
        title = stringResource(R.string.gallery),
        icon = R.drawable.ic_camera
    ){
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(place.images) { imageUrl ->

                Card(
                    shape = RoundedCornerShape(12.dp)
                ) {

                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .width(220.dp)
                            .height(140.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

    }

}

@Composable
fun ScheduleSection(
    place: PlaceInfo
){
    val dayOrganized = listOf(
        "Segunda",
        "Terça",
        "Quarta",
        "Quinta",
        "Sexta",
        "Sábado",
        "Domingo",
        "Feriado"
    )

    SectionCard(
        title = stringResource(R.string.schedules),
        icon = R.drawable.ic_clock
    ){
        dayOrganized.forEach { day ->

            val horario = place.horarios[day]

            horario?.let {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = day,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )

                    if (it.fechado) {

                        Card(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Red.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.closed),
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight(500),
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 2.dp
                                )
                            )
                        }

                    } else {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                painter = painterResource(
                                    id = R.drawable.ic_clock
                                ),
                                contentDescription = null,
                                tint = Purple,
                                modifier = Modifier.size(14.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(4.dp)
                            )

                            Text(
                                text = "${it.abertura} - ${it.fechamento}",
                                color = Gray,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun ServicesSection(
    place: PlaceInfo
) {
    SectionCard(
        title = stringResource(R.string.services),
        icon = R.drawable.ic_bell

    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            place.services.forEach{ item ->
                ServicesItem(
                    title = item
                )
            }
        }

    }
}

@Composable
fun PriceSection(
    place: PlaceInfo
) {
    SectionCard(
        title = stringResource(R.string.price),
        icon = R.drawable.ic_price

    ) {
        if (place.price.valor.isNullOrBlank()) {

            Text(
                text = stringResource(R.string.text_free),
                color = Purple,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "R$",
                    color = Purple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.width(3.dp))

                Text(
                    text = place.price.valor,
                    color = Purple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(3.dp))

                Text(
                    text = "por",
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.width(3.dp))

                Text(
                    text = place.price.por,
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

/*@Composable
fun InformationSection(
    place: PlaceInfo
) {
    SectionCard(
        title = stringResource(R.string.informations),
        icon = R.drawable.ic_info_id

    ) {
        Text(
            text = place.informations,
            textAlign = TextAlign.Justify,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

    }
}*/

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

@Composable
fun ServicesItem(
    title: String
){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Blue.copy(alpha = 0.15f)
        )
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight(600)
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

data class Schedule(
    val abertura: String? = null,
    val fechamento: String? = null,
    val fechado: Boolean = false
)

data class Price(
    val por: String = "",
    val valor: String = "",
    val tipo: String = ""
)

data class PlaceInfo(
    val id: String = "",
    val name: String = "",
    val city: String = "",
    val street: String = "",
    val uf: String = "",
    val numero: String = "",
    val neighborhood: String = "",
    val cep: String = "",
    val tags: List<String> = emptyList(),
    val description: String = "",
    val infrastructure: List<String> = emptyList(),
    val horarios: Map<String, Schedule> = emptyMap(),
    val price: Price = Price(),
    val services: List<String> = emptyList(),
    val images: List<String> = emptyList(),
    val cover: String = "",
    //val informations: String = "",
)

@Preview(showBackground = true)
@Composable
fun PlacesPreview() {
    DesbravandoTheme {
        PlaceDetailsScreen(
            place = PlaceInfo(
                name = "Preview",
                city = "Preview"
            )
        )
    }
}