package com.desbravando.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desbravando.app.ui.theme.Blue
import com.desbravando.app.ui.theme.DesbravandoTheme
import com.desbravando.app.ui.theme.OffWhite
import com.desbravando.app.ui.theme.Poppins
import com.desbravando.app.ui.theme.Purple
import com.desbravando.app.ui.theme.White
import com.google.android.gms.ads.nativead.NativeAd.Image
import com.google.firebase.firestore.FirebaseFirestore

class CatalogRestaurant : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            DesbravandoTheme {
                Catalog()
            }
        }
    }
}

@Composable
fun Catalog() {

    var restaurants by remember {
        mutableStateOf<List<Restaurants>>(emptyList())
    }

    LaunchedEffect(Unit) {

        findRestaurants { list ->
            restaurants = list
        }
    }

    var search by remember {mutableStateOf("")}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhite)
    ) {
        Column(
            modifier = Modifier.padding(
                top = 40.dp,
                start = 20.dp,
                end = 20.dp
            )
        ) {

            Row {

                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Voltar",
                    tint = Blue,
                    modifier = Modifier
                        .size(28.dp)
                        .border(
                            width = 1.dp,
                            color = Blue,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(5.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "Catálogo de Lugares",
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    color = Blue,
                    fontWeight = FontWeight(600)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))
        }
        
        HorizontalDivider(
            thickness = 1.dp
        )

        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 15.dp
            )
        ) {

            OutlinedTextField(
                value = search,
                onValueChange = {
                    search = it
                },
                placeholder = {
                    Text("Pesquisar restaurante")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Pesquisar"
                    )
                },
                modifier = Modifier.fillMaxWidth().background(color = White),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }
        
        HorizontalDivider(
            thickness = 1.dp
        )

        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            restaurants.forEach { restaurant ->

                Text(
                    text = restaurant.name
                )

                Text(
                    text = restaurant.city
                )
            }
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .background(
                        color = White,
                        shape = RoundedCornerShape(16.dp)
                    )
            ){
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cb3),
                        contentDescription = "Foto do lugar",
                        modifier = Modifier
                            .width(120.dp)
                            .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                bottomStart = 16.dp
                            )
                            ),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Nome do Lugar",
                            fontFamily = Poppins,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Localização",
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.End

                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_next),
                            contentDescription = "Visualizar",
                            tint = Blue,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(28.dp)
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Box(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .background(
                        color = White,
                        shape = RoundedCornerShape(16.dp)
                    )
            ){
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cb3),
                        contentDescription = "Foto do lugar",
                        modifier = Modifier
                            .width(120.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    bottomStart = 16.dp
                                )
                            ),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Nome do Lugar",
                            fontFamily = Poppins,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Localização",
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.End

                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_next),
                            contentDescription = "Visualizar",
                            tint = Blue,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(28.dp)
                        )

                    }
                }
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun CatalogPreview() {
    DesbravandoTheme {
        Catalog()
    }
}

data class Restaurants(
    val name: String = "",
    val city: String = ""
)

fun findRestaurants(
    onResult: (List<Restaurants>) -> Unit
){
    FirebaseFirestore
        .getInstance()
        .collection("locations")
        .whereEqualTo("tags", "GatroBar")
        .get()
        .addOnSuccessListener { result ->
            val list = result.documents.mapNotNull {
                it.toObject(Restaurants::class.java)
            }
            onResult(list)
        }
}