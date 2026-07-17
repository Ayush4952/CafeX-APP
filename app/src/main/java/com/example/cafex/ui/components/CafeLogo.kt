package com.example.cafex.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.cafex.R

@Composable
fun CafeLogo(
    modifier: Modifier = Modifier,
    size: Dp = 112.dp,
) {
    Image(
        painter = painterResource(R.drawable.cafex_menu_logo),
        contentDescription = "CafeX logo",
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit,
    )
}
