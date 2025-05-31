package com.aut.studyspotlive.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aut.studyspotlive.model.StudySpot
import com.aut.studyspotlive.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class StudySpotViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val _studySpots = MutableStateFlow<List<StudySpot>>(emptyList())
    val studySpots: StateFlow<List<StudySpot>> = _studySpots

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        // Directly fetch spots without authentication check
        fetchStudySpots()
        Log.d("StudySpotViewModel", "Directly fetching spots on init (skipping auth)")
    }

    // Fetch study spots from server with enhanced error handling
    fun fetchStudySpots() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null  // Clear any previous errors
            
            try {
                Log.d("StudySpotViewModel", "Starting to fetch study spots...")
                
                // Try to sign in first to ensure we have authentication
                val isSignedIn = repository.signInAnonymously()
                if (!isSignedIn) {
                    Log.w("StudySpotViewModel", "Anonymous sign-in failed, attempting to fetch without auth")
                }
                
                // Get spots from the repository with enhanced error handling
                val spots = repository.getStudySpots()
                
                if (spots.isEmpty()) {
                    Log.w("StudySpotViewModel", "Received empty list of study spots")
                    // Don't update error message here, as empty list could be valid
                } else {
                    Log.d("StudySpotViewModel", "Successfully fetched ${spots.size} study spots")
                }
                
                // Update the state with whatever we got (even if empty)
                _studySpots.value = spots
                
            } catch (e: Exception) {
                // Handle error and show message to user
                val errorMsg = "Failed to load study spots: ${e.message ?: "Unknown error"}"
                _errorMessage.value = errorMsg
                Log.e("StudySpotViewModel", errorMsg, e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSpotStatus(spotId: String, newStatus: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.updateSpotStatus(spotId, newStatus)
                if (success) {
                    // Force refresh from server after successful update
                    fetchStudySpots()
                    Log.d("StudySpotViewModel", "Successfully updated spot status and refreshed data")
                } else {
                    _errorMessage.value = "Failed to update spot status"
                    Log.e("StudySpotViewModel", "Failed to update spot status: $spotId to $newStatus")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e("StudySpotViewModel", "Error updating spot", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
    
    // Create a new study spot and refresh the list immediately
    fun createStudySpot(spotName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Simplified: Always attempt to create the spot directly
                val success = repository.createStudySpot(spotName)
                
                if (success) {
                    Log.d("StudySpotViewModel", "Successfully created study spot: $spotName")
                    
                    // Important: Force refresh from server to show the new spot
                    fetchStudySpots()
                } else {
                    _errorMessage.value = "Failed to create new study spot"
                    Log.e("StudySpotViewModel", "Failed to create study spot with name: $spotName")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e("StudySpotViewModel", "Error creating study spot: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
