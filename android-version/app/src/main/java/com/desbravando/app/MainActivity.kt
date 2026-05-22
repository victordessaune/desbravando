package com.desbravando.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desbravando.app.ui.theme.BlueSecondary
import com.desbravando.app.ui.theme.DesbravandoTheme
import com.desbravando.app.ui.theme.LightPurple
import com.desbravando.app.ui.theme.Purple
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.desbravando.app.ui.theme.Poppins
import androidx.compose.material3.ButtonDefaults


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
            )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f),
            color = Color.Transparent
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.white_logo_desbravando),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .width(150.dp)
                        .height(60.dp)
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize()

            ) {
                var email by remember { mutableStateOf("")}
                var password by remember { mutableStateOf("")}

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = "Login", fontFamily = Poppins, fontSize = 22.sp, color = Purple, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "Usuário", fontFamily = Poppins, fontSize = 22.sp, color = BlueSecondary, fontWeight = FontWeight.Bold)

                }
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Text(text = "Email", fontSize = 18.sp, fontWeight = FontWeight.Light)

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("você@gmail.com") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                )
                Spacer(modifier = Modifier.padding(top = 5.dp))

                Text(
                    text = "Senha",
                    fontSize = 18.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(top = 12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("******") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    shape = RoundedCornerShape(20.dp),
                )
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(82.dp)
                        .padding(top = 25.dp, bottom = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple
                    )
                ) {
                    Text(text = "Entrar",
                        fontSize = 18.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(top = 12.dp, bottom = 10.dp),
                    border = BorderStroke(1.dp, Purple),
                    colors = ButtonDefaults.buttonColors(
                        Color.Transparent
                    )
                ) {
                    Text(text = "Criar Conta",
                        fontSize = 18.sp,
                        fontFamily = Poppins,
                        color = Purple,
                        fontWeight = FontWeight.SemiBold)
                }

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
