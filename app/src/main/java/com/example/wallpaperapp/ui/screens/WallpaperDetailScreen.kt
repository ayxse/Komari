package com.example.wallpaperapp.ui.screens

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.wallpaperapp.data.Wallpaper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import android.graphics.Bitmap
import androidx.compose.material3.AlertDialog
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.GlobalScope
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import android.provider.MediaStore
import android.content.ContentValues
import android.graphics.BitmapFactory
import java.io.InputStream
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.max
import kotlin.math.min
import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
    
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WallpaperDetailScreen(
    wallpaper: Wallpaper,
    onBackPressed: () -> Unit,
    onTagClick: (String) -> Unit = {} // Add callback for tag clicks
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()
    var isFavorite by remember { mutableStateOf(false) }
    var isSettingWallpaper by remember { mutableStateOf(false) }
    var showInfoSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    
    // Pan and zoom state - start with wallpaper preview (crop) view
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    
    println("DEBUG: WallpaperDetailScreen opened - Title: ${wallpaper.title}")
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Full-screen wallpaper image
        if (wallpaper.imageUrl.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures(
                            panZoomLock = false
                        ) { _, pan, zoom, _ ->
                            try {
                                println("DEBUG: Gesture detected - pan: $pan, zoom: $zoom")
                                
                                // Update scale with constraints (0.5x to 3x zoom) - allow zoom out to see full image
                                val newScale = (scale * zoom).coerceIn(0.5f, 3f)
                                
                                // Only update if scale actually changed to prevent unnecessary updates
                                if (newScale != scale) {
                                    println("DEBUG: Scale changed from $scale to $newScale")
                                    scale = newScale
                                }
                                
                                // Calculate max offset based on current screen size and scale
                                val maxOffsetX = if (newScale > 1f) {
                                    (size.width.toFloat() * (newScale - 1f)) / 2f
                                } else 0f
                                
                                val maxOffsetY = if (newScale > 1f) {
                                    (size.height.toFloat() * (newScale - 1f)) / 2f
                                } else 0f
                                
                                // Update offsets with constraints
                                val newOffsetX = (offsetX + pan.x).coerceIn(-maxOffsetX, maxOffsetX)
                                val newOffsetY = (offsetY + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                                
                                // Only update if offsets actually changed
                                if (newOffsetX != offsetX) {
                                    println("DEBUG: OffsetX changed from $offsetX to $newOffsetX")
                                    offsetX = newOffsetX
                                }
                                if (newOffsetY != offsetY) {
                                    println("DEBUG: OffsetY changed from $offsetY to $newOffsetY")
                                    offsetY = newOffsetY
                                }
                                
                            } catch (e: Exception) {
                                println("DEBUG: Gesture error: ${e.message}")
                                e.printStackTrace()
                                // Reset on error to wallpaper preview
                                scale = 1f
                                offsetX = 0f
                                offsetY = 0f
                            }
                        }
                    }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(wallpaper.imageUrl)
                        .crossfade(true)
                        .size(coil.size.Size.ORIGINAL) // Load full resolution
                        .build(),
                    contentDescription = wallpaper.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        ),
                    onSuccess = { 
                        println("DEBUG: Full resolution image loaded successfully")
                    },
                    onError = { 
                        println("DEBUG: Image failed to load - ${it.result.throwable}")
                    }
                )
            }
        } else {
            // Fallback for empty URL
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🖼️\nNo Image URL",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Dark overlay for better readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )
        
        // Top bar with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackPressed,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        RoundedCornerShape(50)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Reset zoom/pan button (only show when image is transformed)
                if (scale != 1f || offsetX != 0f || offsetY != 0f) {
                    IconButton(
                        onClick = {
                            scale = 1f // Reset to wallpaper preview (crop) view
                            offsetX = 0f
                            offsetY = 0f
                        },
                        modifier = Modifier
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                RoundedCornerShape(50)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset View",
                            tint = Color.White
                        )
                    }
                }
                
                // Info button
                IconButton(
                    onClick = { showInfoSheet = true },
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            RoundedCornerShape(50)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Color.White
                    )
                }
                
                // Favorite button
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            RoundedCornerShape(50)
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFF8DA1) else Color.White
                    )
                }
            }
        }
        
        // Bottom action panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f),
                            Color.Black.copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            // Wallpaper info
            Text(
                text = wallpaper.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
            
            if (wallpaper.category.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = wallpaper.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Resolution info
            Text(
                text = "${wallpaper.width} × ${wallpaper.height}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            
            // Pan and zoom hint
            Text(
                text = "👆 Pinch to zoom • Drag to move",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Set as wallpaper button
                Button(
                    onClick = {
                        scope.launch {
                            setSimpleWallpaper(context, wallpaper.imageUrl) { success ->
                                if (success) {
                                    Toast.makeText(context, "Wallpaper set successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to set wallpaper", Toast.LENGTH_SHORT).show()
                                }
                                isSettingWallpaper = false
                            }
                        }
                        isSettingWallpaper = true
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF8DA1),
                        contentColor = Color.White
                    ),
                    enabled = !isSettingWallpaper
                ) {
                    if (isSettingWallpaper) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("🖼️ Set Wallpaper")
                    }
                }
                
                // Download button
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            downloadWallpaper(context, wallpaper)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White,
                        containerColor = Color.Transparent
                    )
                ) {
                    Text("⬇️ Download")
                }
            }
        }
        
        // Info Modal Bottom Sheet
        if (showInfoSheet) {
            ModalBottomSheet(
                onDismissRequest = { showInfoSheet = false },
                sheetState = bottomSheetState,
                containerColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color.White,
                dragHandle = {
                    // Add a visible drag handle for better UX
                    Column {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .background(
                                    color = if (isDarkTheme) Color(0xFF444444) else Color(0xFFCCCCCC),
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(16.dp)) // Add space after drag handle
                    }
                }
            ) {
                WallpaperInfoContent(
                    wallpaper = wallpaper,
                    onDismiss = { showInfoSheet = false },
                    onTagClick = onTagClick
                )
            }
        }
    }
}

