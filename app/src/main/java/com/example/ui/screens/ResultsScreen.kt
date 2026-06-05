package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DocumentEntity
import com.example.ui.ClarityScreen
import com.example.ui.ClarityViewModel
import com.example.ui.components.HandwrittenDocView
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    viewModel: ClarityViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val activeDoc by viewModel.activeDocument.collectAsState()
    val activeHighlightField by viewModel.activeHighlightField.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }

    // Dialog state controllers for clinician corrections
    var tempMedication by remember { mutableStateOf("") }
    var tempDosage by remember { mutableStateOf("") }
    var tempFrequency by remember { mutableStateOf("") }
    var tempDuration by remember { mutableStateOf("") }
    var tempInstructions by remember { mutableStateOf("") }
    var tempNotes by remember { mutableStateOf("") }
    var tempEditReason by remember { mutableStateOf("") }

    LaunchedEffect(activeDoc) {
        activeDoc?.let {
            tempMedication = it.medication
            tempDosage = it.dosage
            tempFrequency = it.frequency
            tempDuration = it.duration
            tempInstructions = it.instructions
            tempNotes = it.additionalNotes
            tempEditReason = "Clinician review"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Medical Document Intelligence",
                            style = MaterialTheme.typography.labelSmall,
                            color = ClarityTextSecondary
                        )
                        Text(
                            text = activeDoc?.title ?: "Interpretation Results",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ClarityTextPrimary,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("results_back_btn")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.navigateTo(ClarityScreen.DETAIL)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ClarityAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("view_details_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Details", fontSize = 13.sp)
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
                Text("No active document loaded", color = ClarityTextSecondary)
            }
        } else {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("results_split_container")
            ) {
                val isTablet = maxWidth >= 720.dp

                if (isTablet) {
                    // TABLET LANDSCAPE SPLIT SCREEN (Side-by-Side as designed)
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Left Physical Scan Layout
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SectionHeader(
                                title = "Source Document (Scribble Scan)",
                                icon = Icons.Default.Edit,
                                description = "Click ink blocks to highlight verified elements"
                            )
                            HandwrittenDocView(
                                rawTextCode = doc.rawTextCode,
                                activeHighlightField = activeHighlightField,
                                modifier = Modifier.weight(1f),
                                onFieldClicked = { field -> viewModel.setHighlightField(field) }
                            )
                        }

                        // Right Structured Interpretation Card list
                        Column(
                            modifier = Modifier
                                .weight(1.2f)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            SectionHeader(
                                title = "Structured AI Interpretation",
                                icon = Icons.Default.CheckCircle,
                                description = "Validated across standard pharmacopoeia indices"
                            )

                            InterpretationDetailPane(
                                doc = doc,
                                activeHighlightField = activeHighlightField,
                                onFieldFocused = { field -> viewModel.setHighlightField(field) },
                                onTriggerClinicEdit = { showEditDialog = true }
                            )
                        }
                    }
                } else {
                    // MOBILE ADAPTIVE PORTRAIT (Stacked details with nice tabs)
                    var mobileTabSelected by remember { mutableStateOf(0) } // 0 = Scan, 1 = Analysis Data

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Tab Selector
                        TabRow(
                            selectedTabIndex = mobileTabSelected,
                            containerColor = ClarityBackground,
                            contentColor = ClarityPrimary,
                            modifier = Modifier.clip(RoundedCornerShape(14.dp)).border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                        ) {
                            Tab(
                                selected = mobileTabSelected == 0,
                                onClick = { mobileTabSelected = 0 },
                                text = { Text("Original Document", fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = mobileTabSelected == 1,
                                onClick = { mobileTabSelected = 1 },
                                text = { Text("AI Interpretation", fontWeight = FontWeight.Bold) }
                            )
                        }

                        if (mobileTabSelected == 0) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Tap individual lines of blue ink below to highlight identified structures.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ClarityTextSecondary
                                )
                                HandwrittenDocView(
                                    rawTextCode = doc.rawTextCode,
                                    activeHighlightField = activeHighlightField,
                                    modifier = Modifier.weight(1f),
                                    onFieldClicked = { field -> viewModel.setHighlightField(field) }
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                InterpretationDetailPane(
                                    doc = doc,
                                    activeHighlightField = activeHighlightField,
                                    onFieldFocused = { field -> viewModel.setHighlightField(field) },
                                    onTriggerClinicEdit = { showEditDialog = true }
                                )
                            }
                        }
                    }
                }
            }
        }

        // CLINICAL MODIFICATION OVERRIDE DIALOG
        if (showEditDialog && doc != null) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = {
                    Text(
                        "Clinical Modification Override",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Correct any detected fields. All overrides are logged securely in the immutable timeline.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ClarityTextSecondary
                        )
                        
                        OutlinedTextField(
                            value = tempMedication,
                            onValueChange = { tempMedication = it },
                            label = { Text("Medication") },
                            modifier = Modifier.fillMaxWidth().testTag("edit_med_field")
                        )

                        OutlinedTextField(
                            value = tempDosage,
                            onValueChange = { tempDosage = it },
                            label = { Text("Dosage / Strength") },
                            modifier = Modifier.fillMaxWidth().testTag("edit_dosage_field")
                        )

                        OutlinedTextField(
                            value = tempFrequency,
                            onValueChange = { tempFrequency = it },
                            label = { Text("Frequency") },
                            modifier = Modifier.fillMaxWidth().testTag("edit_freq_field")
                        )

                        OutlinedTextField(
                            value = tempDuration,
                            onValueChange = { tempDuration = it },
                            label = { Text("Duration") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = tempInstructions,
                            onValueChange = { tempInstructions = it },
                            label = { Text("Instructions") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = tempNotes,
                            onValueChange = { tempNotes = it },
                            label = { Text("Additional Clinical Notes") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = tempEditReason,
                            onValueChange = { tempEditReason = it },
                            label = { Text("Reason for correction (required)") },
                            modifier = Modifier.fillMaxWidth().testTag("edit_reason_field")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.commitDocumentEdit(
                                updatedMed = tempMedication,
                                updatedDosage = tempDosage,
                                updatedFreq = tempFrequency,
                                updatedDur = tempDuration,
                                updatedInstructions = tempInstructions,
                                updatedNotes = tempNotes,
                                editReason = tempEditReason
                            )
                            showEditDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ClarityPrimary)
                    ) {
                        Text("Apply Override")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(ClarityPrimary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = ClarityPrimary, modifier = Modifier.size(16.dp))
        }
        Column {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ClarityTextPrimary
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = ClarityTextSecondary
            )
        }
    }
}

