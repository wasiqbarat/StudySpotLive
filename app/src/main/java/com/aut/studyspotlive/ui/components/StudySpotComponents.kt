package com.aut.studyspotlive.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.aut.studyspotlive.model.StudySpot
import java.text.SimpleDateFormat
import java.util.*

// A component to display a single study spot in the list
@Composable
fun StudySpotItem(
    studySpot: StudySpot,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = studySpot.spotName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status: ",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                val statusColor = when (studySpot.currentStatus) {
                    StudySpot.STATUS_EMPTY -> Color(0xFF4CAF50) // Green
                    StudySpot.STATUS_GETTING_FULL -> Color(0xFFFFC107) // Yellow/Amber
                    StudySpot.STATUS_PACKED -> Color(0xFFF44336) // Red
                    else -> MaterialTheme.colorScheme.onSurface
                }
                
                Text(
                    text = studySpot.currentStatus,
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Display last updated timestamp with better null safety
            Spacer(modifier = Modifier.height(4.dp))
            
            // Format the timestamp safely outside of composable functions
            val lastUpdatedText = if (studySpot.lastUpdated != null) {
                try {
                    val sdf = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
                    "Last updated: ${sdf.format(studySpot.lastUpdated.toDate())}"
                } catch (e: Exception) {
                    "Last updated: Recently"
                }
            } else {
                "Last updated: Not available"
            }
            
            // Now display the text (no try-catch around the composable)
            Text(
                text = lastUpdatedText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// A dialog to update study spot status
@Composable
fun UpdateStatusDialog(
    studySpot: StudySpot,
    onDismiss: () -> Unit,
    onStatusSelected: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Update Status",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = studySpot.spotName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                StatusButton(
                    text = StudySpot.STATUS_EMPTY,
                    color = Color(0xFF4CAF50),
                    onClick = { onStatusSelected(StudySpot.STATUS_EMPTY) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                StatusButton(
                    text = StudySpot.STATUS_GETTING_FULL,
                    color = Color(0xFFFFC107),
                    onClick = { onStatusSelected(StudySpot.STATUS_GETTING_FULL) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                StatusButton(
                    text = StudySpot.STATUS_PACKED,
                    color = Color(0xFFF44336),
                    onClick = { onStatusSelected(StudySpot.STATUS_PACKED) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun StatusButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color
        ),
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Error",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Dismiss")
                }
            }
        }
    }
}
