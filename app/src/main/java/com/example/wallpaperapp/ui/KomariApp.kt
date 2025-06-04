package com.example.wallpaperapp.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.wallpaperapp.R
import com.example.wallpaperapp.data.Wallpaper
import com.example.wallpaperapp.ui.screens.FavoritesScreen
import com.example.wallpaperapp.ui.screens.HomeScreen
import com.example.wallpaperapp.ui.screens.ProfileScreen
import com.example.wallpaperapp.ui.screens.SearchScreen
import com.example.wallpaperapp.ui.screens.SplashScreen
import com.example.wallpaperapp.ui.screens.WallpaperDetailScreen

sealed class Screen(val route: String, val title: String, val icon: Int) {
    object Home : Screen("home", "Home", R.drawable.ic_home)
    object Search : Screen("search", "Search", R.drawable.ic_search)
    object Favorites : Screen("favorites", "Favorites", R.drawable.ic_favorites)
    object Profile : Screen("profile", "Profile", R.drawable.ic_profile)
    object WallpaperDetail : Screen("wallpaper_detail", "Wallpaper", 0)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Search,
    Screen.Favorites,
    Screen.Profile
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KomariApp() {
    var showSplash by remember { mutableStateOf(true) }
    
    if (showSplash) {
        SplashScreen {
            showSplash = false
        }
    } else {
        MainApp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // For passing wallpaper data between screens
    // Using proper state management with remember
    var selectedWallpaper by remember { mutableStateOf<Wallpaper?>(null) }

    Scaffold(
        bottomBar = {
            // Only show bottom nav for main screens, not detail screen
            if (currentRoute != Screen.WallpaperDetail.route) {
                BottomNavBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = if (currentRoute == Screen.WallpaperDetail.route) {
                Modifier
            } else {
                Modifier.padding(innerPadding)
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onWallpaperClick = { wallpaper ->
                            println("DEBUG: Wallpaper clicked: ${wallpaper.title} - ${wallpaper.imageUrl}")
                            selectedWallpaper = wallpaper
                            navController.navigate(Screen.WallpaperDetail.route)
                        }
                    )
                }
                composable(Screen.Search.route) {
                    SearchScreen(
                        onWallpaperClick = { wallpaper ->
                            selectedWallpaper = wallpaper
                            navController.navigate(Screen.WallpaperDetail.route)
                        }
                    )
                }
                composable(Screen.Favorites.route) {
                    FavoritesScreen(
                        onWallpaperClick = { wallpaper ->
                            selectedWallpaper = wallpaper
                            navController.navigate(Screen.WallpaperDetail.route)
                        }
                    )
                }
                composable(Screen.Profile.route) {
                    ProfileScreen()
                }
                composable(Screen.WallpaperDetail.route) {
                    println("DEBUG: Detail screen opened, selectedWallpaper: ${selectedWallpaper?.title}")
                    if (selectedWallpaper != null) {
                        WallpaperDetailScreen(
                            wallpaper = selectedWallpaper!!,
                            onBackPressed = {
                                navController.popBackStack()
                            },
                            onTagClick = { tag ->
                                // Navigate to search screen with the clicked tag
                                navController.navigate(Screen.Search.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                // Note: We'll need to pass the tag to SearchScreen
                                // This requires updating SearchScreen to accept initial search terms
                            }
                        )
                    } else {
                        // Fallback UI if wallpaper is null
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "Error: Wallpaper not found",
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String?
) {
    val isDarkTheme = isSystemInDarkTheme()
    val navBarColor = if (isDarkTheme) Color(0xFF0A0A0A) else Color(0xFFE0E0E0) // Same exact grey as status bar
    
    NavigationBar(
        containerColor = navBarColor,
        tonalElevation = 0.dp // Remove elevation that can cause color shifts
    ) {
        bottomNavItems.forEach { screen ->
            val isSelected = currentRoute == screen.route
            
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = screen.title,
                        modifier = Modifier.size(26.dp) // Slightly larger icons
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                selected = isSelected,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = if (isDarkTheme) Color.White else Color(0xFF1A1A1A), // Dark color for light mode
                    selectedTextColor = if (isDarkTheme) Color.White else Color(0xFF1A1A1A), // Dark color for light mode
                    unselectedIconColor = if (isDarkTheme) Color(0xFF808080) else Color(0xFF9E9E9E), // Lighter grey for light mode
                    unselectedTextColor = if (isDarkTheme) Color(0xFF808080) else Color(0xFF9E9E9E), // Lighter grey for light mode
                    indicatorColor = if (isDarkTheme) {
                        Color(0xFFFF8DA1).copy(alpha = 0.12f) // Light pink for dark mode
                    } else {
                        Color(0xFFFF8DA1).copy(alpha = 0.15f) // Light pink for light mode
                    }
                )
            )
        }
    }
} 