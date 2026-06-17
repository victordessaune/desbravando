package com.desbravando.app

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import coil.compose.AsyncImage
import com.desbravando.app.ui.components.BottomBar
import com.desbravando.app.ui.theme.Blue
import com.desbravando.app.ui.theme.BlueCustom
import com.desbravando.app.ui.theme.BlueSecondary
import com.desbravando.app.ui.theme.DarkBlue
import com.desbravando.app.ui.theme.DarkGray
import com.desbravando.app.ui.theme.DesbravandoTheme
import com.desbravando.app.ui.theme.Gray
import com.desbravando.app.ui.theme.LightGray
import com.desbravando.app.ui.theme.MediumGray
import com.desbravando.app.ui.theme.OffWhite
import com.desbravando.app.ui.theme.Poppins
import com.desbravando.app.ui.theme.Purple
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.desbravando.app.ui.components.BottomBarWithNavigation
import com.desbravando.app.ui.components.CategoryCard
import com.desbravando.app.ui.components.FavoriteCard
import com.desbravando.app.ui.components.FavoriteWideCard
import com.desbravando.app.ui.components.LocalCard
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class FavoritesActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
        setContent {
            DesbravandoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomBarWithNavigation(
                            selectedRoute = "favorites",
                            context = this
                        )
                    }
                ) { innerPadding ->
                    Favorites(
                        onBack = { finish() },
                        modifier = Modifier.padding(innerPadding),

                   )
                }
            }
        }
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Favorites(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var favorites by remember { mutableStateOf<List<FavoriteLocation>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        FavoritesRepository.getFavorites { list ->
            favorites = list
        }
    }
    val filteredFavorites = favorites.filter { item ->
        if (selectedCategory.isEmpty()) return@filter true

        item.tags.any { tag ->
            // Remove o 's' do final para comparar "Parque" com "Parques"
            val cleanTag = tag.trim().removeSuffix("s")
            val cleanSelected = selectedCategory.trim().removeSuffix("s")

            cleanTag.equals(cleanSelected, ignoreCase = true) ||
                    tag.contains(selectedCategory, ignoreCase = true) ||
                    selectedCategory.contains(tag, ignoreCase = true)
        }

    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(color = OffWhite)
            .verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            var userName by remember { mutableStateOf("") }
            var userNickname by remember { mutableStateOf("") }
            var userEmail by remember { mutableStateOf("") }
            var userBio by remember { mutableStateOf("") }
            var userPassword by remember { mutableStateOf("") }
            var userConfirmPassword by remember { mutableStateOf("") }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Voltar",
                        modifier = Modifier.size(26.dp),
                        tint = Blue
                    )
                }
                Text(
                    text = "Organizar Roteiro",
                    fontSize = 14.sp,
                    color = Blue,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )
                Spacer(modifier = Modifier.size(48.dp))



            }
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .padding(top = 15.dp)
                        .fillMaxWidth()
                ) {

                    Text(
                        text = "Favoritos",
                        fontSize = 18.sp,
                        color = DarkBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Purple,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("${favorites.size} ")
                            }
                            withStyle(style = SpanStyle(color = Gray)) {
                                append("Lugares salvos")
                            }
                        },
                        fontSize = 13.sp,
                        color = Gray,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins,
                    )
                    Text(
                        text = "Seus lugares favoritos para visitar e explorar!",
                        fontSize = 12.sp,
                        color = Gray,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins,

                        )
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .padding(top = 15.dp)
                    .padding(bottom = 15.dp)
                    .fillMaxWidth()
            ) {
                CategoryCard(
                    selectedTags = if (selectedCategory.isEmpty()) emptySet() else setOf(selectedCategory),
                    onTagSelected = { tag ->
                        selectedCategory = if (tag == "Todos") ""
                        else if (selectedCategory == tag) ""
                        else tag
                    }
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                filteredFavorites.forEach { fav ->
                    FavoriteWideCard(
                        favorite = fav,
                        onRemove = {
                            favorites = favorites.filter { it.id != fav.id }

                        }
                    )
                }
            }



        }

    }
}
@Preview(showBackground = true)
@Composable
fun FavoritesPreview() {
    DesbravandoTheme {
        Favorites()
    }
}

