package com.desbravando.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.desbravando.app.data.remote.RetrofitInstance
import com.desbravando.app.ui.components.BottomBar
import com.desbravando.app.ui.components.BottomBarWithNavigation
import com.desbravando.app.ui.theme.*
import com.desbravando.app.ui.theme.Poppins
import com.desbravando.app.ui.utils.WeatherVisual
import com.desbravando.app.ui.utils.getWeatherVisual
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue

const val API_KEY = "6168cafa8739e67b09689ffecf6e0eac"

class HomeActivity : ComponentActivity() {

    private var temperature = mutableStateOf("--°C")
    private var weatherDesc = mutableStateOf("Carregando...")
    private var weatherVisual = mutableStateOf(WeatherVisual(R.drawable.ic_sun, Color(0xFFFFCC00)))
    private var userName = mutableStateOf("Explorador")
    private var carouselImages = mutableStateOf<List<String>>(emptyList())

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) fetchWeather()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        fetchUserData()
        fetchCarouselImages()

        val alreadyGranted = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (alreadyGranted) {
            fetchWeather()
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        setContent {
            DesbravandoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomBarWithNavigation(
                            selectedRoute = "home",
                            context = this
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(OffWhite)
                    ) {
                        Home(
                            temperature = temperature.value,
                            weatherDesc = weatherDesc.value,
                            weatherVisual = weatherVisual.value,
                            userName = userName.value,
                            carouselImages = carouselImages.value
                        )
                    }
                }
            }
        }
    }

    private fun fetchUserData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name") ?: "Explorador"
                userName.value = name
            }
    }

    private fun fetchCarouselImages() {
        FirebaseFirestore.getInstance()
            .collection("locations")
            .get()
            .addOnSuccessListener { result ->
                val allCovers = result.documents.mapNotNull { doc ->
                    doc.getString("cover")?.takeIf { it.isNotBlank() }
                }
                carouselImages.value = allCovers.shuffled().take(5)
            }
    }

    private fun fetchWeather() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                callWeatherApi(location.latitude, location.longitude)
            } else {
                val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                    com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 5000L
                ).setMaxUpdates(1).build()

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    object : com.google.android.gms.location.LocationCallback() {
                        override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                            fusedLocationClient.removeLocationUpdates(this)
                            val loc = result.lastLocation ?: return
                            callWeatherApi(loc.latitude, loc.longitude)
                        }
                    },
                    android.os.Looper.getMainLooper()
                )
            }
        }
    }

    private fun callWeatherApi(lat: Double, lon: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getWeather(
                    lat = lat,
                    lon = lon,
                    apiKey = API_KEY
                )
                withContext(Dispatchers.Main) {
                    temperature.value = "${response.main.temp.toInt()}°C"
                    weatherDesc.value = response.weather.firstOrNull()?.description ?: "Indisponível"
                    weatherVisual.value = getWeatherVisual(response.weather.firstOrNull()?.main ?: "Clear")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    weatherDesc.value = "Erro ao carregar"
                }
            }
        }
    }
}

@Composable
fun Home(
    modifier: Modifier = Modifier,
    temperature: String = "--°C",
    weatherDesc: String = "Carregando...",
    weatherVisual: WeatherVisual = WeatherVisual(R.drawable.ic_sun, Color(0xFFFFCC00)),
    userName: String = "Explorador",
    carouselImages: List<String> = emptyList()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 30.dp, start = 10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.purple_logo_desbravando),
                contentDescription = stringResource(R.string.cd_logo),
                modifier = Modifier.width(15.dp).height(15.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(R.string.home_logo_text),
                fontFamily = Poppins,
                fontSize = 15.sp,
                color = Purple,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .padding(start = 20.dp, top = 15.dp, end = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Olá, $userName!",
                    fontFamily = Poppins,
                    fontSize = 24.sp,
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.home_subtitle),
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Gray,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                WeatherWidget(
                    temperature = temperature,
                    weatherDesc = weatherDesc,
                    weatherVisual = weatherVisual
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        StateSelector()

        Spacer(modifier = Modifier.height(20.dp))

        BigLocationCardList(images = carouselImages)

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CategoryCard(icon = R.drawable.ic_beach, label = "Praias") {
                context.startActivity(
                    Intent(context, CatalogActivity::class.java)
                        .putExtra("filter_category", "Praias")
                )
            }
            CategoryCard(icon = R.drawable.ic_tree, label = "Parques") {
                context.startActivity(
                    Intent(context, CatalogActivity::class.java)
                        .putExtra("filter_category", "Parques")
                )
            }
            CategoryCard(icon = R.drawable.ic_church, label = "Religioso") {
                context.startActivity(
                    Intent(context, CatalogActivity::class.java)
                        .putExtra("filter_category", "Religioso")
                )
            }
            CategoryCard(icon = R.drawable.ic_utensils, label = "Gastrobar") {
                context.startActivity(
                    Intent(context, CatalogActivity::class.java)
                        .putExtra("filter_category", "GastroBar")
                )
            }
            CategoryCard(icon = R.drawable.ic_mountain, label = "Eco") {
                context.startActivity(
                    Intent(context, CatalogActivity::class.java)
                        .putExtra("filter_category", "Eco")
                )
            }
            CategoryCard(icon = R.drawable.ic_historic, label = "Histórico") {
                context.startActivity(
                    Intent(context, CatalogActivity::class.java)
                        .putExtra("filter_category", "Histórico")
                )
            }
        }
    }
}