// Simple wallpaper setting function
private suspend fun setSimpleWallpaper(
    context: Context,
    imageUrl: String,
    onResult: (Boolean) -> Unit
) {
    println("DEBUG: Setting wallpaper from URL: $imageUrl")
    
    try {
        withContext(Dispatchers.IO) {
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()
            
            val imageLoader = coil.ImageLoader(context)
            val result = imageLoader.execute(request)
            
            if (result.drawable != null) {
                val bitmap = (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                
                if (bitmap != null && !bitmap.isRecycled) {
                    println("DEBUG: Bitmap created successfully: ${bitmap.width}x${bitmap.height}")
                    
                    val wallpaperManager = android.app.WallpaperManager.getInstance(context)
                    wallpaperManager.setBitmap(bitmap)
                    
                    println("DEBUG: Wallpaper set successfully")
                    withContext(Dispatchers.Main) {
                        onResult(true)
                    }
                } else {
                    println("DEBUG: Failed to create valid bitmap")
                    withContext(Dispatchers.Main) {
                        onResult(false)
                    }
                }
            } else {
                println("DEBUG: Failed to load image")
                withContext(Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    } catch (e: Exception) {
        println("DEBUG: Exception: ${e.message}")
        e.printStackTrace()
        withContext(Dispatchers.Main) {
            onResult(false)
        }
    }
}

// Download wallpaper function using direct HTTP connection
private suspend fun downloadWallpaper(context: Context, wallpaper: Wallpaper) {
    withContext(Dispatchers.IO) {
        try {
            println("DEBUG: Starting gallery download for ${wallpaper.imageUrl}")
            
            val url = URL(wallpaper.imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            
            // Set headers for Gelbooru compatibility
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 11; SM-G991B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Mobile Safari/537.36")
            connection.setRequestProperty("Accept", "image/webp,image/apng,image/png,image/jpg,image/jpeg,*/*;q=0.8")
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9")
            connection.setRequestProperty("Connection", "keep-alive")
            
            if (wallpaper.imageUrl.contains("gelbooru.com")) {
                connection.setRequestProperty("Referer", "https://gelbooru.com/")
            }
            
            connection.connectTimeout = 30000 // 30 seconds
            connection.readTimeout = 60000 // 60 seconds
            connection.connect()
            
            val responseCode = connection.responseCode
            println("DEBUG: HTTP Response Code: $responseCode")
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val fileName = "${wallpaper.title.takeIf { it.isNotEmpty() } ?: "wallpaper_${wallpaper.postId}"}"
                    .replace("[^a-zA-Z0-9._-]".toRegex(), "_")
                
                val fileExtension = wallpaper.imageUrl.substringAfterLast('.').lowercase().takeIf { 
                    it in listOf("jpg", "jpeg", "png", "webp", "gif") 
                } ?: "png"
                
                val fullFileName = "${fileName}.${fileExtension}"
                
                // Use MediaStore to save to Pictures and make it appear in gallery
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fullFileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/$fileExtension")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Komari Wallpapers")
                }
                
                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                
                if (imageUri != null) {
                    val inputStream = connection.inputStream
                    val outputStream = resolver.openOutputStream(imageUri)
                    
                    if (outputStream != null) {
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var totalBytes = 0L
                        val fileSize = connection.contentLength.toLong()
                        
                        println("DEBUG: Starting gallery download, file size: $fileSize bytes")
                        
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                            totalBytes += bytesRead
                            
                            if (fileSize > 0) {
                                val progress = (totalBytes * 100 / fileSize).toInt()
                                if (totalBytes % (fileSize / 10) < 8192) { // Log every 10%
                                    println("DEBUG: Download progress: $progress% ($totalBytes / $fileSize bytes)")
                                }
                            }
                        }
                        
                        outputStream.close()
                        inputStream.close()
                        connection.disconnect()
                        
                        println("DEBUG: Gallery download completed: $totalBytes bytes")
                        
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Image saved to Photos! Check your gallery.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Failed to create image file", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to access Pictures directory", Toast.LENGTH_LONG).show()
                    }
                }
                
            } else {
                println("DEBUG: HTTP Error: $responseCode")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Download failed: HTTP $responseCode", Toast.LENGTH_LONG).show()
                }
            }
            
        } catch (e: Exception) {
            println("DEBUG: Gallery download failed: ${e.message}")
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Download failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WallpaperInfoContent(
    wallpaper: Wallpaper,
    onDismiss: () -> Unit,
    onTagClick: (String) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Wallpaper Information",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Basic Info Section
        InfoSection(
            title = "Details",
            isDarkTheme = isDarkTheme
        ) {
            InfoRow("Title", wallpaper.title.ifEmpty { "Untitled" })
            InfoRow("Resolution", "${wallpaper.width} × ${wallpaper.height}")
            InfoRow("File Size", formatFileSize(wallpaper.fileSize))
            if (wallpaper.category.isNotEmpty()) {
                InfoRow("Category", wallpaper.category)
            }
            if (wallpaper.status.isNotEmpty()) {
                InfoRow("Status", wallpaper.status.replaceFirstChar { it.uppercase() })
            }
        }
        
        if (wallpaper.dateAdded != null) {
            // Published Date Section
            InfoSection(
                title = "Published",
                isDarkTheme = isDarkTheme
            ) {
                val date = wallpaper.dateAdded!!.toDate()
                val formatter = java.text.SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", java.util.Locale.getDefault())
                InfoRow("Date", formatter.format(date))
            }
        }
        
        // Source section - link to original source (Pixiv, DeviantArt, etc.)
        if (wallpaper.source.isNotEmpty()) {
            InfoSection(
                title = "Source",
                isDarkTheme = isDarkTheme
            ) {
                val context = LocalContext.current
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Original Source",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Determine platform from URL for better display text
                    val displayText = when {
                        wallpaper.source.contains("pixiv.net") -> "🎨 View on Pixiv"
                        wallpaper.source.contains("deviantart.com") -> "🎨 View on DeviantArt"
                        wallpaper.source.contains("twitter.com") || wallpaper.source.contains("x.com") -> "🐦 View on Twitter/X"
                        wallpaper.source.contains("artstation.com") -> "🎨 View on ArtStation"
                        wallpaper.source.contains("danbooru.donmai.us") -> "🔗 View on Danbooru"
                        else -> "🔗 View Original"
                    }
                    
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wallpaper.source))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Could not open source link", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF8DA1).copy(alpha = 0.15f),
                            contentColor = Color(0xFFFF8DA1)
                        ),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = displayText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
        
        if (wallpaper.tags.isNotEmpty()) {
            // Tags Section
            InfoSection(
                title = "Tags",
                isDarkTheme = isDarkTheme
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    wallpaper.tags.forEach { tag ->
                        TagChip(tag = tag, isDarkTheme = isDarkTheme, onTagClick = onTagClick)
                    }
                }
            }
        }
        
        if (wallpaper.curatorNotes.isNotEmpty()) {
            // Curator Notes Section
            InfoSection(
                title = "Curator Notes",
                isDarkTheme = isDarkTheme
            ) {
                Text(
                    text = wallpaper.curatorNotes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Add extra spacer for better scrolling experience
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun InfoSection(
    title: String,
    isDarkTheme: Boolean,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFFF8F9FA),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun TagChip(tag: String, isDarkTheme: Boolean, onTagClick: (String) -> Unit) {
    Button(
        onClick = { onTagClick(tag) },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF8DA1).copy(alpha = 0.15f),
            contentColor = Color(0xFFFF8DA1)
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}

// Utility function to format file size
private fun formatFileSize(sizeInBytes: Long): String {
    if (sizeInBytes == 0L) return "Unknown"
    
    val units = arrayOf("B", "KB", "MB", "GB")
    var size = sizeInBytes.toDouble()
    var unitIndex = 0
    
    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }
    
    return "%.1f %s".format(size, units[unitIndex])
} 