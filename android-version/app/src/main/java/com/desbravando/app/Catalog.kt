package com.desbravando.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.desbravando.app.ui.theme.White
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.draw.shadow
import com.desbravando.app.ui.theme.Gray

class Catalog : ComponentActivity() {
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
    val filteredRestaurants = restaurants.filter {
        it.name.contains(search, ignoreCase = true)
    }

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

            Row(
                verticalAlignment = Alignment.CenterVertically
            ){

                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Voltar",
                    modifier = Modifier
                        .size(28.dp)
                        .border(
                            width = 1.dp,
                            color = Blue,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(5.dp)
                )

                Spacer(modifier = Modifier.width(7.dp))

                Text(
                    text = "Catálogo de Lugares",
                    fontFamily = Poppins,
                    fontSize = 17.sp,
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
                    Text("Buscar lugares")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Pesquisar"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = White)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gray,
                    unfocusedBorderColor = Gray,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
            )
            )
        }
        
        HorizontalDivider(
            thickness = 1.dp
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(top = 20.dp, start = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(categories) { category ->

                CategoryItem(
                    category = category,
                    onClick = {

                        // filtrar lugares pela categoria

                    }
                )

            }
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(top = 20.dp),
            thickness = 1.dp
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {

            items(filteredRestaurants) { restaurant ->

                RestaurantCard(
                    restaurant = restaurant,
                    onClick = {

                        // Aqui depois você navega
                        // navController.navigate(...)

                    }
                )
            }
        }

    }
}
@Composable
fun RestaurantCard(
    restaurant: Restaurants,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .height(110.dp)
            .fillMaxWidth()
            .background(
                color = White,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {

            Image(
                painter = painterResource(id = R.drawable.cb3),
                contentDescription = "Foto do lugar",
                modifier = Modifier
                    .width(130.dp)
                    .fillMaxHeight()
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
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = restaurant.name,
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = restaurant.city,
                    fontFamily = Poppins,
                    fontSize = 14.sp
                )
            }

            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
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

//Fuções relacionadas a categoria
data class Category(
    val name: String,
    val icon: Int
)

val categories = listOf(
    Category("Praia", R.drawable.ic_beach),
    Category("Restaurante", R.drawable.ic_utensils),
    Category("Parque", R.drawable.ic_tree),
    Category("Histórico", R.drawable.ic_historic),
    Category("Religioso", R.drawable.ic_church),
    Category("Eco", R.drawable.ic_mountain)
)

@Composable
fun CategoryItem(
    category: Category,
    onClick: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 2.dp),

    ) {

        Box(
            modifier = Modifier
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(10.dp)
                )
                .fillMaxSize()
                .background(
                    color = White,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(category.icon),
                    contentDescription = category.name,
                    tint = Blue,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = category.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(500)
                )
            }
        }
    }
}