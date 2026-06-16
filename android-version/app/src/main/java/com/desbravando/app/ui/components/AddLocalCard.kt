package com.desbravando.app.ui.components
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.desbravando.app.FavoritesRepository
import com.desbravando.app.R
import com.desbravando.app.Location
import com.desbravando.app.findLocations
import com.desbravando.app.ui.theme.Blue
import com.desbravando.app.ui.theme.DesbravandoTheme
import com.desbravando.app.ui.theme.Gray
import com.desbravando.app.ui.theme.LightGray
import com.desbravando.app.ui.theme.MediumGray
import com.desbravando.app.ui.theme.Poppins
import com.desbravando.app.ui.theme.Purple
import com.desbravando.app.ui.theme.White



@Composable
fun AddLocalCard(
    location: Location,
    onClick: () -> Unit = {}
) {
    var locations by remember { mutableStateOf<List<Location>>(emptyList()) }

    LaunchedEffect(Unit) {
        findLocations { list -> locations = list }
    }

    Box(
        modifier = Modifier
            .height(110.dp)
            .fillMaxWidth()
            .background(
                color = White,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {

        Row(
            modifier = Modifier.fillMaxSize()
        ) {

            AsyncImage(
                model = location.imageUrl,
                contentDescription = "Foto do lugar",
                modifier = Modifier
                    .width(130.dp)
                    .fillMaxHeight()
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            bottomStart = 16.dp
                        )
                    ),
                contentScale = ContentScale.Crop
            )


            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = location.name,
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.location_dot_solid_full),
                        contentDescription = "Visualizar",
                        tint = Purple,
                        modifier = Modifier
                            .size(16.dp)
                    )

                    Text(
                        text = location.city,
                        fontFamily = Poppins,
                        color = Purple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }


            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_plus),
                        contentDescription = "Visualizar",
                        tint = Blue,
                        modifier = Modifier
                            .padding(end = 16.dp, bottom = 16.dp)
                            .size(45.dp)
                    )
                }
            }

        }
    }
}
@Preview(showBackground = true)
@Composable
fun AddLocalCardPreview() {
    DesbravandoTheme {
        AddLocalCard(location = Location(
            imageUrl = "",
            name = "",
            city = " "
        ),
            onClick = {}
        )
    }
}