package com.desbravando.app

import android.R.attr.fontWeight
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desbravando.app.ui.theme.Blue
import com.desbravando.app.ui.theme.BlueSecondary
import com.desbravando.app.ui.theme.DesbravandoTheme
import com.desbravando.app.ui.theme.NavGraph
import com.desbravando.app.ui.theme.OffWhite
import com.desbravando.app.ui.theme.Poppins
import com.desbravando.app.ui.theme.Purple

class CatalogRestaurant : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {

        }
        }
    }

@Composable
fun Catalog(
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(15.dp)
    ) {
        Row() {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Logo",
                modifier = Modifier
                    .width(28.dp)
                    .height(28.dp)
                    .border(width = 1.dp, color = Blue, shape = RoundedCornerShape(4.dp))
                    .padding(5.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))

            Image(
                painter = painterResource(id = R.drawable.restaurant_icon),
                contentDescription = "Logo",
                modifier = Modifier
                    .width(25.dp)
                    .height(25.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = "GastroBar",
                fontFamily = Poppins,
                fontSize = 18.sp,
                fontWeight = FontWeight(500),
            )

        }
        Spacer(modifier = Modifier.width(6.dp))

        Row() { }
    }
}

@Preview(showBackground = true)
@Composable
fun CatalogPreview() {
    DesbravandoTheme {
        Catalog()
    }
}