@Composable
fun WeatherWidget(
    temperature: String = "--°C",
    weatherDesc: String = "Carregando...",
    weatherVisual: WeatherVisual = WeatherVisual(R.drawable.ic_sun, Color(0xFFFFCC00))
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp), spotColor = Purple)
            .clip(RoundedCornerShape(10.dp))
            .background(White)
            .padding(6.dp)
    ) {
        Icon(
            painter = painterResource(id = weatherVisual.icon),
            contentDescription = stringResource(R.string.cd_climate),
            tint = weatherVisual.tint,
            modifier = Modifier.size(35.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = temperature,
                fontFamily = Poppins,
                fontSize = 18.sp,
                color = DarkBlue,
                fontWeight = FontWeight(600)
            )
            Text(
                text = weatherDesc,
                fontFamily = Poppins,
                fontSize = 12.sp,
                color = Gray,
                fontWeight = FontWeight(500)
            )
        }
    }
}

val estados = listOf(
    "Acre", "Alagoas", "Amapá", "Amazonas", "Bahia",
    "Ceará", "Distrito Federal", "Espírito Santo", "Goiás",
    "Maranhão", "Mato Grosso", "Mato Grosso do Sul", "Minas Gerais",
    "Pará", "Paraíba", "Paraná", "Pernambuco", "Piauí",
    "Rio de Janeiro", "Rio Grande do Norte", "Rio Grande do Sul",
    "Rondônia", "Roraima", "Santa Catarina", "São Paulo",
    "Sergipe", "Tocantins"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StateSelector() {
    var expanded by remember { mutableStateOf(false) }
    var estadoSelecionado by remember { mutableStateOf("Espírito Santo") }

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
                    contentDescription = "map pin",
                    tint = Blue
                )
                Text(
                    text = "Estado selecionado:",
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
                            estadoSelecionado = estado
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BigLocationCardList(images: List<String> = emptyList()) {

    val placeholders = listOf("", "", "", "", "")
    val cards = images.ifEmpty { placeholders }

    val pagerState = rememberPagerState(pageCount = { cards.size })

    LaunchedEffect(Unit) {
        while (true) {
            delay(10000L)
            val next = (pagerState.currentPage + 1) % cards.size
            pagerState.animateScrollToPage(next)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 40.dp),
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val scale = lerp(0.90f, 1f, 1f - pageOffset.absoluteValue.coerceIn(0f, 1f))
            val alpha = lerp(0.6f, 1f, 1f - pageOffset.absoluteValue.coerceIn(0f, 1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .graphicsLayer {
                        scaleY = scale
                        scaleX = scale
                        this.alpha = alpha
                    }
                    .clip(RoundedCornerShape(30.dp))
                    .background(LightGray)
            ) {
                if (cards[page].isNotBlank()) {
                    AsyncImage(
                        model = cards[page],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 15.dp, start = 15.dp)
                        .background(Purple, RoundedCornerShape(30.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = null,
                        tint = White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Em destaque",
                        color = White,
                        fontFamily = Poppins,
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 15.dp, bottom = 15.dp)
                        .background(Purple, RoundedCornerShape(30.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Explorar", color = White, fontFamily = Poppins, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = White
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 15.dp, bottom = 15.dp)
                        .size(40.dp)
                        .background(White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_heart_regular),
                        contentDescription = "Favoritar",
                        tint = Purple,
                        modifier = Modifier.fillMaxSize(0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(cards.size) { index ->
                val isSelected = pagerState.currentPage == index
                val width by animateDpAsState(
                    targetValue = if (isSelected) 20.dp else 7.dp,
                    animationSpec = tween(300),
                    label = "dot_width"
                )
                Box(
                    modifier = Modifier
                        .height(7.dp)
                        .width(width)
                        .background(
                            color = if (isSelected) Purple else LightGray,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
fun CategoryCard(
    icon: Int,
    label: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .width(55.dp)
            .height(75.dp)
            .clickable { onClick() }
            .background(color = White, RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.TopCenter)
                .background(LightGray, CircleShape)
                .padding(5.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Blue,
            )
        }
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 8.sp,
            color = Blue,
            fontWeight = FontWeight(500),
            modifier = Modifier
                .padding(5.dp)
                .align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeActivityPreview() {
    DesbravandoTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = { BottomBar("home") {} }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(OffWhite)
            ) {
                Home()
            }
        }
    }
}