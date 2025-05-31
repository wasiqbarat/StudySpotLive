package com.aut.studyspotlive.repository

import android.util.Log
import com.aut.studyspotlive.model.StudySpot
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    // Use companion object for static constants
    companion object {
        private const val TAG = "FirebaseRepository"
        private const val COLLECTION_NAME = "study_spots" // Ensure this exactly matches your Firestore collection name
    }
    
    // Simple direct references to Firebase services
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val spotsCollection = firestore.collection(COLLECTION_NAME)
    
    // Check if user is authenticated
    fun isUserAuthenticated(): Boolean {
        val currentUser = auth.currentUser
        val isAuthenticated = currentUser != null
        Log.d(TAG, "User authentication status: $isAuthenticated")
        return isAuthenticated
    }

    // Test method to diagnose Firebase authentication issues
    suspend fun testAuthentication() {
        Log.d(TAG, "======= FIREBASE AUTH DIAGNOSTIC TEST =======")
        Log.d(TAG, "Testing Firebase Auth initialization...")
        
        try {
            // Check instance details
            Log.d(TAG, "Auth class: ${auth.javaClass.name}")
            Log.d(TAG, "Auth app name: ${auth.app?.name ?: "null"}")
            Log.d(TAG, "Current user: ${auth.currentUser?.uid ?: "null"}")
            
            // Force sign out to ensure clean state
            auth.signOut()
            Log.d(TAG, "Signed out any existing user")
            Log.d(TAG, "Current user after signout: ${auth.currentUser?.uid ?: "null"}")
            
            // Try to sign in
            Log.d(TAG, "Attempting anonymous sign in...")
            auth.signInAnonymously()
                .addOnSuccessListener { result ->
                    val user = result.user
                    Log.d(TAG, "SUCCESS! User: ${user?.uid ?: "null"}")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "FAILURE! Error type: ${e.javaClass.name}")
                    Log.e(TAG, "Error message: ${e.message}")
                    Log.e(TAG, "Error cause: ${e.cause?.message ?: "unknown"}")
                }
                .addOnCompleteListener { 
                    Log.d(TAG, "Sign in attempt completed")
                }
            
            Log.d(TAG, "Test initiated - check logs for results")
        } catch (e: Exception) {
            Log.e(TAG, "Test failed with exception: ${e.message}")
            e.printStackTrace()
        }
        
        Log.d(TAG, "======= END OF TEST =======")
    }
    
    // Simplified sign-in anonymous method
    suspend fun signInAnonymously(): Boolean {
        try {
            // Check if already signed in
            if (auth.currentUser != null) {
                return true
            }
            
            // Sign in anonymously
            val authResult = auth.signInAnonymously().await()
            return authResult.user != null
        } catch (e: Exception) {
            Log.e(TAG, "Anonymous auth failed: ${e.message}")
            return false
        }
    }

    // Get all study spots, fetching fresh from server (not cache)
    suspend fun getStudySpots(): List<StudySpot> {
        // Skip authentication for now - we'll fix it properly later
        // Focus on getting the data fetch working
        
        return try {
            // Enhanced logging and verification
            Log.d(TAG, "──────────────────────────────────────────")
            Log.d(TAG, "Starting study spots fetch operation")
            Log.d(TAG, "Collection path: ${spotsCollection.path}")
            
            // Verify Firestore initialization
            Log.d(TAG, "Using Firestore instance: ${firestore.javaClass.name}")
            
            // Check if authentication is working
            val authUser = auth.currentUser
            if (authUser != null) {
                Log.d(TAG, "Currently signed in as: ${authUser.uid}")
            } else {
                Log.d(TAG, "No user currently signed in")
            }
            
            // Try to fetch with a more flexible approach
            Log.d(TAG, "Fetching study spots from SERVER source...")
            val querySnapshot = try {
                // Direct Firestore call without using collection reference to avoid any issues
                firestore.collection(COLLECTION_NAME)
                    .get(Source.SERVER)
                    .await()
            } catch (serverException: Exception) {
                Log.w(TAG, "SERVER fetch failed: ${serverException.message}")
                
                // Fallback to DEFAULT source which picks best available
                Log.d(TAG, "Falling back to DEFAULT source...")
                firestore.collection(COLLECTION_NAME)
                    .get()
                    .await()
            }
            
            Log.d(TAG, "Query snapshot obtained, document count: ${querySnapshot.size()}")
            
            // Check if the snapshot is empty
            if (querySnapshot.isEmpty) {
                Log.w(TAG, "⚠️ Query returned EMPTY results. Possible issues:")
                Log.w(TAG, "   1. Collection '${COLLECTION_NAME}' might not exist")
                Log.w(TAG, "   2. No documents in the collection")
                Log.w(TAG, "   3. Security rules prevent access")
                
                // Create a test document in the study_spots collection to ensure it exists
                try {
                    val newSpotData = hashMapOf(
                        "spotName" to "Library",
                        "currentStatus" to "Empty",
                        "lastUpdated" to com.google.firebase.Timestamp.now()
                    )
                    
                    Log.d(TAG, "Creating test document in ${COLLECTION_NAME}...")
                    firestore.collection(COLLECTION_NAME)
                        .add(newSpotData)
                        .await()
                        
                    Log.d(TAG, "✓ Test document created successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "✗ Failed to create test document: ${e.message}")
                }
            }
            
            val spots = querySnapshot.documents.mapNotNull { document ->
                try {
                    val id = document.id
                    val data = document.data
                    Log.d(TAG, "Document $id data: $data")
                    
                    val spotName = document.getString("spotName") ?: ""
                    val currentStatus = document.getString("currentStatus") ?: "Unknown"
                    val lastUpdated = document.getTimestamp("lastUpdated")
                    
                    // Successfully parsed a study spot
                    Log.d(TAG, "Parsed spot: id=$id, name=$spotName, status=$currentStatus")
                    StudySpot(id, spotName, currentStatus, lastUpdated)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing document ${document.id}: ${e.message}")
                    e.printStackTrace()
                    null
                }
            }
            
            Log.d(TAG, "Successfully fetched ${spots.size} study spots")
            Log.d(TAG, "──────────────────────────────────────────")
            spots
        } catch (e: Exception) {
            Log.e(TAG, "Critical error getting study spots: ${e.message}")
            Log.e(TAG, "Exception type: ${e.javaClass.name}")
            e.printStackTrace()
            emptyList()
        }
    }

    // Update status of a study spot without authentication
    suspend fun updateSpotStatus(spotId: String, status: String): Boolean {
        // Skip authentication for now

        return try {
            // Update with server timestamp
            val updates = hashMapOf<String, Any>(
                "currentStatus" to status,
                "lastUpdated" to com.google.firebase.Timestamp.now()
            )
            
            Log.d(TAG, "Updating spot $spotId without auth check, new status: $status")
            
            // Update the document
            spotsCollection
                .document(spotId)
                .update(updates)
                .await()
                
            Log.d(TAG, "Successfully updated status for spot $spotId to '$status'")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update spot status: ${e.message}")
            Log.e(TAG, "Exception type: ${e.javaClass.name}")
            e.printStackTrace()
            false
        }
    }
    
    // Create study spot without authentication check
    suspend fun createStudySpot(spotName: String): Boolean {
        // Skip authentication for now - we'll fix it properly later
        
        return try {
            // Create document with exact fields matching the existing structure
            val studySpotData = hashMapOf(
                "spotName" to spotName,
                "currentStatus" to "Empty", 
                "lastUpdated" to com.google.firebase.Timestamp.now()
            )
            
            Log.d(TAG, "Creating new study spot: $spotName without auth check")
            Log.d(TAG, "Data: spotName=$spotName, currentStatus=Empty, lastUpdated=now()")
            
            // Add document to collection with auto-generated ID
            val documentRef = spotsCollection.add(studySpotData).await()
            
            Log.d(TAG, "Successfully created study spot: $spotName with ID: ${documentRef.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create study spot: ${e.message}")
            Log.e(TAG, "Error type: ${e.javaClass.name}")
            e.printStackTrace()
            false
        }
    }
}
