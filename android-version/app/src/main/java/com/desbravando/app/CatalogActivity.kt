package com.desbravando.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Scaffold
import androidx.compose.ui.draw.clip
import com.desbravando.app.ui.components.CategoryCard
import com.desbravando.app.ui.components.LocalCard
import com.desbravando.app.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.desbravando.app.ui.components.BottomBarWithNavigation

class CatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val filterCategory = intent.getStringExtra("filter_category") ?: ""

        setContent {
            DesbravandoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomBarWithNavigation(
                            selectedRoute = "explore",
                            context = this
                        )
                    }
                ) { innerPadding ->
                    Catalog(
                        onBack = { finish() },
                        initialCategory = filterCategory,
                        modifier = Modifier.padding(innerPadding)
                    )
                }



            }
        }
    }
}

@Composable
fun Catalog(
    onBack: () -> Unit = {},
    initialCategory: String = "",
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    var locations by remember { mutableStateOf<List<Location>>(emptyList()) }
    var search by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(initialCategory) } // ← usa o valor inicial
    var selectedState by remember { mutableStateOf("Todos") }


    LaunchedEffect(Unit) {
        findLocations { list -> locations = list }
    }

    val filteredLocations = locations.filter { locationItem ->
        val matchesSearch = locationItem.name.contains(search, ignoreCase = true)

        val matchesCategory = selectedCategory.isEmpty() || locationItem.tags.any { tag ->
            val cleanTag = tag.trim().removeSuffix("s")
            val cleanSelected = selectedCategory.trim().removeSuffix("s")

            cleanTag.equals(cleanSelected, ignoreCase = true) ||
                    tag.contains(selectedCategory, ignoreCase = true) ||
                    selectedCategory.contains(tag, ignoreCase = true)
        }

        val estadosParaUf = mapOf(
            "Acre" to "AC",
            "Alagoas" to "AL",
            "Amapá" to "AP",
            "Amazonas" to "AM",
            "Bahia" to "BA",
            "Ceará" to "CE",
            "Distrito Federal" to "DF",
            "Espírito Santo" to "ES",
            "Goiás" to "GO",
            "Maranhão" to "MA",
            "Mato Grosso" to "MT",
            "Mato Grosso do Sul" to "MS",
            "Minas Gerais" to "MG",
            "Pará" to "PA",
            "Paraíba" to "PB",
            "Paraná" to "PR",
            "Pernambuco" to "PE",
            "Piauí" to "PI",
            "Rio de Janeiro" to "RJ",
            "Rio Grande do Norte" to "RN",
            "Rio Grande do Sul" to "RS",
            "Rondônia" to "RO",
            "Roraima" to "RR",
            "Santa Catarina" to "SC",
            "São Paulo" to "SP",
            "Sergipe" to "SE",
            "Tocantins" to "TO"
        )

        val matchesState =
            selectedState == "Todos" ||
                    locationItem.uf == estadosParaUf[selectedState]


        matchesSearch && matchesCategory && matchesState
    }

    Column(
        modifier = modifier
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .border(
                            width = 1.dp,
                            color = Blue,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = stringResource(R.string.cd_ic_back),
                        tint = Blue,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = stringResource(R.string.title_catalog),
                    fontFamily = Poppins,
                    fontSize = 17.sp,
                    fontWeight = FontWeight(600)
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
        }

        HorizontalDivider(thickness = 1.dp)

        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp)
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text(stringResource(R.string.search_bar_placeholder)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Pesquisar")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = White)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp)),
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
            modifier = Modifier.padding(bottom = 5.dp),
            thickness = 1.dp)

        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            CategoryCard(
                selectedTags = if (selectedCategory.isEmpty()) emptySet() else setOf(selectedCategory),
                onTagSelected = { tag ->
                    selectedCategory = if (tag == "Todos") ""
                    else if (selectedCategory == tag) ""
                    else tag
                }
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 5.dp, bottom = 16.dp),
            thickness = 1.dp
        )

        StateSelector(
            estadoSelecionado = selectedState,
            onEstadoChange = {selectedState = it}
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(filteredLocations) { location ->
                LocalCard(location = location, onClick = {
                    val intent = Intent(
                        context,
                        Places::class.java
                    )

                    intent.putExtra(
                        "PLACE_ID",
                        location.id
                    )

                    context.startActivity(intent)
                })
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

data class Location(
    val id: String = "",
    val name: String = "",
    val city: String = "",
    val uf: String = "",
    val imageUrl: String = "",
    val tags: List<String> = emptyList()
)

fun findLocations(onResult: (List<Location>) -> Unit) {
    FirebaseFirestore
        .getInstance()
        .collection("locations")
        .get()
        .addOnSuccessListener { result ->
            val list = result.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                Location(
                    id       = doc.id,
                    name     = data["name"] as? String ?: "",
                    city     = "${data["city"] as? String ?: ""}, ${data["uf"] as? String ?: ""}",
                    imageUrl = data["cover"] as? String ?: "",
                    uf = data["uf"] as? String ?: "",
                    tags     = (data["tags"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                )
            }
            onResult(list)
        }
}

val estados = listOf(
    "Todos", "Acre", "Alagoas", "Amapá", "Amazonas", "Bahia",
    "Ceará", "Distrito Federal", "Espírito Santo", "Goiás",
    "Maranhão", "Mato Grosso", "Mato Grosso do Sul", "Minas Gerais",
    "Pará", "Paraíba", "Paraná", "Pernambuco", "Piauí",
    "Rio de Janeiro", "Rio Grande do Norte", "Rio Grande do Sul",
    "Rondônia", "Roraima", "Santa Catarina", "São Paulo",
    "Sergipe", "Tocantins"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StateSelector(
    estadoSelecionado: String,
    onEstadoChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .height(40.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp), spotColor = Purple)
            .background(White, RoundedCornerShape(20.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .padding(start = 10.dp)
        ) {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.location_dot_solid_full),
                    contentDescription = stringResource(R.string.cd_map_pin),
                    tint = Blue,
                    modifier = Modifier
                        .padding(end = 4.dp)
                )
                Text(
                    text = stringResource(R.string.text_selected_state),
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = Gray,
                    fontWeight = FontWeight(500)
                )
            }
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(start = 15.dp)
                    .background(LightGray, RoundedCornerShape(15.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = estadoSelecionado,
                    fontFamily = Poppins,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Blue,
                    modifier = Modifier.size(20.dp)
                )
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                estados.forEach { estado ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = estado,
                                fontFamily = Poppins,
                                fontSize = 13.sp,
                                color = if (estado == estadoSelecionado) Blue else DarkBlue
                            )
                        },
                        onClick = {
                            onEstadoChange(estado)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}