package com.example.wallpaperapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wallpaperapp.data.Wallpaper
import kotlin.random.Random

@Composable
fun FavoritesScreen(
    onWallpaperClick: (Wallpaper) -> Unit = {}
) {
    // Simulated favorites state - in real app this would come from a database or state management
    var favorites by remember { mutableStateOf(emptyList<Wallpaper>()) }
    val isDarkTheme = isSystemInDarkTheme()
    
    // Modern gradient background
    val backgroundGradient = if (isDarkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF000000),
                Color(0xFF0A0A0A),
                Color(0xFF000000)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF8F9FA),
                Color(0xFFFFFFFF),
                Color(0xFFF1F3F4)
            )
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Modern header
            Text(
                text = "Favorites",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            
            if (favorites.isNotEmpty()) {
                Text(
                    text = "Your saved wallpapers",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Favorites count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Saved",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${favorites.size} wallpapers",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Favorites grid
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalItemSpacing = 16.dp,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favorites) { wallpaper ->
                        FirebaseWallpaperCard(
                            wallpaper = wallpaper,
                            height = (180 + Random.nextInt(80)).dp,
                            onClick = { onWallpaperClick(wallpaper) }
                        )
                    }
                }
            } else {
                // Modern empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        // Large heart icon
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(
                                    color = Color(0xFFFF8DA1).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(60.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Empty favorites",
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFFFF8DA1)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "No Favorites Yet",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Start exploring and save your favorite wallpapers to see them here",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                lineHeight = 24.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Call to action button
                        Button(
                            onClick = {
                                // Just navigate to explore - no demo data needed
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF8DA1),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(28.dp),
                            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 24.dp)
                        ) {
                            Text(
                                text = "Explore",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                ),
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Demo button (temporary) with sample wallpapers
                        Button(
                            onClick = {
                                favorites = listOf(
                                    Wallpaper(
                                        id = "demo1",
                                        title = "Sample Wallpaper 1",
                                        imageUrl = "",
                                        rating = "demo",
                                        featured = true
                                    ),
                                    Wallpaper(
                                        id = "demo2",
                                        title = "Sample Wallpaper 2", 
                                        imageUrl = "",
                                        rating = "demo",
                                        featured = false
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFF6750A4),
                                contentColor = if (isDarkTheme) Color.White else Color.White
                            ),
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp)
                        ) {
                            Text(
                                text = "Add Sample Favorites",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
} 