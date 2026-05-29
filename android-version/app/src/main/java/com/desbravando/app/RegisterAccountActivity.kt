package com.desbravando.app

import android.os.Bundle
import android.os.Looper
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
import android.content.Intent
import android.os.Handler
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.desbravando.app.ui.theme.Blue
import com.desbravando.app.ui.theme.BlueCustom
import com.desbravando.app.ui.theme.DarkBlue
import com.desbravando.app.ui.theme.MediumGray
import com.desbravando.app.ui.theme.NavGraph
import com.desbravando.app.ui.theme.OffWhite


class RegisterAccountActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DesbravandoTheme {
                NavGraph()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Register(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Register(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(color = OffWhite)
    ){
        Column(
            modifier = Modifier
                .padding(24.dp)
        ) {
            var userName by remember { mutableStateOf("")}
            var userNickname by remember { mutableStateOf("")}
            var userEmail by remember { mutableStateOf("")}
            var userBio by remember { mutableStateOf("")}
            var userPassword by remember { mutableStateOf("")}
            var userConfirmPassword by remember { mutableStateOf("")}

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)

            ) {
                Text(
                    text = "Informe",
                    fontFamily = Poppins,
                    fontSize = 22.sp,
                    color = Blue,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "seus Dados",
                    fontFamily = Poppins,
                    fontSize = 22.sp,
                    color = BlueSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.padding(top = 5.dp))

            //Campo do Nome do usuário
            Text(
                text = "Nome",
                fontSize = 14.sp,
                color = BlueCustom,
                fontFamily = Poppins,
                fontWeight = FontWeight(500),
                modifier = Modifier.padding(top = 12.dp))

            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MediumGray,
                    focusedBorderColor = Blue,
                ),

                singleLine = true,
            )

            Spacer(modifier = Modifier.padding(top = 5.dp))

            //Campo do apelido do usuário
            Text(
                text = "Nome de Usuário",
                fontSize = 14.sp,
                color = BlueCustom,
                fontFamily = Poppins,
                fontWeight = FontWeight(500),
                modifier = Modifier.padding(top = 12.dp))

            OutlinedTextField(
                value = userNickname,
                onValueChange = { userNickname = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MediumGray,
                    focusedBorderColor = Blue,
                ),

                singleLine = true,
            )

            Spacer(modifier = Modifier.padding(top = 5.dp))

            //Campo do email do usuário
            Text(
                text = "Email",
                fontSize = 14.sp,
                color = BlueCustom,
                fontFamily = Poppins,
                fontWeight = FontWeight(500),
                modifier = Modifier.padding(top = 12.dp))

            OutlinedTextField(
                value = userEmail,
                onValueChange = { userEmail = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MediumGray,
                    focusedBorderColor = Blue,
                ),

                singleLine = true,
            )

            Spacer(modifier = Modifier.padding(top = 5.dp))

            //Campo da senha do usuário
            Text(
                text = "Senha",
                fontSize = 14.sp,
                color = BlueCustom,
                fontFamily = Poppins,
                fontWeight = FontWeight(500),
                modifier = Modifier.padding(top = 12.dp))

            OutlinedTextField(
                value = userPassword,
                onValueChange = { userPassword = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MediumGray,
                    focusedBorderColor = Blue,
                ),

                singleLine = true,
            )

            Spacer(modifier = Modifier.padding(top = 5.dp))

            //Campo de confirmação de senha do usuário
            Text(
                text = "Confirme sua senha",
                fontSize = 14.sp,
                color = BlueCustom,
                fontFamily = Poppins,
                fontWeight = FontWeight(500),
                modifier = Modifier.padding(top = 12.dp))

            OutlinedTextField(
                value = userConfirmPassword,
                onValueChange = { userConfirmPassword = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MediumGray,
                    focusedBorderColor = Blue,
                ),

                singleLine = true,
            )



        }

    }
}


@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    DesbravandoTheme {
        Register()
    }
}