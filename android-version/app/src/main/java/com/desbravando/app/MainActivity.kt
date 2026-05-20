package com.desbravando.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.desbravando.app.ui.theme.Pink40
import com.desbravando.app.ui.theme.Purple40
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.desbravando.app.ui.theme.DesbravandoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DesbravandoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Login(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
@Composable
fun Login(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Pink40),
        verticalArrangement = Arrangement.Bottom,
    ) {
        Text(
            text = "Desbravando",
            color = Color.White,
        )
        Image(
            painter = painterResource(id = R.drawable.white_logo_desbravando),
            contentDescription = "Logo",
            modifier = Modifier
                .width(180.dp)
                .height(60.dp)
        )

        Surface(
            modifier = Modifier.size(500.dp),
            shape = RoundedCornerShape(
                topStart = 32.dp,
                topEnd = 32.dp
            ),
            color = Color.White
        ) {
            Text(
                text = "Login",
                modifier = Modifier
                    .padding(
                        top = 32.dp,
                        start = 24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    DesbravandoTheme {
        Login()
    }
}
