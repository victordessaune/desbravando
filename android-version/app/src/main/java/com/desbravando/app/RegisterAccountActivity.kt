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
import android.service.autofill.UserData
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlin.contracts.contract



class RegisterAccountActivity : ComponentActivity(){

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
        setContent {
            DesbravandoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Register(
                        modifier = Modifier.padding(innerPadding),
                        onRegisterClick = { userData, password ->
                            validateData(userData, password)
                        }
                    )
                }
            }
        }
    }

    private fun validateData(userData: HashMap<String, String>, password: String){

        if (userData["email"].toString().isNotBlank()){
            if (password.isNotBlank()){
                createAccount(userData, password)
            } else {
                Toast.makeText(
                    baseContext,
                    getString(R.string.message_alert_password),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        } else {
            Toast.makeText(
                baseContext,
                getString(R.string.message_alert_email),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun createAccount(userData: HashMap<String, String>, password: String) {
        auth.createUserWithEmailAndPassword(userData["email"].toString(), password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid
                    saveData(uid, userData)
                    // O método saveData já vai se encarregar de fazer o signOut() e o finish()
                    // para voltar o usuário para a tela de login após salvar tudo!
                } else {
                    // Pega a mensagem de erro real enviada pelo Firebase
                    val erroFirebase = task.exception?.message ?: getString(R.string.message_error_signup)

                    // Exibe o erro real na tela (ex: "The email address is already in use...")
                    Toast.makeText(
                        baseContext,
                        erroFirebase,
                        Toast.LENGTH_LONG // Mudado para LONG para dar tempo de ler o erro completo
                    ).show()
                }
            }
    }

    private fun saveData(uid: String?, userData: HashMap<String, String>) {
        db.collection("users")
            .document(uid.toString())
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(
                    baseContext,
                    getString(R.string.message_succes_signup),
                    Toast.LENGTH_SHORT,
                ).show()

                // Desconecta o usuário recém-criado para que ele precise fazer login manualmente
                auth.signOut()

                // FECHA a tela de registro e volta automaticamente para a tela de Login (MainActivity)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    baseContext,
                    getString(R.string.message_error_save_data),
                    Toast.LENGTH_SHORT,
                ).show()
            }
    }
}

@Composable
fun Register(
    modifier: Modifier = Modifier,
    onRegisterClick: (HashMap<String, String>, String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(color = OffWhite)
            .verticalScroll(rememberScrollState())
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
            var userPhotoUrl by remember { mutableStateOf("") }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)

            ) {
                Text(
                    text = stringResource(R.string.title_report),
                    fontFamily = Poppins,
                    fontSize = 22.sp,
                    color = Blue,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = stringResource(R.string.title_your_data),
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
                ProfileRegisterPicture(
                    onPhotoUploaded = { url -> userPhotoUrl = url }
                )
            }

            //Campo do Nome do usuário
            Text(
                text = stringResource(R.string.text_name),
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
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MediumGray,
                    focusedBorderColor = Blue,
                    focusedTextColor = Blue,
                    unfocusedTextColor = MediumGray,
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                singleLine = true,
            )

            Spacer(modifier = Modifier.padding(top = 5.dp))

            //Campo do apelido do usuário
            Text(
                text = stringResource(R.string.text_username),
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
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MediumGray,
                    focusedBorderColor = Blue,
                    focusedTextColor = Blue,
                    unfocusedTextColor = MediumGray,
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                singleLine = true,
            )

            Spacer(modifier = Modifier.padding(top = 5.dp))

            //Campo do email do usuário
            Text(
                text = stringResource(R.string.text_email),
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
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MediumGray,
                    focusedBorderColor = Blue,
                    focusedTextColor = Blue,
                    unfocusedTextColor = MediumGray,
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),

                singleLine = true,
            )

            Spacer(modifier = Modifier.padding(top = 5.dp))

            //Campo da senha do usuário
            Text(
                text = stringResource(R.string.text_password),
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
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MediumGray,
                    focusedBorderColor = Blue,
                    focusedTextColor = Blue,
                    unfocusedTextColor = MediumGray,
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),

                singleLine = true,
            )

            Spacer(modifier = Modifier.padding(top = 5.dp))

            //Campo de confirmação de senha do usuário
            Text(
                text = stringResource(R.string.text_confirm_password),
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
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MediumGray,
                    focusedBorderColor = Blue,
                    focusedTextColor = Blue,
                    unfocusedTextColor = MediumGray,
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),

                singleLine = true,
            )
            Spacer(modifier = Modifier.padding(top = 5.dp))

            //Campo da biografia do usuário
            Text(
                text = stringResource(R.string.text_biography),
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
                    focusedTextColor = Blue,
                    unfocusedTextColor = MediumGray,
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),

                singleLine = true,
            )

            Button(
                onClick = {
                    val userData = hashMapOf(
                        "name" to userName,
                        "nickname" to userNickname,
                        "email" to userEmail,
                        "bio" to userBio,
                        "fotoUrl" to userPhotoUrl,
                        "role" to "user"
                    )

                    onRegisterClick(userData, userPassword)

                },
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
                        text = stringResource(R.string.text_create_account),
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
                    text = stringResource(R.string.text_already_have_account),
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Gray,
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = stringResource(R.string.text_connect),
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Blue,
                )
            }

        }

    }
}

@Composable
fun ProfileRegisterPicture(
    onPhotoUploaded: (String) -> Unit = {},
    modifier: Modifier = Modifier,
){
    var imageUri by remember { mutableStateOf <Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            isUploading = true
            uploadImageToCloudinary(context, uri) { url ->
                isUploading = false
                if (url != null) {
                    onPhotoUploaded(url)
                } else {
                    Toast.makeText(context, "Erro ao enviar foto", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Box(contentAlignment = Alignment.BottomEnd){
        if (imageUri != null){
            AsyncImage(
                model = imageUri,
                contentDescription = stringResource(R.string.cd_profile_picture),
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

        if (isUploading) {
            Box(
                modifier = Modifier
                    .size(125.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
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
                contentDescription = stringResource(R.string.cd_change_profile_picture),
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
        Register(onRegisterClick = { _, _ -> })
    }
}