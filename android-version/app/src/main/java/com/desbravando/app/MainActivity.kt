package com.desbravando.app

import android.app.Activity
import android.content.Intent
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.desbravando.app.ui.theme.Blue
import com.desbravando.app.ui.theme.DarkGray
import com.desbravando.app.ui.theme.Gray
import com.desbravando.app.ui.theme.LightGray
import com.desbravando.app.ui.theme.MediumGray
import com.desbravando.app.ui.theme.NavGraph
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
        setContent {
            DesbravandoTheme {
                NavGraph()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Login(modifier = Modifier.padding(innerPadding))
                }
            }

        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            TODO("ENVIAR PARA A TELA HOME")
        }
    }

    private fun validateData(email: String, password: String) {

    }
}
@Composable
fun Login(
    modifier: Modifier = Modifier,
    onNavigateToRegister:() -> Unit ={}
) {
    val context = LocalContext.current
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
                        .padding(top = 30.dp)

                ) {
                    Text(
                        text = "Login",
                        fontFamily = Poppins,
                        fontSize = 22.sp,
                        color = Blue,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(3.5.dp))

                    Text(
                        text = "Usuário",
                        fontFamily = Poppins,
                        fontSize = 22.sp,
                        color = BlueSecondary,
                        fontWeight = FontWeight.Bold
                    )

                }
                Spacer(modifier = Modifier.padding(top = 25.dp))
                Text(
                    text = "Email",
                    color = Blue,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight(510))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text(
                            text = "você@gmail.com",
                            fontSize = 13.sp)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MediumGray,
                        focusedBorderColor = Blue,
                    ),

                    singleLine = true,
                )
                Spacer(modifier = Modifier.padding(top = 5.dp))

                Text(
                    text = "Senha",
                    fontSize = 14.sp,
                    color = Blue,
                    fontFamily = Poppins,
                    fontWeight = FontWeight(510),
                    modifier = Modifier.padding(top = 12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("******") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MediumGray,
                        focusedBorderColor = Blue,
                    ),
                )
                Spacer(modifier = Modifier.padding(top = 5.dp))

                Text(
                    text = "Esqueceu a senha?",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Right,
                    color = DarkGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 10.dp, top = 2.dp)
                )
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(top = 12.dp, bottom = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple
                    )
                ) {
                    Text(text = "Continuar",
                        fontSize = 15.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight(500))
                }
                Button(
                    onClick = {onNavigateToRegister()},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(top = 12.dp, bottom = 10.dp),
                    border = BorderStroke(1.dp, Purple),
                    colors = ButtonDefaults.buttonColors(
                        Color.Transparent
                    )
                ) {
                    Text(text = "Criar Conta",
                        fontSize = 15.sp,
                        fontFamily = Poppins,
                        color = Purple,
                        fontWeight = FontWeight(500))
                }
                Text(
                    text = "Termos de Uso | Políticas de Privacidade",
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    color = DarkGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
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
