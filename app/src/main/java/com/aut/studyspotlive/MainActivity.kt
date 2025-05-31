package com.aut.studyspotlive

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.aut.studyspotlive.ui.screens.StudySpotListScreen
import com.aut.studyspotlive.ui.theme.StudySpotLiveTheme
import com.aut.studyspotlive.viewmodel.StudySpotViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    // Initialize ViewModel
    private val viewModel: StudySpotViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase
        initializeFirebase()
        
        enableEdgeToEdge()
        setContent {
            StudySpotLiveTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Display the study spot list screen
                    StudySpotListScreen(viewModel = viewModel)
                }
            }
        }
    }
    
    private fun initializeFirebase() {
        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this)
            // Firebase is ready
        } catch (e: Exception) {
            // Handle Firebase initialization error
            Toast.makeText(
                this,
                "Error initializing Firebase: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }
}