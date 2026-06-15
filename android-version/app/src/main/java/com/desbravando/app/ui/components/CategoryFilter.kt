package com.desbravando.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desbravando.app.R
import com.desbravando.app.ui.theme.DarkBlue
import com.desbravando.app.ui.theme.DesbravandoTheme
import com.desbravando.app.ui.theme.LightGray
import com.desbravando.app.ui.theme.Poppins
import com.desbravando.app.ui.theme.Purple
import com.desbravando.app.ui.theme.White

data class FilterOption(
    val label: String,
    val icon: Int
)

@Composable
fun CategoryCard(
        selectedTags: Set<String> = emptySet(),
        onTagSelected: (String) -> Unit = {},
) {

    val options = listOf(
        FilterOption("Todos", R.drawable.ic_all),
        FilterOption("Praias", R.drawable.ic_beach),
        FilterOption("Parques", R.drawable.ic_tree),
        FilterOption("Religioso", R.drawable.ic_culture),
        FilterOption("GastroBar", R.drawable.ic_utensils),
        FilterOption("Eco", R.drawable.ic_mountain),
        FilterOption("Histórico", R.drawable.ic_landmark)
    )
    var selected by remember { mutableStateOf("Todos") }


    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        options.forEach { option ->

            items(options) { option ->
                val isSelected = option.label == selected

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(40))
                        .background(if (isSelected) Purple else White)
                        .clickable { selected = option.label }
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
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
}
@Preview(showBackground = true)
@Composable
fun CategoryFilterPreview() {
    DesbravandoTheme {
        CategoryCard()
    }
}