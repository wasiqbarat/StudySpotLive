package com.aut.studyspotlive

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase

class StudySpotLiveApp : Application() {
    private val TAG = "StudySpotLiveApp"
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        initializeFirebase()
    }
    
    private fun initializeFirebase() {
        try {
            // Initialize Firebase
            val app = FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase App initialized: ${app?.name}")
            
            // Explicitly initialize and verify Firebase Auth
            val auth = Firebase.auth
            Log.d(TAG, "Firebase Auth initialized: ${auth.app?.name}, tenantId: ${auth.tenantId ?: "default"}")
            
            // Try to get current auth state
            val currentUser = auth.currentUser
            Log.d(TAG, "Current auth state: user=${currentUser?.uid ?: "not signed in"}")
            
            // Configure Firestore
            val firestore = FirebaseFirestore.getInstance()
            
            // Log debug information for Firestore
            Log.d(TAG, "Firestore initialized: ${firestore.app.name}")
            
            // Force online behavior for consistent data
            firestore.clearPersistence()
                .addOnSuccessListener { Log.d(TAG, "Firestore persistence cleared successfully") }
                .addOnFailureListener { e -> Log.e(TAG, "Failed to clear Firestore persistence: ${e.message}") }
            
            Log.d(TAG, "Firebase services initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase: ${e.message}")
            e.printStackTrace()
        }
    }
}
