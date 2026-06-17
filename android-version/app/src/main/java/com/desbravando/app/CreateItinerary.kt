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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.desbravando.app.ui.components.AddLocalCard
import com.desbravando.app.ui.components.CategoryCard
import com.desbravando.app.ui.theme.Blue
import com.desbravando.app.ui.theme.BlueSecondary
import com.desbravando.app.ui.theme.DarkBlue
import com.desbravando.app.ui.theme.DesbravandoTheme
import com.desbravando.app.ui.theme.Gray
import com.desbravando.app.ui.theme.LightBlue
import com.desbravando.app.ui.theme.LightPurple
import com.desbravando.app.ui.theme.MediumGray
import com.desbravando.app.ui.theme.OffWhite
import com.desbravando.app.ui.theme.Poppins
import com.desbravando.app.ui.theme.Purple
import com.desbravando.app.ui.theme.White
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class CreateItinerary : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        enableEdgeToEdge()
        setContent {
            DesbravandoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Itinerary(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
@Composable
fun Itinerary(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(0) }
    var itineraryTitle by remember { mutableStateOf("") }
    var customCoverUrl by remember { mutableStateOf<String?>(null) }
    var locations by remember { mutableStateOf<List<Location>>(emptyList()) }
    var selectedLocations by remember { mutableStateOf<List<Location>>(emptyList()) }

    LaunchedEffect(Unit) {
        findLocations { list -> locations = list }
    }

    when (currentStep) {
        0 -> CreateItineraryStep(
            modifier = modifier,
            onNext = { title, coverUrl ->
                itineraryTitle = title
                customCoverUrl = coverUrl
                currentStep = 1
            },
            onBackHome = { (context as? ComponentActivity)?.finish() }
        )
        1 -> DefineRouteStep(
            modifier = modifier,
            onNext = { currentStep = 2 },
            onBack = { currentStep = 0 }
        )
        2 -> AddLocalsStep(
            modifier = modifier,
            locations = locations,
            selectedLocations = selectedLocations,
            onToggleLocation = { location ->
                selectedLocations = if (location in selectedLocations)
                    selectedLocations - location
                else
                    selectedLocations + location
            },
            onNext = { currentStep = 3 },
            onBack = { currentStep = 1 }
        )
        3 -> OrganizeLocalsStep(
            modifier = modifier,
            selectedLocations = selectedLocations,
            onNext = { currentStep = 4 },
            onBack = { currentStep = 2 }
        )
        4 -> ViewItineraryStep(
            modifier = modifier,
            itineraryTitle = itineraryTitle,
            customCoverUrl = customCoverUrl,
            selectedLocations = selectedLocations,
        )
    }
}


// ─── PASSO 0 ────────────────────────────────────────────────
@Composable
fun CreateItineraryStep(
    modifier: Modifier = Modifier,
    onNext: (String, String?) -> Unit,
    onBackHome: () -> Unit
) {
    val context = LocalContext.current
    var itineraryName by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedCoverUrl by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        imageUri = uri
        if (uri != null) {
            isUploading = true
            uploadImageToCloudinary(context, uri) { url ->
                uploadedCoverUrl = url
                isUploading = false
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(OffWhite)
            .padding(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        ) {
            IconButton(onClick = { onBackHome() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Voltar",
                    modifier = Modifier.size(26.dp),
                    tint = Blue
                )
            }
            Text(
                text = stringResource(R.string.title_new_itinerary),
                fontSize = 14.sp,
                color = Blue,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(bottom = 20.dp).fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(start = 5.dp, top = 40.dp).fillMaxWidth()
            ) {
                Text(
                    text = "Para onde vamos?",
                    fontSize = 18.sp,
                    color = DarkBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )
                Text(
                    text = "Escolha o destino do seu roteiro",
                    fontSize = 12.sp,
                    color = Gray,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Poppins
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
                .clickable { launcher.launch(arrayOf("image/*")) },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Blue,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Escolha uma foto de capa",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Blue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = itineraryName,
            onValueChange = { itineraryName = it },
            placeholder = { Text("Ex: Férias em Família", fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(30.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MediumGray,
                focusedBorderColor = Blue,
                focusedTextColor = Blue,
                unfocusedTextColor = MediumGray
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { if (itineraryName.isNotBlank()) onNext(itineraryName, uploadedCoverUrl) },
            enabled = itineraryName.isNotBlank() && !isUploading,
            modifier = Modifier.fillMaxWidth().height(45.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple)
        ) {
            Text(text = "Iniciar Configuração", color = Color.White, fontFamily = Poppins)
        }

        Spacer(modifier = Modifier.weight(1.2f))
    }
}

// ─── PASSO 1 ────────────────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DefineRouteStep(
    modifier: Modifier = Modifier,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(color = OffWhite)
            .verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
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
                    text = stringResource(R.string.title_new_itinerary),
                    fontSize = 14.sp,
                    color = Blue,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins,
                    modifier = Modifier.align(Alignment.CenterVertically).padding(top = 1.dp)
                )
                Spacer(modifier = Modifier.size(48.dp))
            }

            Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(start = 5.dp, top = 20.dp).fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.subtitle_new_itinerary),
                        fontSize = 18.sp,
                        color = DarkBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Poppins
                    )
                    Text(
                        text = stringResource(R.string.title_define_details_itinerary),
                        fontSize = 12.sp,
                        color = Gray,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins
                    )
                }
            }

            Row(modifier = Modifier.padding(start = 5.dp, bottom = 10.dp, top = 25.dp).fillMaxWidth()) {
                Text(text = stringResource(R.string.question_days), fontSize = 12.sp, color = DarkBlue, fontFamily = Poppins, fontWeight = FontWeight.Medium)
            }
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                DayCard()
            }

            Row(modifier = Modifier.padding(start = 5.dp, bottom = 10.dp, top = 25.dp).fillMaxWidth()) {
                Text(text = stringResource(R.string.question_with_whom), fontSize = 12.sp, color = DarkBlue, fontFamily = Poppins, fontWeight = FontWeight.Medium)
            }
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                CompanyCard()
            }

            Row(modifier = Modifier.padding(start = 5.dp, bottom = 10.dp, top = 25.dp).fillMaxWidth()) {
                Text(text = stringResource(R.string.title_style_trip), fontSize = 12.sp, color = DarkBlue, fontFamily = Poppins, fontWeight = FontWeight.Medium)
            }
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                InterestsCard()
            }

            Row(modifier = Modifier.padding(start = 5.dp, bottom = 10.dp, top = 25.dp).fillMaxWidth()) {
                Text(text = stringResource(R.string.title_style_trip), fontSize = 12.sp, color = DarkBlue, fontFamily = Poppins, fontWeight = FontWeight.Medium)
            }
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                MoodCard()
            }

            Button(
                onClick = { onNext() },
                modifier = Modifier.fillMaxWidth().padding(top = 25.dp).height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        brush = Brush.horizontalGradient(colorStops = arrayOf(0.0f to Purple, 1.0f to Blue))
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.title_keep), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium, fontFamily = Poppins)
                }
            }
        }
    }
}

