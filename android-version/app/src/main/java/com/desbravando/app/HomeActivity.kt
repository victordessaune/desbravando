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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desbravando.app.ui.theme.DarkBlue
import com.desbravando.app.ui.theme.DesbravandoTheme
import com.desbravando.app.ui.theme.Gray
import com.desbravando.app.ui.theme.Poppins
import com.desbravando.app.ui.theme.Purple

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DesbravandoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(Color.White)
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
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 10.dp, start = 10.dp)
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
                .padding(start = 10.dp, top = 20.dp, end = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.home_welcome),
                        fontFamily = Poppins,
                        fontSize = 20.sp,
                        color = DarkBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.home_subtitle),
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = Gray,
                        fontWeight = FontWeight.Bold
                    )
                }

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

    ){
        Column(
            modifier = Modifier.padding(end = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_sun),
                contentDescription = stringResource(R.string.cd_climate),
                tint = Color.Yellow
            )

        }

        Column() {
            Row(

            ) {
                Text(
                    text = stringResource(R.string.home_temp_placeholder),
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Gray,
                    fontWeight = FontWeight.Bold
                )
            }
            Row() {
                Text(
                    text = stringResource(R.string.home_weather_placeholder),
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeActivityPreview() {
    DesbravandoTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White)
            ) {
                Home()
            }
        }
    }
}