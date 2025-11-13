package com.example.banknkhonde.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.banknkhonde.R
import kotlinx.coroutines.delay
import android.view.animation.OvershootInterpolator
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearOutSlowInEasing

// Helper to convert Android interpolator to Compose Easing
fun OvershootInterpolator.toComposeEasing(): Easing = Easing { t -> this.getInterpolation(t) }

@Composable
fun SplashScreen(navController: NavController) {
    val darkBlue = Color(0xFF0F1E3D)
    val vibrantBlue = Color(0xFF1E3A8A)

    val scale = remember { Animatable(0.8f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 1000, easing = OvershootInterpolator().toComposeEasing())
        )
        delay(1500)
        navController.popBackStack()
        navController.navigate("login")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(darkBlue, vibrantBlue),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo_modern),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale.value),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Bank Nkhonde",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp
            )
            Text(
                text = "Digital Savings",
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Securing Your Future, Digitally",
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}
