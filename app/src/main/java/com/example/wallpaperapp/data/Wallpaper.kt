package com.example.wallpaperapp.data

import com.google.firebase.firestore.PropertyName
import com.google.firebase.Timestamp
import java.util.Date

data class Wallpaper(
    val id: String = "",
    
    // Map Firebase field names to our property names
    @get:PropertyName("post_id")
    @set:PropertyName("post_id")
    var postId: String = "",
    
    // Use file_url from Firebase as imageUrl
    @get:PropertyName("file_url")
    @set:PropertyName("file_url")
    var imageUrl: String = "",
    
    // Use preview_url from Firebase as thumbnailUrl
    @get:PropertyName("preview_url")
    @set:PropertyName("preview_url")
    var thumbnailUrl: String = "",
    
    // Title is auto-generated in your admin dashboard
    var title: String = "",
    
    // Tags array
    var tags: List<String> = emptyList(),
    
    // Dimensions
    var width: Int = 0,
    var height: Int = 0,
    
    // File size
    @get:PropertyName("file_size")
    @set:PropertyName("file_size")
    var fileSize: Long = 0,
    
    // Rating/category
    var rating: String = "",
    
    // Use is_featured from Firebase as featured
    @get:PropertyName("is_featured")
    @set:PropertyName("is_featured")
    var featured: Boolean = false,
    
    // Date added as Firestore Timestamp
    @get:PropertyName("date_added")
    @set:PropertyName("date_added")
    var dateAdded: Timestamp? = null,
    
    // Curator notes
    @get:PropertyName("curator_notes")
    @set:PropertyName("curator_notes")
    var curatorNotes: String = "",
    
    // Original source URL (Pixiv, DeviantArt, Twitter, etc.)
    var source: String = "",
    
    // Status (draft/published)
    var status: String = ""
) {
    // Computed properties for UI
    val category: String get() = rating.replaceFirstChar { it.uppercase() }
    val createdAt: Long get() = dateAdded?.toDate()?.time ?: 0L
}

// Result wrapper for Firebase operations
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
} 