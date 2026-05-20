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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desbravando.app.ui.theme.BlueSecondary
import com.desbravando.app.ui.theme.DesbravandoTheme
import com.desbravando.app.ui.theme.LightPurple
import com.desbravando.app.ui.theme.Purple

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
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Purple,
                        0.48f to BlueSecondary
                    )
                )
            ),

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Image(
                painter = painterResource(id = R.drawable.white_logo_desbravando),
                contentDescription = "Logo",
                modifier = Modifier
                    .width(120.dp)
                    .height(60.dp)

            )
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            shape = RoundedCornerShape(
                topStart = 32.dp,
                topEnd = 32.dp
            ),
            color = Color.White
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp),

            ) {
                Text(
                    text = "Login",
                    fontSize = 18.sp,
                    color = Purple,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Spacer(
                    modifier = Modifier.width(10.dp)
                )
                Text(
                    text = "Usuário",
                    fontSize = 18.sp,
                    color = BlueSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
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
