package com.aut.studyspotlive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aut.studyspotlive.model.StudySpot
import com.aut.studyspotlive.ui.components.*
import com.aut.studyspotlive.viewmodel.StudySpotViewModel

// Opt-in to experimental Material3 APIs
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun StudySpotListScreen(viewModel: StudySpotViewModel) {
    val spots by viewModel.studySpots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var selectedSpot by remember { mutableStateOf<StudySpot?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            // Using TopAppBar with ExperimentalMaterial3Api opt-in
            TopAppBar(
                title = { Text("StudySpot Live") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Study Spot",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && spots.isEmpty()) {
                LoadingIndicator()
            } else if (spots.isEmpty()) {
                // Show message when there are no spots
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Show error message if available
                    errorMessage?.let { message ->
                        ErrorMessage(message = message, onDismiss = { viewModel.clearError() })
                    }

                    // Header with refresh button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Study Spots",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        
                        // Manual refresh button
                        IconButton(onClick = { viewModel.fetchStudySpots() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                    }
                    
                    Text(
                        text = "No study spots available",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Please check back later or contact administrator",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                // Show the list of study spots with header
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header with title and refresh button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Study Spots",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        
                        // Manual refresh button
                        IconButton(onClick = { viewModel.fetchStudySpots() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                    }
                    
                    // Show loading indicator if refreshing with existing data
                    if (isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                    
                    // List of spots
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(spots) { spot ->
                            StudySpotItem(
                                studySpot = spot,
                                onClick = { selectedSpot = spot }
                            )
                        }
                    }
                }
            }
            
            // Show error message if there is one
            errorMessage?.let { message ->
                ErrorMessage(
                    message = message,
                    onDismiss = { viewModel.clearError() }
                )
            }
            
            // Show update dialog when a spot is selected
            selectedSpot?.let { spot ->
                UpdateStatusDialog(
                    studySpot = spot,
                    onDismiss = { selectedSpot = null },
                    onStatusSelected = { newStatus ->
                        viewModel.updateSpotStatus(spot.id, newStatus)
                        selectedSpot = null
                    }
                )
            }
            
            // Show add study spot dialog
            if (showAddDialog) {
                AddStudySpotDialog(
                    onDismiss = { showAddDialog = false },
                    onAddSpot = { spotName ->
                        viewModel.createStudySpot(spotName)
                        showAddDialog = false
                    }
                )
            }
        }
    }
}
