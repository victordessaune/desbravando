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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import coil.compose.AsyncImage
import com.desbravando.app.ui.components.BottomBar
import com.desbravando.app.ui.theme.Blue
import com.desbravando.app.ui.theme.BlueCustom
import com.desbravando.app.ui.theme.BlueSecondary
import com.desbravando.app.ui.theme.DarkBlue
import com.desbravando.app.ui.theme.DarkGray
import com.desbravando.app.ui.theme.DesbravandoTheme
import com.desbravando.app.ui.theme.Gray
import com.desbravando.app.ui.theme.LightGray
import com.desbravando.app.ui.theme.MediumGray
import com.desbravando.app.ui.theme.OffWhite
import com.desbravando.app.ui.theme.Poppins
import com.desbravando.app.ui.theme.Purple
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class CreateItinerary : ComponentActivity() {

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
                    Itinerary(
                        modifier = Modifier.padding(innerPadding),

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
fun Itinerary(modifier: Modifier = Modifier) {
    var currentStep by remember { mutableStateOf(1) }

    when (currentStep) {
        1 -> DefineRouteStep(
            modifier = modifier,
            onNext = { currentStep = 2 }
        )
        2 -> AddLocalsStep(
            modifier = modifier,
            onNext = { currentStep = 3 },
            onBack = { currentStep = 1 }
        )
        3 -> OrganizeLocalsStep(
            modifier = modifier,
            onNext = { currentStep = 4 },
            onBack = { currentStep = 2}
        )
        4 -> ViewItineraryStep(
            modifier = modifier,
            onNext = { currentStep = 4 },
            onBack = { currentStep = 2}
        )


    }
}

// ─── PASSO 1 ────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DefineRouteStep(
    modifier: Modifier = Modifier,
    onNext: () -> Unit
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
                Text(
                    text = "Novo Roteiro",
                    fontSize = 14.sp,
                    color = Blue,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(top = 1.dp)
                )


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
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .padding(top = 20.dp)
                        .fillMaxWidth()
                ) {

                    Text(
                        text = "Como será sua viagem?",
                        fontSize = 18.sp,
                        color = DarkBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                    Text(
                        text = "Defina alguns detalhes para o seu roteiro",
                        fontSize = 12.sp,
                        color = Gray,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins,

                        )
                }
            }


            Row(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .padding(bottom = 10.dp)
                    .padding(top = 25.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "Quantos dias?",
                    fontSize = 12.sp,
                    color = DarkBlue,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,

                    )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()

            ) {

                DayCard()
            }

            Row(

                modifier = Modifier
                    .padding(start = 5.dp)
                    .padding(bottom = 10.dp)
                    .padding(top = 25.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "Com quem você vai?",
                    fontSize = 12.sp,
                    color = DarkBlue,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,

                    )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()

            ) {

                CompanyCard()
            }
            Row(

                    modifier = Modifier
                        .padding(start = 5.dp)
                        .padding(bottom = 10.dp)
                        .padding(top = 25.dp)
                        .fillMaxWidth(),
            ) {
            Text(
                text = "Quais são os seus interesses?",
                fontSize = 12.sp,
                color = DarkBlue,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,

                )
        }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()

            ) {

                InterestsCard()
            }

            Row(

                modifier = Modifier
                    .padding(start = 5.dp)
                    .padding(bottom = 10.dp)
                    .padding(top = 25.dp)
                    .fillMaxWidth(),
            ){
                Text(
                    text = "Estilo da viagem",
                    fontSize = 12.sp,
                    color = DarkBlue,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,

                    )
            }

            Row( horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()

            ) {

                MoodCard()
            }
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp)
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
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
                        text = "Continuar",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins
                    )
                }
            }


        }

    }

}
//─── PASSO 2 ────────────────────────────────────────────────

@Composable
fun AddLocalsStep(
    modifier: Modifier = Modifier,
    onNext: () -> Unit,
    onBack: () -> Unit
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
                Text(
                    text = "Adicionar Locais",
                    fontSize = 14.sp,
                    color = Blue,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(top = 1.dp)
                )


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

                CategoryCard()
            }

            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .padding(top = 20.dp)
                        .fillMaxWidth()
                ) {

                    Text(
                        text = "Sugestões para você",
                        fontSize = 16.sp,
                        color = DarkBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                    Text(
                        text = "Selecionamos lugares perfeitos para o seu roteiro!",
                        fontSize = 12.sp,
                        color = Gray,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins,

                        )
                }
            }

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp)
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
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
                        text = "Continuar",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins
                    )
                }
            }


        }

    }
}

