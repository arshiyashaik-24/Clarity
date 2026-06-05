package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ClarityViewModel
import com.example.ui.theme.*

@Composable
fun AnalysisScreen(viewModel: ClarityViewModel) {
    val progress by viewModel.analysisProgress.collectAsState()
    val stepName by viewModel.analysisStepName.collectAsState()

    // Breathing pulse animations matching Stripe & Apple style patterns
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ClarityBackground)
            .padding(24.dp)
            .testTag("analysis_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            
            // 1. ACTIVE BREATHING RADAR GLOW
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                // Outermost halo ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 2.dp,
                            brush = Brush.radialGradient(
                                colors = listOf(ClarityAccent.copy(alpha = 0.4f), Color.Transparent),
                                radius = 220f
                            ),
                            shape = CircleShape
                        )
                )

                // Mid ring (Rotating dot visualizer)
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .rotate(ringRotation)
                        .border(
                            width = 1.5.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(ClarityPrimary, ClarityAccent, Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )

                // Inner core
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(ClarityPrimary, ClarityAccent))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Analyzing",
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                            .rotate(-ringRotation) // negate rotation to keep icon steady but fluid
                    )
                }
            }

            // 2. PRIMARY STATUS BLOCK
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "CLARITY AI PROCESSING",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = ClarityAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                )
                Text(
                    text = "Analyzing Document Scribe...",
                    style = MaterialTheme.typography.displayMedium,
                    color = ClarityTextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Decrypting physical strokes & checking pharmacology guidelines.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ClarityTextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // 3. MINIMALIST TRACKSBAR
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .testTag("analysis_progress_bar"),
                    color = ClarityAccent,
                    trackColor = ClarityPrimary.copy(alpha = 0.05f)
                )

                Text(
                    text = "${(progress * 100).toInt()}% Analytically Resolved",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ClarityPrimary
                    )
                )
            }

            // 4. STEP CHRONOLOGY CHECKLIST (ACTIVE HIGHLIGHTS)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ClaritySurface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val activeStepIndex = when {
                        progress < 0.15f -> 0
                        progress < 0.35f -> 1
                        progress < 0.55f -> 2
                        progress < 0.75f -> 3
                        progress < 0.90f -> 4
                        else -> 5
                    }

                    StepCheckItem(title = "Stabilize coordinates & de-noise", isCompleted = progress >= 0.15f, isActive = activeStepIndex == 0)
                    StepCheckItem(title = "Extract character scribbles (OCR)", isCompleted = progress >= 0.35f, isActive = activeStepIndex == 1)
                    StepCheckItem(title = "Analyze cursive ink patterns", isCompleted = progress >= 0.55f, isActive = activeStepIndex == 2)
                    StepCheckItem(title = "Match medical dictionaries (Tx)", isCompleted = progress >= 0.75f, isActive = activeStepIndex == 3)
                    StepCheckItem(title = "Validate dosage & safety protocols", isCompleted = progress >= 0.90f, isActive = activeStepIndex == 4)
                    StepCheckItem(title = "Finalizing result structures", isCompleted = progress >= 1.0f, isActive = activeStepIndex == 5)
                }
            }
        }
    }
}

@Composable
fun StepCheckItem(
    title: String,
    isCompleted: Boolean,
    isActive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> ClaritySuccess.copy(alpha = 0.1f)
                        isActive -> ClarityAccent.copy(alpha = 0.1f)
                        else -> Color(0xFFF1F5F9)
                    }
                )
                .border(
                    width = 1.dp,
                    color = when {
                        isCompleted -> ClaritySuccess
                        isActive -> ClarityAccent
                        else -> Color(0xFFCBD5E1)
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = ClaritySuccess,
                    modifier = Modifier.size(12.dp)
                )
            } else if (isActive) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(ClarityAccent, CircleShape)
                )
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isCompleted -> ClarityTextPrimary
                    isActive -> ClarityAccent
                    else -> ClarityTextSecondary
                }
            )
        )
    }
}