// ─── PASSO 2 ────────────────────────────────────────────────
@Composable
fun AddLocalsStep(
    modifier: Modifier = Modifier,
    locations: List<Location> = emptyList(),
    selectedLocations: List<Location> = emptyList(),
    onToggleLocation: (Location) -> Unit = {},
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize().background(color = OffWhite)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
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
                    text = stringResource(R.string.text_add_places),
                    fontSize = 14.sp,
                    color = Blue,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins,
                    modifier = Modifier.align(Alignment.CenterVertically).padding(top = 1.dp)
                )
                Spacer(modifier = Modifier.size(48.dp))
            }

            Row(horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(top= 20.dp)
                    .fillMaxWidth()) {
                CategoryCard()
            }

            Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(start = 5.dp, top = 25.dp).fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.text_suggestions), fontSize = 16.sp, color = DarkBlue, fontWeight = FontWeight.SemiBold, fontFamily = Poppins)
                    Text(text = stringResource(R.string.message_perfect_suggestions), fontSize = 12.sp, color = Gray, fontWeight = FontWeight.Medium, fontFamily = Poppins)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(locations) { location ->
                AddLocalCard(
                    location = location,
                    isSelected = location in selectedLocations,
                    onClick = { onToggleLocation(location) }
                )
            }
        }

        Button(
            onClick = { onNext() },
            modifier = Modifier.fillMaxWidth().padding(24.dp).height(45.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    brush = Brush.horizontalGradient(colorStops = arrayOf(0.0f to Purple, 1.0f to Blue))
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.title_keep), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium, fontFamily = Poppins)
            }
        }
    }
}

