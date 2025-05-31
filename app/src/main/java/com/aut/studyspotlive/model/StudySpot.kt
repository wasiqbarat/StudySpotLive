package com.aut.studyspotlive.model

import com.google.firebase.Timestamp

data class StudySpot(
    val id: String = "",
    val spotName: String = "",
    val currentStatus: String = "",
    val lastUpdated: Timestamp? = null
) {
    companion object {
        const val STATUS_EMPTY = "Empty"
        const val STATUS_GETTING_FULL = "Getting Full"
        const val STATUS_PACKED = "Packed"
    }
}