//─── PASSO 3 ────────────────────────────────────────────────
@Composable
fun OrganizeLocalsStep(
    modifier: Modifier = Modifier,
    onNext: () -> Unit,
    onBack: () -> Unit
) {}
//─── PASSO 4 ────────────────────────────────────────────────
@Composable
fun ViewItineraryStep(
    modifier: Modifier = Modifier,
    onNext: () -> Unit,
    onBack: () -> Unit
) {}
//─── Cards ────────────────────────────────────────────────
@Composable
fun DayCard() {
    val options = listOf("1 dia", "2 dias", "3 dias", "+ de 3")
    var selected by remember { mutableStateOf("") }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        options.forEach { option ->

            val isSelected = option == selected

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(40))
                    .background(if (isSelected) Purple else LightGray)
                    .clickable{ selected = option }
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ){
                Text(
                    text = option,
                    fontSize = 12.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color.White else DarkBlue // branco se selecionado, azul se não
                )
            }


        }

    }
}
data class FilterOption(
    val label: String,
    val icon: Int
)

@Composable
fun CategoryCard() {
    val options = listOf("Todos", "Praias", "Parques", "Religioso", "GastroBar", "Eco", "Histórico")
    var selected by remember { mutableStateOf("") }


    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        options.forEach { option ->

            items(options) { option ->
                val isSelected = option == selected

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(40))
                        .background(if (isSelected) Purple else LightGray)
                        .clickable { selected = option }
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = option,
                        fontSize = 12.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else DarkBlue // branco se selecionado, azul se não
                    )
                }


            }
        }

    }
}
@Composable
fun CompanyCard() {
    val options = listOf(
        FilterOption("Sozinho", R.drawable.ic_alone),
        FilterOption("Casal", R.drawable.ic_couple),
        FilterOption("Família", R.drawable.ic_family),
        FilterOption("Amigos", R.drawable.ic_friends)
    )
    var selected by remember { mutableStateOf("") }


    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

        options.forEach { option ->

            val isSelected = option.label == selected

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(20))
                    .background(if (isSelected) Purple else LightGray)
                    .clickable{ selected = option.label }
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                        Icon(
                            painter = painterResource(id = option.icon),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(22.dp),
                            tint = if (isSelected) Color.White else DarkBlue
                        )

                    Text(
                        text = option.label,
                        fontSize = 12.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else DarkBlue,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }


        }

    }
}
@Composable
fun MoodCard() {
    val options = listOf(
        FilterOption("Relaxado", R.drawable.ic_relax),
        FilterOption("Equilibrado", R.drawable.ic_compass),
        FilterOption("Aventura", R.drawable.ic_mountain)
    )
    var selected by remember { mutableStateOf("") }


    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

        options.forEach { option ->

            val isSelected = option.label == selected

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(20))
                    .background(if (isSelected) Purple else LightGray)
                    .clickable{ selected = option.label }
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = option.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(20.dp),
                        tint = if (isSelected) Color.White else DarkBlue
                    )

                    Text(
                        text = option.label,
                        fontSize = 12.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else DarkBlue,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }


        }

    }

}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestsCard() {
    val options = listOf(
        FilterOption("Praias", R.drawable.ic_beach),
        FilterOption("Cultura", R.drawable.ic_culture),
        FilterOption("GastroBar", R.drawable.ic_utensils),
        FilterOption("História", R.drawable.ic_landmark),
        FilterOption("Natureza", R.drawable.ic_tree)
    )
    var selected by remember { mutableStateOf(setOf<String>()) }


    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        options.forEach { option ->

            val isSelected = option.label in selected

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(20))
                    .background(if (isSelected) Purple else LightGray)
                    .clickable{
                        selected = if (option.label in selected)
                            selected - option.label
                        else
                            selected + option.label }
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = option.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(22.dp),
                        tint = if (isSelected) Color.White else DarkBlue
                    )

                    Text(
                        text = option.label,
                        fontSize = 12.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else DarkBlue,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }


        }

    }
}





@Preview(showBackground = true)
@Composable
fun Step1Preview() {
    DesbravandoTheme {
        DefineRouteStep(onNext = {})
    }
}

@Preview(showBackground = true)
@Composable
fun Step2Preview() {
    DesbravandoTheme {
        AddLocalsStep(onNext = {}, onBack = {})
    }
}
