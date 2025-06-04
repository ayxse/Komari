package com.example.wallpaperapp.repository

import android.util.Log
import com.example.wallpaperapp.data.Result
import com.example.wallpaperapp.data.Wallpaper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallpaperRepository @Inject constructor() {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val wallpapersCollection = firestore.collection("curated_wallpapers")
    
    companion object {
        private const val TAG = "WallpaperRepository"
    }
    
    fun getFeaturedWallpapers(): Flow<Result<List<Wallpaper>>> = flow {
        try {
            Log.d(TAG, "Starting to fetch featured wallpapers...")
            emit(Result.Loading)
            
            // Query for wallpapers that are both published and featured
            val query = wallpapersCollection
                .whereEqualTo("status", "published")
                .whereEqualTo("is_featured", true)
                .orderBy("date_added", Query.Direction.DESCENDING)
                .limit(50)
            
            Log.d(TAG, "Executing Firebase query for published & featured wallpapers...")
            val snapshot = query.get().await()
            Log.d(TAG, "Query completed. Document count: ${snapshot.documents.size}")
            
            if (snapshot.isEmpty) {
                Log.d(TAG, "No published featured wallpapers found")
                
                // Fallback: try to get any published wallpapers
                Log.d(TAG, "Trying fallback query for any published wallpapers...")
                val fallbackQuery = wallpapersCollection
                    .whereEqualTo("status", "published")
                    .orderBy("date_added", Query.Direction.DESCENDING)
                    .limit(10)
                
                val fallbackSnapshot = fallbackQuery.get().await()
                Log.d(TAG, "Fallback query completed. Document count: ${fallbackSnapshot.documents.size}")
                
                val wallpapers = fallbackSnapshot.documents.mapNotNull { document ->
                    try {
                        Log.d(TAG, "Processing document: ${document.id}")
                        Log.d(TAG, "Document data: ${document.data}")
                        val wallpaper = document.toObject(Wallpaper::class.java)?.copy(id = document.id)
                        Log.d(TAG, "Wallpaper: ${wallpaper?.title} - Status: ${wallpaper?.status} - Featured: ${wallpaper?.featured}")
                        wallpaper
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing document ${document.id}: ${e.message}")
                        null
                    }
                }
                
                emit(Result.Success(wallpapers))
                return@flow
            }
            
            val wallpapers = snapshot.documents.mapNotNull { document ->
                try {
                    Log.d(TAG, "Processing document: ${document.id}")
                    Log.d(TAG, "Document data: ${document.data}")
                    val wallpaper = document.toObject(Wallpaper::class.java)?.copy(id = document.id)
                    Log.d(TAG, "Wallpaper: ${wallpaper?.title} - Status: ${wallpaper?.status} - Featured: ${wallpaper?.featured}")
                    wallpaper
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing document ${document.id}: ${e.message}")
                    null
                }
            }
            
            Log.d(TAG, "Successfully processed ${wallpapers.size} wallpapers")
            emit(Result.Success(wallpapers))
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching featured wallpapers: ${e.message}", e)
            emit(Result.Error(e))
        }
    }
    
    fun getAllWallpapers(): Flow<Result<List<Wallpaper>>> = flow {
        try {
            emit(Result.Loading)
            
            val query = wallpapersCollection
                .whereEqualTo("status", "published")
                .orderBy("date_added", Query.Direction.DESCENDING)
                .limit(100)
            
            val snapshot = query.get().await()
            val wallpapers = snapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Wallpaper::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }
            
            emit(Result.Success(wallpapers))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
    
    fun searchWallpapers(query: String): Flow<Result<List<Wallpaper>>> = flow {
        try {
            emit(Result.Loading)
            
            // Search by post_id (since we don't have a title field)
            val searchQuery = wallpapersCollection
                .whereEqualTo("status", "published")
                .whereGreaterThanOrEqualTo("post_id", query)
                .whereLessThanOrEqualTo("post_id", query + "\uf8ff")
                .orderBy("post_id")
                .limit(50)
            
            val snapshot = searchQuery.get().await()
            val wallpapers = snapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Wallpaper::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }
            
            emit(Result.Success(wallpapers))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
    
    fun getWallpapersByCategory(category: String): Flow<Result<List<Wallpaper>>> = flow {
        try {
            emit(Result.Loading)
            
            val categoryQuery = wallpapersCollection
                .whereEqualTo("rating", category.lowercase())
                .whereEqualTo("status", "published")
                .orderBy("date_added", Query.Direction.DESCENDING)
                .limit(50)
            
            val snapshot = categoryQuery.get().await()
            val wallpapers = snapshot.documents.mapNotNull { document ->
                try {

                    document.toObject(Wallpaper::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }

            emit(Result.Success(wallpapers))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
    
    // Simple test method to check Firebase connection
    fun testConnection(): Flow<Result<Int>> = flow {
        try {
            Log.d(TAG, "Testing Firebase connection...")
            emit(Result.Loading)
            
            // Test basic connection
            val snapshot = wallpapersCollection.limit(5).get().await()
            Log.d(TAG, "Connection test successful!")
            Log.d(TAG, "Total documents found: ${snapshot.size()}")
            
            // Log some sample data for debugging
            snapshot.documents.forEach { doc ->
                Log.d(TAG, "Document ID: ${doc.id}")
                Log.d(TAG, "Document data: ${doc.data}")
                Log.d(TAG, "Status: ${doc.getString("status")}")
                Log.d(TAG, "Is Featured: ${doc.getBoolean("is_featured")}")
                Log.d(TAG, "Title: ${doc.getString("title")}")
                Log.d(TAG, "---")
            }
            
            // Count published wallpapers
            val publishedSnapshot = wallpapersCollection
                .whereEqualTo("status", "published")
                .get().await()
            Log.d(TAG, "Published wallpapers count: ${publishedSnapshot.size()}")
            
            // Count featured wallpapers
            val featuredSnapshot = wallpapersCollection
                .whereEqualTo("is_featured", true)
                .get().await()
            Log.d(TAG, "Featured wallpapers count: ${featuredSnapshot.size()}")
            
            // Count published AND featured wallpapers
            val publishedFeaturedSnapshot = wallpapersCollection
                .whereEqualTo("status", "published")
                .whereEqualTo("is_featured", true)
                .get().await()
            Log.d(TAG, "Published AND Featured wallpapers count: ${publishedFeaturedSnapshot.size()}")
            
            emit(Result.Success(snapshot.size()))
        } catch (e: Exception) {
            Log.e(TAG, "Connection test failed: ${e.message}", e)
            emit(Result.Error(e))
        }
    }
} 