// ─── PASSO 3 ────────────────────────────────────────────────
@Composable
fun OrganizeLocalsStep(
    modifier: Modifier = Modifier,
    selectedLocations: List<Location> = emptyList(),
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize().background(color = OffWhite)
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
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
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(selectedLocations) { location ->
                AddLocalCard(location = location, isSelected = true, onClick = {})
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onBack() },
                modifier = Modifier.fillMaxWidth().height(45.dp).border(width = 2.dp, color = Purple, shape = RoundedCornerShape(20.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Purple),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = "Adicionar Locais", fontFamily = Poppins, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = { onNext() },
                modifier = Modifier.fillMaxWidth().height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple, contentColor = Color.White),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = "Continuar", fontFamily = Poppins, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// ─── PASSO 4 ────────────────────────────────────────────────
@Composable
fun ViewItineraryStep(
    modifier: Modifier = Modifier,
    itineraryTitle: String,
    customCoverUrl: String?,
    selectedLocations: List<Location> = emptyList(),
) {
    val context = LocalContext.current
    val displayCoverUrl = customCoverUrl ?: selectedLocations.firstOrNull()?.imageUrl

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize().background(OffWhite)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 16.dp)
        ) {
            Text(
                text = "Roteiro Pronto!",
                fontSize = 14.sp,
                color = Blue,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(top = 50.dp)
                .fillMaxSize()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp).padding(horizontal = 24.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (displayCoverUrl != null) {
                        AsyncImage(model = displayCoverUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Box(modifier = Modifier.fillMaxSize().background(BlueSecondary))
                    }
                    Box(
                        modifier = Modifier.fillMaxSize().background(
                            Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)))
                        )
                    )
                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                        Text(text = itineraryTitle.ifBlank { "Meu Roteiro" }, color = Color.White, fontSize = 18.sp, fontFamily = Poppins, fontWeight = FontWeight.Bold)
                        Text(text = "${selectedLocations.size} locais", color = Color.White, fontSize = 12.sp, fontFamily = Poppins)
                    }
                }
            }

            Button(
                onClick = {
                    val auth = FirebaseAuth.getInstance()
                    val db = Firebase.firestore
                    val uid = auth.currentUser?.uid ?: return@Button
                    val roteiro = hashMapOf(
                        "title" to itineraryTitle,
                        "imageUrl" to displayCoverUrl,
                        "locations" to selectedLocations.map { it.id },
                        "createdAt" to System.currentTimeMillis()
                    )
                    db.collection("users")
                        .document(uid)
                        .collection("itineraries")
                        .add(roteiro)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Roteiro salvo!", Toast.LENGTH_SHORT).show()
                            (context as? ComponentActivity)?.finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Erro ao salvar roteiro", Toast.LENGTH_SHORT).show()
                        }
                },
                modifier = Modifier.fillMaxWidth().padding(24.dp).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = "Começar a aventura!", color = Color.White, fontFamily = Poppins, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// ─── Cards ────────────────────────────────────────────────
