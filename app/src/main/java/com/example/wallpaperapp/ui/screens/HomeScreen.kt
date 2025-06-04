package com.example.wallpaperapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.wallpaperapp.R
import com.example.wallpaperapp.data.Result
import com.example.wallpaperapp.data.Wallpaper
import com.example.wallpaperapp.viewmodel.HomeViewModel
import com.example.wallpaperapp.viewmodel.WallpaperTab
import kotlin.random.Random

@Composable
fun HomeScreen(
    onWallpaperClick: (Wallpaper) -> Unit = {}
) {
    val viewModel: HomeViewModel = viewModel()
    val featuredWallpapersState by viewModel.featuredWallpapers.collectAsState()
    val allWallpapersState by viewModel.allWallpapers.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    
    val isDarkTheme = isSystemInDarkTheme()
    val logoResId = if (isDarkTheme) R.drawable.clearlogodarkmode else R.drawable.clearlogo
    
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
        // Make everything scrollable with staggered grid including header content
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            modifier = Modifier.fillMaxSize(),
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header content as a single spanning item
            item(span = androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan.FullLine) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Logo with modern styling
                    Card(
                        modifier = Modifier.size(90.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (isDarkTheme) 0.dp else 8.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = logoResId),
                                contentDescription = "Komari Logo",
                                modifier = Modifier.size(70.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Modern greeting text
                    Text(
                        text = "Discover Amazing",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Light,
                            fontSize = 28.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "Wallpapers",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Curated collection of stunning anime wallpapers",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    // Tab Row for Featured and All tabs
                    TabRow(
                        selectedTabIndex = selectedTab.ordinal,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Tab(
                            selected = selectedTab == WallpaperTab.FEATURED,
                            onClick = { viewModel.setSelectedTab(WallpaperTab.FEATURED) },
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "Featured",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (selectedTab == WallpaperTab.FEATURED) FontWeight.Bold else FontWeight.Normal
                                ),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Tab(
                            selected = selectedTab == WallpaperTab.ALL,
                            onClick = { viewModel.setSelectedTab(WallpaperTab.ALL) },
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "All",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (selectedTab == WallpaperTab.ALL) FontWeight.Bold else FontWeight.Normal
                                ),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Tab section header with dynamic count
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (selectedTab == WallpaperTab.FEATURED) "Featured" else "All Wallpapers",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = when {
                                    selectedTab == WallpaperTab.FEATURED && featuredWallpapersState is Result.Success -> 
                                        "${(featuredWallpapersState as Result.Success<List<Wallpaper>>).data.size} wallpapers"
                                    selectedTab == WallpaperTab.ALL && allWallpapersState is Result.Success -> 
                                        "${(allWallpapersState as Result.Success<List<Wallpaper>>).data.size} wallpapers"
                                    selectedTab == WallpaperTab.FEATURED && featuredWallpapersState is Result.Loading -> "Loading..."
                                    selectedTab == WallpaperTab.ALL && allWallpapersState is Result.Loading -> "Loading..."
                                    else -> "Error loading"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
            
            // Handle different states for the selected tab
            when {
                // FEATURED TAB
                selectedTab == WallpaperTab.FEATURED && featuredWallpapersState is Result.Loading -> {
                    item(span = androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan.FullLine) {
                        LoadingState()
                    }
                }
                selectedTab == WallpaperTab.FEATURED && featuredWallpapersState is Result.Error -> {
                    item(span = androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan.FullLine) {
                        ErrorState(onRetry = { viewModel.refreshWallpapers() }, onTestConnection = { viewModel.testFirebaseConnection() })
                    }
                }
                selectedTab == WallpaperTab.FEATURED && featuredWallpapersState is Result.Success -> {
                    val wallpapers = (featuredWallpapersState as Result.Success<List<Wallpaper>>).data
                    if (wallpapers.isEmpty()) {
                        item(span = androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan.FullLine) {
                            EmptyState()
                        }
                    } else {
                        // Wallpaper items
                        items(wallpapers) { wallpaper ->
                            FirebaseWallpaperCard(
                                wallpaper = wallpaper,
                                height = (280 + Random.nextInt(100)).dp,
                                onClick = { onWallpaperClick(wallpaper) }
                            )
                        }
                    }
                }
                
                // ALL TAB
                selectedTab == WallpaperTab.ALL && allWallpapersState is Result.Loading -> {
                    item(span = androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan.FullLine) {
                        LoadingState()
                    }
                }
                selectedTab == WallpaperTab.ALL && allWallpapersState is Result.Error -> {
                    item(span = androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan.FullLine) {
                        ErrorState(onRetry = { viewModel.refreshWallpapers() }, onTestConnection = { viewModel.testFirebaseConnection() })
                    }
                }
                selectedTab == WallpaperTab.ALL && allWallpapersState is Result.Success -> {
                    val wallpapers = (allWallpapersState as Result.Success<List<Wallpaper>>).data
                    if (wallpapers.isEmpty()) {
                        item(span = androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan.FullLine) {
                            EmptyState()
                        }
                    } else {
                        // Wallpaper items
                        items(wallpapers) { wallpaper ->
                            FirebaseWallpaperCard(
                                wallpaper = wallpaper,
                                height = (280 + Random.nextInt(100)).dp,
                                onClick = { onWallpaperClick(wallpaper) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFFFF8DA1)
        )
    }
}

@Composable
fun ErrorState(onRetry: () -> Unit, onTestConnection: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "😔",
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Failed to load wallpapers",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Please check your internet connection",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry
        ) {
            Text("Try Again")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onTestConnection,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6750A4)
            )
        ) {
            Text("Test Connection")
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🖼️",
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No wallpapers found",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Please add some wallpapers from the admin dashboard",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirebaseWallpaperCard(
    wallpaper: Wallpaper,
    height: androidx.compose.ui.unit.Dp = 200.dp,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkTheme) 0.dp else 12.dp
        ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // High-quality wallpaper image with better loading
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(wallpaper.imageUrl) // Use full quality image instead of thumbnail
                    .crossfade(300)
                    .build(),
                contentDescription = wallpaper.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp)),
                placeholder = null, // We'll handle loading state manually
                error = null // We'll handle error state manually
            )
            
            // Loading/Error state overlay
            if (wallpaper.imageUrl.isEmpty()) {
                // Error state with clean background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = if (isDarkTheme) {
                                    listOf(
                                        Color(0xFF2A2A2A),
                                        Color(0xFF1A1A1A)
                                    )
                                } else {
                                    listOf(
                                        Color(0xFFF0F0F0),
                                        Color(0xFFE0E0E0)
                                    )
                                }
                            )
                        )
                ) {
                    Text(
                        text = "🖼️",
                        fontSize = 32.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            
            // Featured badge
            if (wallpaper.featured) {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .background(
                            Color(0xFFFF8DA1),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "⭐",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
} 