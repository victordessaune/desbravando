  package com.desbravando.app

import android.content.Intent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.desbravando.app.ui.theme.White
import com.desbravando.app.FavoritesRepository
import com.desbravando.app.FavoriteLocation
import com.desbravando.app.ui.components.BottomBarWithNavigation
import com.desbravando.app.ui.components.FavoriteCard
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class ProfileActivity : ComponentActivity() {

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
                            selectedRoute = "profile",
                            context = this
                        )
                    }
                ) { innerPadding ->
                    Profile(
                        onBack = { finish() },
                        onLogout = {
                            auth.signOut()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun Profile(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var itineraries by remember { mutableStateOf<List<SavedItinerary>>(emptyList()) }

    LaunchedEffect(Unit) {
        ItinerariesRepository.getItineraries { itineraries = it }
    }
    var favorites by remember { mutableStateOf<List<FavoriteLocation>>(emptyList()) }

    LaunchedEffect(Unit) {
        FavoritesRepository.getFavorites { favorites = it }
    }

    val favoritosExibidos = favorites.takeLast(3)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(color = OffWhite)
            .verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
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
                        contentDescription = "Voltar",
                        tint = Blue,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(onClick = { onLogout() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logout),
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = Blue
                    )
                }
            }



            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfilePicture()

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .padding(top = 25.dp)
                        .fillMaxWidth()
                ) {

                    Text(
                        text = "Nome Teste",
                        fontSize = 18.sp,
                        color = DarkBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                    Text(
                        text = "@nome_teste",
                        fontSize = 14.sp,
                        color = Gray,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins,

                    )
                    Text(
                        text = "Desbravando o ES",
                        fontSize = 12.sp,
                        color = Gray,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }


           Row(
               horizontalArrangement = Arrangement.SpaceBetween,
               modifier = Modifier
                   .padding(start = 5.dp)
                   .padding(end = 5.dp)
                   .padding(bottom = 10.dp)
                   .padding(top = 12.dp)
                   .fillMaxWidth(),
           ){
               Text(
                   text = stringResource(R.string.text_my_itinerary),
                   fontSize = 14.sp,
                   color = Purple,
                   fontFamily = Poppins,
                   fontWeight = FontWeight.Medium,

               )
               Text(
                   text = stringResource(R.string.text_see_all),
                   fontSize = 13.sp,
                   color = Gray,
                   fontFamily = Poppins,
                   fontWeight = FontWeight.Medium,

               )
           }

            if (itineraries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Você ainda não criou nenhum roteiro",
                        fontSize = 12.sp,
                        color = Gray,
                        fontFamily = Poppins,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    itineraries.chunked(2).forEach { par ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            par.forEach { itinerary ->
                                ItineraryCard(
                                    imageUrl = itinerary.imageUrl,
                                    name = itinerary.title,
                                    description = "${itinerary.locationsCount} locais",
                                    location = ""
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    context.startActivity(Intent(context, CreateItinerary::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp)
                    .padding(top = 5.dp)
                    .border(
                        width = 1.dp,
                        color = MediumGray,
                        shape = RoundedCornerShape(30.dp)
                    ),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.text_create_new_itinerary),
                        fontSize = 15.sp,
                        fontFamily = Poppins,
                        color = Blue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Nome
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .padding(end = 5.dp)
                    .padding(bottom = 10.dp)
                    .padding(top = 25.dp)
                    .fillMaxWidth(),
            ){
                Text(
                    text = stringResource(R.string.text_favorite_places),
                    fontSize = 14.sp,
                    color = Purple,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,

                    )
                val context = LocalContext.current
                Text(
                    text = stringResource(R.string.text_see_all),
                    fontSize = 13.sp,
                    color = Gray,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, FavoritesActivity::class.java))  }

                    )
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()

            ) { items(favoritosExibidos)  { fav ->
                FavoriteCard(
                    favorite = fav
                )

            }


            }
        }
    }
}
@Composable
fun ItineraryCard(
    imageUrl: String,
    name: String,
    location: String,
    description: String
) {
    var isFavorited by remember { mutableStateOf(true) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .width(165.dp),
    ) {
        Box {
            AsyncImage(
                model = imageUrl,
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

            ){
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(color = White, shape = CircleShape)
                        .clickable { isFavorited = !isFavorited }

                )
                {
                    Icon(
                        painter =  painterResource(id = if (isFavorited) R.drawable.ic_heart_regular
                        else R.drawable.heart),
                        contentDescription = null,
                        tint = Purple,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.Center)

                    )

                }

            }


        }


        Column(modifier = Modifier.padding(start = 14.dp, top = 6.dp, bottom = 4.dp, end = 8.dp)) {
            Text(
                text = name,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                color = DarkBlue,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Text(
                text = description,
                fontFamily = Poppins,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = Gray,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Text(
                text = location,
                fontFamily = Poppins,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = Gray,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }
    }
}


@Composable
fun ProfilePicture(modifier: Modifier = Modifier) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Box(contentAlignment = Alignment.BottomEnd) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = stringResource(R.string.cd_profile_picture),
                modifier = Modifier
                    .size(125.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(125.dp),
                tint = Color.LightGray
            )
        }

        IconButton(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier
                .offset(x = (-10).dp, y = (-15).dp)
                .background(Blue, CircleShape)
                .size(25.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = stringResource(R.string.cd_change_profile_picture),
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    DesbravandoTheme {
        Profile(onBack = {}, onLogout = {})
    }
}