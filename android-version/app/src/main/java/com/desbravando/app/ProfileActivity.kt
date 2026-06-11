package com.desbravando.app

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.desbravando.app.ui.theme.Blue
import com.desbravando.app.ui.theme.BlueCustom
import com.desbravando.app.ui.theme.BlueSecondary
import com.desbravando.app.ui.theme.DarkBlue
import com.desbravando.app.ui.theme.DarkGray
import com.desbravando.app.ui.theme.DesbravandoTheme
import com.desbravando.app.ui.theme.Gray
import com.desbravando.app.ui.theme.MediumGray
import com.desbravando.app.ui.theme.OffWhite
import com.desbravando.app.ui.theme.Poppins
import com.desbravando.app.ui.theme.Purple
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class ProfileActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
        setContent {
            DesbravandoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // CORRIGIDO: era Register(...), agora chama Profile(...)
                    Profile(
                        modifier = Modifier.padding(innerPadding),
                        onRegisterClick = { userData, password ->
                            validateData(userData, password)
                        }
                    )
                }
            }
        }
    }

    private fun validateData(userData: HashMap<String, String>, password: String) {
        // CORRIGIDO: agora valida nome e nickname além de email e senha
        when {
            userData["name"].isNullOrBlank() -> {
                Toast.makeText(baseContext, "Insira seu nome", Toast.LENGTH_SHORT).show()
            }
            userData["nickname"].isNullOrBlank() -> {
                Toast.makeText(baseContext, "Insira um nome de usuário", Toast.LENGTH_SHORT).show()
            }
            userData["email"].isNullOrBlank() -> {
                Toast.makeText(baseContext, "Insira um email", Toast.LENGTH_SHORT).show()
            }
            password.isBlank() -> {
                Toast.makeText(baseContext, "Insira uma senha", Toast.LENGTH_SHORT).show()
            }
            else -> createAccount(userData, password)
        }
    }

    private fun createAccount(userData: HashMap<String, String>, password: String) {
        auth.createUserWithEmailAndPassword(userData["email"].toString(), password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    saveData(uid, userData)
                } else {
                    val erroFirebase = task.exception?.message ?: "Erro desconhecido ao cadastrar."
                    Toast.makeText(baseContext, erroFirebase, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveData(uid: String?, userData: HashMap<String, String>) {
        db.collection("users")
            .document(uid.toString())
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(baseContext, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                auth.signOut()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(baseContext, "Erro ao salvar dados do perfil", Toast.LENGTH_SHORT).show()
            }
    }
}

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    onRegisterClick: (HashMap<String, String>, String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(color = OffWhite)
            .verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            var userName by remember { mutableStateOf("") }
            var userNickname by remember { mutableStateOf("") }
            var userEmail by remember { mutableStateOf("") }
            var userBio by remember { mutableStateOf("") }
            var userPassword by remember { mutableStateOf("") }
            var userConfirmPassword by remember { mutableStateOf("") }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        painter = painterResource(id = R.drawable.menu_ellipsis),
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = Blue
                    )
                }

                IconButton(onClick = { }) {
                    Icon(
                        painter = painterResource(id = R.drawable.heart),
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = Blue
                    )
                }
            }



            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProfilePicture()

                    Text(
                        text = "@nome",
                        fontSize = 13.sp,
                        color = Gray,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
            }

            // Nome
            Text(
                text = "Locais Favoritados",
                fontSize = 15.sp,
                color = Purple,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .padding(top = 12.dp),
                    textAlign = TextAlign.Center
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()

            ) {
                LocationCard(
                    imageUrl = "https://images.unsplash.com/photo-1483729558449-99ef09a8c325?w=400",
                    name = "Pão de Açúcar",
                    location = " Vitória - ES"
                )
                LocationCard(
                    imageUrl = "https://images.unsplash.com/photo-1483729558449-99ef09a8c325?w=400",
                    name = "Pão de Açúcar",
                    location = " Vitória - ES"
                )
                LocationCard(
                    imageUrl = "https://images.unsplash.com/photo-1483729558449-99ef09a8c325?w=400",
                    name = "Pão de Açúcar",
                    location = " Vitória - ES"
                )

            }


            Text(
                text = "Meus Roteiros",
                fontSize = 14.sp,
                color = Purple,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 12.dp)
            )

            Column(

            ) {

                    ItineraryCard(
                        imageUrl = "https://images.unsplash.com/photo-1483729558449-99ef09a8c325?w=400",
                        name = "Um dia em Vitória- ES",
                        description = "Um ótimo roteiro!",
                        location = "Vitória - ES"

                    )


            }




            // Botão Criar Conta
            Button(
                onClick = {
                    // CORRIGIDO: valida se as senhas coincidem antes de prosseguir
                    if (userPassword != userConfirmPassword) {
                        return@Button
                    }
                    val userData = hashMapOf(
                        "name" to userName,
                        "nickname" to userNickname,
                        "email" to userEmail,
                        "bio" to userBio,
                        "role" to "user"
                    )
                    onRegisterClick(userData, userPassword)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .padding(top = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
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
fun ItineraryCard(
    imageUrl: String,
    name: String,
    location: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier
            .padding(top = 10.dp)
            .width(900.dp),
    ) {
        Box (modifier = Modifier.fillMaxWidth()){
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {

                    AsyncImage(
                        model = imageUrl,
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier
                            .width(130.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 13.sp,
                            color = DarkBlue,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp),

                        )
                        Text(
                            text = location,
                            fontFamily = Poppins,
                            fontSize = 12.sp,
                            color = Gray,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth(),

                        )
                        Text(
                            text = description,
                            fontFamily = Poppins,
                            fontSize = 11.sp,
                            color = DarkGray,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth(),

                            )
                    }


                }
            Icon(
                painter = painterResource(id = R.drawable.openeye),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            )


        }

    }
}
@Composable
fun LocationCard(
    imageUrl: String,
    name: String,
    location: String
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .width(110.dp),
    ) {
        Box {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )

            Icon(
                painter = painterResource(id = R.drawable.heart),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }


        Column(modifier = Modifier.padding(4.dp)) {
            Text(
                text = name,
                fontSize = 12.sp,
                color = Blue,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = location,
                fontFamily = Poppins,
                fontSize = 11.sp,
                color = Gray,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}
@Composable
fun ProfilePicture(modifier: Modifier = Modifier) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Box(contentAlignment = Alignment.BottomEnd) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(125.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(125.dp),
                tint = Color.LightGray
            )
        }

        IconButton(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier
                .offset(x = (-10).dp, y = (-15).dp)
                .background(Blue, CircleShape)
                .size(25.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Alterar foto",
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

// CORRIGIDO: @Preview agora chama Profile(...) em vez de Register(...)
@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    DesbravandoTheme {
        Profile(onRegisterClick = { _, _ -> })
    }
}