@Composable
fun DayCard() {
    val options = listOf("1 dia", "2 dias", "3 dias", "+ de 3")
    var selected by remember { mutableStateOf("") }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(40))
                    .background(if (isSelected) Purple else White)
                    .clickable { selected = option }
                    .padding(horizontal = 15.dp, vertical = 5.dp)
            ) {
                Text(text = option, fontSize = 12.sp, fontFamily = Poppins, fontWeight = FontWeight.Medium, color = if (isSelected) Color.White else DarkBlue)
            }
        }
    }
}

data class FilterOption(val label: String, val icon: Int)

@Composable
fun CompanyCard() {
    val options = listOf(
        FilterOption("Sozinho", R.drawable.ic_alone),
        FilterOption("Casal", R.drawable.ic_couple),
        FilterOption("Família", R.drawable.ic_family),
        FilterOption("Amigos", R.drawable.ic_friends)
    )
    var selected by remember { mutableStateOf("") }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        options.forEach { option ->
            val isSelected = option.label == selected
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(20))
                    .background(if (isSelected) Purple else White)
                    .clickable { selected = option.label }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(painter = painterResource(id = option.icon), contentDescription = null, modifier = Modifier.padding(top = 2.dp).size(22.dp), tint = if (isSelected) Color.White else DarkBlue)
                    Text(text = option.label, fontSize = 12.sp, fontFamily = Poppins, fontWeight = FontWeight.Medium, color = if (isSelected) Color.White else DarkBlue, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
    }
}

@Composable
fun MoodCard() {
    val options = listOf(
        FilterOption("Relaxado", R.drawable.ic_relax),
        FilterOption("Equilibrado", R.drawable.ic_compass),
        FilterOption("Aventura", R.drawable.ic_mountain)
    )
    var selected by remember { mutableStateOf("") }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        options.forEach { option ->
            val isSelected = option.label == selected
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(20))
                    .background(if (isSelected) Purple else White)
                    .clickable { selected = option.label }
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(painter = painterResource(id = option.icon), contentDescription = null, modifier = Modifier.padding(top = 2.dp).size(20.dp), tint = if (isSelected) Color.White else DarkBlue)
                    Text(text = option.label, fontSize = 12.sp, fontFamily = Poppins, fontWeight = FontWeight.Medium, color = if (isSelected) Color.White else DarkBlue, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestsCard() {
    val options = listOf(
        FilterOption("Praias", R.drawable.ic_beach),
        FilterOption("ECO", R.drawable.ic_mountain),
        FilterOption("GastroBar", R.drawable.ic_utensils),
        FilterOption("Histórico", R.drawable.ic_landmark),
        FilterOption("Parques", R.drawable.ic_tree)
    )
    var selected by remember { mutableStateOf(setOf<String>()) }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        options.forEach { option ->
            val isSelected = option.label in selected
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(20))
                    .background(if (isSelected) Purple else White)
                    .clickable {
                        selected = if (option.label in selected) selected - option.label else selected + option.label
                    }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(painter = painterResource(id = option.icon), contentDescription = null, modifier = Modifier.padding(top = 2.dp).size(22.dp), tint = if (isSelected) Color.White else DarkBlue)
                    Text(text = option.label, fontSize = 12.sp, fontFamily = Poppins, fontWeight = FontWeight.Medium, color = if (isSelected) Color.White else DarkBlue, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
    }
}

// ─── Previews ────────────────────────────────────────────────


@Preview(name = "Passo 4 - Visualização Final", showBackground = true)
@Composable
fun PreviewStep4() {
    DesbravandoTheme {
        // No Preview passamos dados fictícios apenas para ver o design
        ViewItineraryStep(
            itineraryTitle = "Minhas Férias",
            customCoverUrl = null,
            selectedLocations = emptyList(),

        )
    }
}