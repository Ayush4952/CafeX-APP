package com.example.cafex.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cafex.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    sessionReady: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var animateIn by remember { mutableStateOf(false) }
    var navigationStarted by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.78f,
        animationSpec = tween(700),
        label = "logoScale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(650),
        label = "logoAlpha",
    )

    LaunchedEffect(Unit) {
        animateIn = true
    }

    LaunchedEffect(sessionReady) {
        if (sessionReady && !navigationStarted) {
            navigationStarted = true
            delay(1_350)
            onFinished()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF2D190F), Color(0xFF60402D)),
                ),
            ),
    ) {
        Image(
            painter = painterResource(R.drawable.cafe_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.17f),
            contentScale = ContentScale.Crop,
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.cafex_logo),
                contentDescription = "CafeX logo",
                modifier = Modifier
                    .size(174.dp)
                    .scale(scale)
                    .alpha(alpha),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Fresh coffee, better moments",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alpha),
            )
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(color = Color(0xFFE8C9A7))
        }
    }
}
