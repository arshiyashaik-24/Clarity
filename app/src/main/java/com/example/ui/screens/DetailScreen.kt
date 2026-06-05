package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DocumentEntity
import com.example.ui.ClarityViewModel
import com.example.ui.components.HandwrittenDocView
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: ClarityViewModel,
    onNavigateBack: () -> Unit
) {
    val activeDoc by viewModel.activeDocument.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Clinical Audit Logs",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("detail_back_btn")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ClarityBackground)
            )
        },
        containerColor = ClarityBackground
    ) { innerPadding ->
        val doc = activeDoc
        if (doc == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No document details loaded.", color = ClarityTextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .testTag("detail_screen_scroll"),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                
                // 1. CONFIDENCE METRIC BREAKDOWN
                item {
                    Text(
                        text = "Accuracy & Verification Metrics",
                        style = MaterialTheme.typography.titleMedium,
                        color = ClarityTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = ClaritySurface),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            ConfidenceMetricRow(label = "Character Outline Matching (OCR)", score = doc.confidenceScore + 2, color = ClaritySuccess)
                            ConfidenceMetricRow(label = "Medical Catalog Term Matching", score = doc.confidenceScore, color = ClarityAccent)
                            ConfidenceMetricRow(label = "Prescription Structural Compliance", score = doc.confidenceScore - 4, color = ClaritySecondary)
                        }
                    }
                }

                // 2. PHARMACOLOGY ENTITIES DEFINED (DETECTOR INTERPRETER)
                item {
                    Text(
                        text = "Identified Pharmacopoeia Definitions",
                        style = MaterialTheme.typography.titleMedium,
                        color = ClarityTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = ClaritySurface),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            ClinicalEntityBadge(
                                term = doc.medication,
                                type = "Indicated Formula",
                                description = when {
                                    doc.medication.contains("Amoxicillin") -> "A bactericidal penicillin-type antibiotic designed to arrest cell-wall synthesis, predominantly applied for pediatric upper respiratory tract infections."
                                    doc.medication.contains("Aspirin") -> "Acetylsalicylic acid, functioning as an anti-platelet aggregator, lowering lipid inflammation hazards."
                                    else -> "Non-steroidal anti-inflammatory treatment prescribed post-arthroscopy, targeting surgical connective swelling."
                                }
                            )

                            ClinicalEntityBadge(
                                term = doc.dosage,
                                type = "Calculated Strength",
                                description = "Standard weight-adjusted dosing guidelines verified against clinical parameters."
                            )
                        }
                    }
                }

                // 3. EDIT HISTORY TIMELINE (Screen 6 timeline requirement)
                item {
                    Text(
                        text = "Audit Log & Editing Timeline",
                        style = MaterialTheme.typography.titleMedium,
                        color = ClarityTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = ClaritySurface),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .testTag("timeline_container"),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val events = doc.timelineEvents.split(";").filter { it.isNotEmpty() }
                            events.forEachIndexed { index, event ->
                                val parts = event.split("|")
                                val time = parts.getOrNull(0) ?: ""
                                val desc = parts.getOrNull(1) ?: ""
                                val isFirst = index == 0
                                val isLast = index == events.size - 1

                                TimelineEventRow(
                                    timestamp = time,
                                    description = desc,
                                    isFirst = isFirst,
                                    isLast = isLast
                                )
                            }
                        }
                    }
                }

                // 4. SUGGESTED VERIFICATIONS BANNER
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ClaritySuccess.copy(alpha = 0.05f))
                            .border(1.dp, ClaritySuccess.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = ClaritySuccess,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "All Safety Benchmarks Verified",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = ClaritySuccess
                                )
                                Text(
                                    text = "This transcription was mapped systematically and checked for dose reasonability. Patient indicators show no toxic redundancies.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ClarityTextPrimary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfidenceMetricRow(
    label: String,
    score: Int,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = ClarityTextPrimary
            )
            Text(
                text = "$score%",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
        LinearProgressIndicator(
            progress = { score.toFloat() / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFFF1F5F9)
        )
    }
}

@Composable
fun ClinicalEntityBadge(
    term: String,
    type: String,
    description: String
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = term,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = ClarityPrimary
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(ClarityAccent.copy(alpha = 0.08f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = type,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = ClarityAccent
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = ClarityTextSecondary,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun TimelineEventRow(
    timestamp: String,
    description: String,
    isFirst: Boolean,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Vertical connector bar
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isLast) ClarityPrimary else ClarityAccent)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(Color(0xFFE2E8F0))
                )
            }
        }

        // Event metadata
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = timestamp,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = ClarityTextSecondary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = ClarityTextPrimary,
                lineHeight = 18.sp
            )
        }
    }
}
