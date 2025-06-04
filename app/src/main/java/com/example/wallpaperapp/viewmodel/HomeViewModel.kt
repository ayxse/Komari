package com.example.wallpaperapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallpaperapp.data.Result
import com.example.wallpaperapp.data.Wallpaper
import com.example.wallpaperapp.repository.WallpaperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    
    private val repository = WallpaperRepository()
    
    private val _featuredWallpapers = MutableStateFlow<Result<List<Wallpaper>>>(Result.Loading)
    val featuredWallpapers: StateFlow<Result<List<Wallpaper>>> = _featuredWallpapers.asStateFlow()
    
    private val _allWallpapers = MutableStateFlow<Result<List<Wallpaper>>>(Result.Loading)
    val allWallpapers: StateFlow<Result<List<Wallpaper>>> = _allWallpapers.asStateFlow()
    
    private val _connectionTest = MutableStateFlow<Result<Int>>(Result.Loading)
    val connectionTest: StateFlow<Result<Int>> = _connectionTest.asStateFlow()
    
    // Track the currently selected tab
    private val _selectedTab = MutableStateFlow(WallpaperTab.FEATURED)
    val selectedTab: StateFlow<WallpaperTab> = _selectedTab.asStateFlow()
    
    init {
        loadFeaturedWallpapers()
        loadAllWallpapers()
    }
    
    fun loadFeaturedWallpapers() {
        viewModelScope.launch {
            repository.getFeaturedWallpapers().collect { result ->
                _featuredWallpapers.value = result
            }
        }
    }
    
    fun loadAllWallpapers() {
        viewModelScope.launch {
            repository.getAllWallpapers().collect { result ->
                _allWallpapers.value = result
            }
        }
    }
    
    fun refreshWallpapers() {
        loadFeaturedWallpapers()
        loadAllWallpapers()
    }
    
    fun setSelectedTab(tab: WallpaperTab) {
        _selectedTab.value = tab
    }
    
    fun testFirebaseConnection() {
        viewModelScope.launch {
            repository.testConnection().collect { result ->
                _connectionTest.value = result
            }
        }
    }
}

enum class WallpaperTab {
    FEATURED, ALL
} 