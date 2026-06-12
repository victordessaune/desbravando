package com.desbravando.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.desbravando.app.data.remote.RetrofitInstance
import com.desbravando.app.ui.components.BottomBar
import com.desbravando.app.ui.theme.*
import com.desbravando.app.ui.utils.WeatherVisual
import com.desbravando.app.ui.utils.getWeatherVisual
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val API_KEY = "6168cafa8739e67b09689ffecf6e0eac"

class HomeActivity : ComponentActivity() {

    private var temperature = mutableStateOf("--°C")
    private var weatherDesc = mutableStateOf("Carregando...")

    private var weatherVisual = mutableStateOf(WeatherVisual(R.drawable.ic_sun, Color(0xFFFFCC00)))

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
                var selectedRoute by remember { mutableStateOf("home") }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomBar(selectedRoute) { route ->
                            selectedRoute = route
                        }
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
                            weatherVisual = weatherVisual.value
                        )
                    }
                }
            }
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
    weatherVisual: WeatherVisual = WeatherVisual(R.drawable.ic_sun, Color(0xFFFFCC00))
) {
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
                .padding(start = 20.dp, top = 15.dp, end = 30.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_welcome),
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
            tint = weatherVisual.tint, // <- agora usa a cor dinâmica
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