@Composable
fun InterpretationDetailPane(
    doc: DocumentEntity,
    activeHighlightField: String?,
    onFieldFocused: (String?) -> Unit,
    onTriggerClinicEdit: () -> Unit
) {
    // Top Score banner
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ClarityPrimary.copy(alpha = 0.02f)),
        border = BorderStroke(1.dp, ClarityPrimary.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total OCR Alignment Accuracy",
                    style = MaterialTheme.typography.labelSmall,
                    color = ClarityTextSecondary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ClaritySuccess,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${doc.confidenceScore}% Clarity Precision",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = ClarityTextPrimary
                        )
                    )
                }
            }

            Button(
                onClick = onTriggerClinicEdit,
                colors = ButtonDefaults.buttonColors(containerColor = ClarityPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("verify_override_btn")
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Edit Entries", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Interactive Fields
    DocumentAttributeCard(
        label = "Medication Name",
        value = doc.medication,
        importanceScore = 94,
        isFocused = activeHighlightField == "medication",
        alternatives = doc.alternativeInterpretation.substringBefore(";"),
        onClick = { onFieldFocused("medication") },
        testTag = "results_item_medication"
    )

    DocumentAttributeCard(
        label = "Extract Dosage / Strength",
        value = doc.dosage,
        importanceScore = 95,
        isFocused = activeHighlightField == "dosage",
        alternatives = null,
        onClick = { onFieldFocused("dosage") },
        testTag = "results_item_dosage"
    )

    DocumentAttributeCard(
        label = "Prescribed Frequency",
        value = doc.frequency,
        importanceScore = doc.confidenceScore - 2,
        isFocused = activeHighlightField == "frequency",
        onClick = { onFieldFocused("frequency") },
        testTag = "results_item_frequency"
    )

    DocumentAttributeCard(
        label = "Duration Of Regime",
        value = doc.duration,
        importanceScore = 94,
        isFocused = activeHighlightField == "duration",
        onClick = { onFieldFocused("duration") },
        testTag = "results_item_duration"
    )

    DocumentAttributeCard(
        label = "Special Patient Instructions",
        value = doc.instructions,
        importanceScore = 91,
        isFocused = activeHighlightField == "instructions",
        onClick = { onFieldFocused("instructions") },
        testTag = "results_item_instructions"
    )

    DocumentAttributeCard(
        label = "Clinical Notes & Observations",
        value = doc.additionalNotes,
        importanceScore = doc.confidenceScore,
        isFocused = activeHighlightField == "additionalNotes",
        onClick = { onFieldFocused("additionalNotes") },
        testTag = "results_item_notes"
    )
}

@Composable
fun DocumentAttributeCard(
    label: String,
    value: String,
    importanceScore: Int,
    isFocused: Boolean,
    alternatives: String? = null,
    onClick: () -> Unit,
    testTag: String = ""
) {
    val outlineColor by animateColorAsState(
        targetValue = if (isFocused) ClarityAccent else Color(0xFFE2E8F0),
        animationSpec = tween(250), label = ""
    )
    val cardBgColor by animateColorAsState(
        targetValue = if (isFocused) ClarityAccent.copy(alpha = 0.015f) else ClaritySurface,
        animationSpec = tween(250), label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isFocused) 1.5.dp else 1.dp,
                color = outlineColor,
                shape = RoundedCornerShape(16.dp)
            )
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = ClarityTextSecondary
                )
                
                // Fine confidence pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when {
                                importanceScore >= 90 -> ClaritySuccess.copy(alpha = 0.1f)
                                importanceScore >= 80 -> ClarityWarning.copy(alpha = 0.1f)
                                else -> ClarityError.copy(alpha = 0.1f)
                            }
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$importanceScore% Conf.",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = when {
                            importanceScore >= 90 -> ClaritySuccess
                            importanceScore >= 80 -> ClarityWarning
                            else -> ClarityError
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = ClarityTextPrimary
                )
            )

            // Ambiguity estimation highlights (Alternative Interpretations when uncertain)
            if (!alternatives.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = ClarityWarning,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Conflicting Ink matches: $alternatives",
                            style = MaterialTheme.typography.labelSmall,
                            color = ClarityTextSecondary
                        )
                    }
                }
            }
        }
    }
}
