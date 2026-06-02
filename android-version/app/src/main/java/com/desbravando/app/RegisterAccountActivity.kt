package com.desbravando.app

import android.app.Activity
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
import android.net.Uri
import android.os.Handler
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.desbravando.app.ui.theme.Blue
import com.desbravando.app.ui.theme.BlueCustom
import com.desbravando.app.ui.theme.DarkBlue
import com.desbravando.app.ui.theme.Gray
import com.desbravando.app.ui.theme.MediumGray
import com.desbravando.app.ui.theme.NavGraph
import com.desbravando.app.ui.theme.OffWhite
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import kotlin.contracts.contract



class RegisterAccountActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DesbravandoTheme {
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

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                ProfilePicture()
            }

            //Campo do Nome do usuário
            Text(
                text = "Nome",
                fontSize = 13.sp,
                color = BlueCustom,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
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
                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp),
                singleLine = true,
            )

            Spacer(modifier = Modifier.padding(top = 5.dp))

            //Campo do apelido do usuário
            Text(
                text = "Nome de Usuário",
                fontSize = 13.sp,
                color = BlueCustom,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
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
                fontSize = 13.sp,
                color = BlueCustom,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
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
                fontSize = 13.sp,
                color = BlueCustom,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
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
                fontSize = 13.sp,
                color = BlueCustom,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
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
            Spacer(modifier = Modifier.padding(top = 5.dp))

            //Campo da biografia do usuário
            Text(
                text = "Biografia",
                fontSize = 13.sp,
                color = BlueCustom,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 12.dp))

            OutlinedTextField(
                value = userBio,
                onValueChange = { userBio = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MediumGray,
                    focusedBorderColor = Blue,
                )
            )

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .padding(top = 20.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(),


            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colorStops = arrayOf(
                                    0.0f to Purple,
                                    1.0f to Blue
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Criar Conta",
                        fontSize = 15.sp,
                        fontFamily = Poppins,
                        color = Color.White,
                        fontWeight = FontWeight(500)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)

            ) {
                Text(
                    text = "Já tem uma conta?",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Gray,
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Conecte-se",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Blue,
                )
            }

        }

    }
}

@Composable
fun ProfilePicture(modifier: Modifier = Modifier){
    var imageUri by remember { mutableStateOf <Uri?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Box(contentAlignment = Alignment.BottomEnd){
        if (imageUri != null){
            AsyncImage(
                model = imageUri,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(125.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }else{
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(125.dp),
                tint = Color.LightGray
            )
        }


        IconButton(
            onClick = { launcher.launch("image/*")},
            modifier = Modifier
                .offset(x=(-10).dp, y = (-15).dp)
                .background(Color.Blue, CircleShape)
                .size(25.dp)

        ){
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Alterar foto",
                tint =  Color.White,
                modifier = Modifier
                    .size(15.dp)
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