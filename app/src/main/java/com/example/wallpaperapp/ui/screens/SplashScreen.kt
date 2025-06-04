package com.example.wallpaperapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.wallpaperapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkTheme) Color.Black else Color.White
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = if (isDarkTheme) R.drawable.clearlogodarkmode else R.drawable.clearlogo
            ),
            contentDescription = "Komari Logo",
            modifier = Modifier.size(100.dp)
        )
    }
    
    LaunchedEffect(Unit) {
        delay(500) // Reduced to 0.5 seconds
        onSplashFinished()
    }
} 