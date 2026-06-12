package com.desbravando.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.desbravando.app.ui.components.BottomBar
import com.desbravando.app.ui.theme.DarkBlue
import com.desbravando.app.ui.theme.DesbravandoTheme
import com.desbravando.app.ui.theme.Gray
import com.desbravando.app.ui.theme.OffWhite
import com.desbravando.app.ui.theme.Poppins
import com.desbravando.app.ui.theme.Purple
import com.desbravando.app.ui.theme.White

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                        Home()
                    }
                }
            }
        }
    }
}

@Composable
fun Home(
    modifier: Modifier = Modifier
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
                modifier = Modifier
                    .width(15.dp)
                    .height(15.dp)
            )
            Spacer(
                modifier = Modifier.width(2.dp)
            )
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
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
                WeatherWidget()
            }
        }
    }
}

@Composable
fun WeatherWidget() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Purple
            )
            .clip(RoundedCornerShape(10.dp))
            .background(White)
            .padding(6.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_sun),
            contentDescription = stringResource(R.string.cd_climate),
            tint = Color.Yellow,
            modifier = Modifier.size(35.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = stringResource(R.string.home_temp_placeholder),
                fontFamily = Poppins,
                fontSize = 18.sp,
                color = DarkBlue,
                fontWeight = FontWeight(600)
            )
            Text(
                text = stringResource(R.string.home_weather_placeholder),
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
