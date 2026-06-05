package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ClarityScreen
import com.example.ui.ClarityViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    viewModel: ClarityViewModel,
    onNavigateToUpload: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(listOf(ClarityPrimary, ClarityAccent))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Clarity",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = ClarityPrimary,
                                letterSpacing = (-0.5).sp
                            )
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.testTag("nav_history_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = null,
                            tint = ClaritySecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "History Archive",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = ClaritySecondary
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ClarityBackground
                )
            )
        },
        containerColor = ClarityBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            
            // 1. CONFIDENT HERO HEADER
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    
                    // Clinical Safety tag
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ClarityAccent.copy(alpha = 0.08f))
                            .border(1.dp, ClarityAccent.copy(alpha = 0.2f), CircleShape)
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ClarityAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "AI-Powered Medical Intelligence Hub",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = ClarityAccent
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Turn Medical Handwriting\nInto Clarity",
                        style = MaterialTheme.typography.displayLarge,
                        color = ClarityTextPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 44.sp,
                        modifier = Modifier.testTag("hero_title")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Upload prescriptions, doctor notes, and handwritten medical documents. Clarity transforms them into structured, readable, and safe information with full coordinate-matched verification.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ClarityTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .widthIn(max = 640.dp)
                            .testTag("hero_subtitle")
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // ACTION CTAs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onNavigateToUpload,
                            colors = ButtonDefaults.buttonColors(containerColor = ClarityPrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("primary_upload_cta")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Analyze Document",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        OutlinedButton(
                            onClick = {
                                // Instantly select the first demo document (amoxicillin) and view results
                                val list = viewModel.allDocuments.value
                                if (list.isNotEmpty()) {
                                    val demoItem = list.firstOrNull { it.isDemo } ?: list.first()
                                    viewModel.selectDocument(demoItem)
                                    viewModel.navigateTo(ClarityScreen.RESULTS)
                                } else {
                                    onNavigateToHistory()
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, ClarityPrimary.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("demo_cta")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = ClarityPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "View Live Demo",
                                color = ClarityPrimary,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // 2. STUNNING EXAMPLE CONVERSION PREVIEW
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = ClaritySurface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(ClarityAccent, CircleShape)
                            )
                            Text(
                                text = "How Clarity Intelligence Interprets Scribes",
                                style = MaterialTheme.typography.titleMedium,
                                color = ClarityTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // High visual contrast split-illustration
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Handwritten Simulation left card
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFA)),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "HANDWRITTEN INPUT",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ClarityTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Amox 500mg\n1 cap tid x 10d",
                                        fontStyle = FontStyle.Italic,
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 18.sp,
                                        color = Color(0xFF1E3A8A),
                                        lineHeight = 24.sp
                                    )
                                }
                            }

                            // Flow indicator
                            Box(
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = ClarityAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Structured output right card
                            Card(
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = ClarityPrimary.copy(alpha = 0.03f)),
                                border = BorderStroke(1.dp, ClarityPrimary.copy(alpha = 0.1f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "CLARITY INTERPRETATION",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ClarityTextSecondary
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(ClaritySuccess.copy(alpha = 0.1f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "94% Match",
                                                color = ClaritySuccess,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Medication: Amoxicillin\nDosage: 500 mg\nFrequency: Three times daily\nDuration: 10 Days",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ClarityTextPrimary,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. FEATURE HIGHLIGHTS
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Advanced Clinical Capabilities",
                        style = MaterialTheme.typography.titleLarge,
                        color = ClarityTextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FeatureCard(
                            icon = Icons.Default.Build,
                            title = "High-Fidelity OCR",
                            description = "Deciphers clinical handwriting from physical notes or scanned PDFs.",
                            modifier = Modifier.weight(1f)
                        )
                        FeatureCard(
                            icon = Icons.Default.Refresh,
                            title = "Terminology Cross-Check",
                            description = "Matches extracted terms with pharmacopoeia to ensure accuracy.",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FeatureCard(
                            icon = Icons.Default.Warning,
                            title = "Ambiguity Safeguards",
                            description = "Flags uncertain labels with confidence scores and alternative matches.",
                            modifier = Modifier.weight(1f)
                        )
                        FeatureCard(
                            icon = Icons.Default.Edit,
                            title = "Verification Pipeline",
                            description = "Supports clinician overrides and saves detailed timelines of edits.",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 4. TRUST BADGES
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(ClarityPrimary.copy(alpha = 0.02f))
                        .border(1.dp, ClarityPrimary.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TrustBadge(title = "Clinical Decision Support", subtitle = "Provider-approved")
                        Box(modifier = Modifier.size(1.dp, 30.dp).background(Color.LightGray))
                        TrustBadge(title = "Secure & HIPAA-Ready", subtitle = "End-to-end encryption")
                        Box(modifier = Modifier.size(1.dp, 30.dp).background(Color.LightGray))
                        TrustBadge(title = "Local Preservation", subtitle = "Room offline sandbox")
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureCard(
    imageVector: ImageVector? = null,
    icon: Any? = null,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ClaritySurface),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ClarityPrimary.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center
            ) {
                if (imageVector != null) {
                    Icon(imageVector = imageVector, contentDescription = null, tint = ClarityPrimary, modifier = Modifier.size(18.dp))
                } else if (icon is ImageVector) {
                    Icon(imageVector = icon, contentDescription = null, tint = ClarityPrimary, modifier = Modifier.size(18.dp))
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = ClarityTextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = ClarityTextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun TrustBadge(
    title: String,
    subtitle: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = ClarityPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = ClarityTextSecondary
        )
    }
}
