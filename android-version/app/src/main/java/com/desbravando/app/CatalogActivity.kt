package com.desbravando.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.desbravando.app.ui.components.CategoryCard
import com.desbravando.app.ui.components.LocalCard
import com.desbravando.app.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore

class CatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val filterCategory = intent.getStringExtra("filter_category") ?: ""

        setContent {
            DesbravandoTheme {
                Catalog(initialCategory = filterCategory)
            }
        }
    }
}

@Composable
fun Catalog(initialCategory: String = "") {

    var locations by remember { mutableStateOf<List<Location>>(emptyList()) }
    var search by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(initialCategory) } // ← usa o valor inicial

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

        matchesSearch && matchesCategory
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
            Row(verticalAlignment = Alignment.CenterVertically) {
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

        HorizontalDivider(thickness = 1.dp)

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
            modifier = Modifier.padding(top = 20.dp),
            thickness = 1.dp
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(filteredLocations) { location ->
                LocalCard(location = location, onClick = { })
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
                    tags     = (data["tags"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                )
            }
            onResult(list)
        }
}