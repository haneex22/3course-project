package com.example.carrentalapp.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Фирменный знак RentGo: бейдж с машиной + словесный логотип. */
@Composable
fun RentGoLogo(
    modifier: Modifier = Modifier,
    iconSize: Int = 72,
    showWordmark: Boolean = true
) {
    val green = Color(0xFF3DDC84)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(iconSize.dp)
                .clip(RoundedCornerShape((iconSize / 4).dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.DirectionsCar,
                    contentDescription = "RentGo",
                    tint = Color.White,
                    modifier = Modifier.size((iconSize * 0.5).dp)
                )
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .width((iconSize * 0.42).dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(green)
                )
            }
        }

        if (showWordmark) {
            Row {
                Text(
                    "Rent",
                    fontSize = (iconSize * 0.36).sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Go",
                    fontSize = (iconSize * 0.36).sp,
                    fontWeight = FontWeight.Black,
                    color = green
                )
            }
        }
    }
}
