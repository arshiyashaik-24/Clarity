package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cl_documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val docType: String, // Prescription, Doctor Note, Referral, Clinical Observation, Discharge Summary
    val timestamp: Long = System.currentTimeMillis(),
    val confidenceScore: Int, // 0 - 100
    val rawTextCode: String,  // Key representing the stylized handwriting overlay layout to render
    val medication: String,
    val dosage: String,
    val frequency: String,
    val duration: String,
    val instructions: String,
    val additionalNotes: String,
    val alternativeInterpretation: String, // format: "Meds: Alternative A (X%), Alternative B (Y%)"
    val highlights: String,               // coordinate bounds mapping "medication|[x,y,w,h];dosage|[x,y,w,h]..."
    val timelineEvents: String,           // format: "timestamp|description;timestamp2|description2..."
    val isDemo: Boolean = false
)
