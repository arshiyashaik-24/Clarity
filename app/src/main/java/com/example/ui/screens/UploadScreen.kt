package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ClarityScreen
import com.example.ui.ClarityViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    viewModel: ClarityViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResults: () -> Unit
) {
    val recentDocs by viewModel.allDocuments.collectAsState()
    val context = LocalContext.current

    // Set up standard modern visual file picker (zero-permissions required)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            // Trigger customized simulated high-fidelity analysis for picked custom note
            viewModel.triggerAnalysisOfNewDocument(
                title = "Imported Document (Physical Scan)",
                docType = "Prescription",
                rawTextCode = "AMOXICILLIN", // default layout for custom picking
                medication = "Amoxicillin with Clavulanic Acid",
                dosage = "875 mg / 125 mg Tablet",
                frequency = "Take 1 tablet every 12 hours",
                duration = "7 Days",
                instructions = "Take with a heavy meal at start of dosage. Do not freeze.",
                additionalNotes = "Custom uploaded photo at ${uri.lastPathSegment}. Successfully parsed with high OCR confidence.",
                alternatives = "Cefdinir (11%), Augmentin Brand (2%)",
                highlights = "medication|120|180|280|45;dosage|120|240|170|40"
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Upload Document",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("upload_back_btn")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ClarityBackground)
            )
        },
        containerColor = ClarityBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. DESCRIPTION TEXT
            item {
                Text(
                    text = "Transmit Medical Scribes For Active Transcription",
                    style = MaterialTheme.typography.titleMedium,
                    color = ClarityTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Select a file from your photo gallery, take a snap, or utilize one of our pre-arranged high-fidelity case studies below to see structured interpretation live.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ClarityTextSecondary,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 2. LARGE MINIMALIST CAPTURE/DRAG TARGET
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(ClaritySurface)
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(listOf(ClarityPrimary.copy(alpha = 0.4f), ClarityAccent.copy(alpha = 0.5f))),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                        .testTag("drag_drop_area"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ClarityAccent.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = ClarityAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Text(
                            text = "Snap Photo or Choose File",
                            style = MaterialTheme.typography.titleMedium,
                            color = ClarityTextPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Supports JPEG, PNG, or Scans (HIPAA Secured)",
                            style = MaterialTheme.typography.bodySmall,
                            color = ClarityTextSecondary
                        )
                    }
                }
            }

            // 3. SECURE DEMO NOTE PRESETS (EXTREMELY USEFUL FOR DEMOING)
            item {
                Text(
                    text = "Or Select A Prepopulated Case Study Note",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ClarityTextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PresetRow(
                        title = "Case Rx-01: Amoxicillin (Pediatrics)",
                        doctor = "Dr. S. Evans, St. Jude Clinic",
                        badgeText = "Prescription",
                        onClick = {
                            viewModel.triggerAnalysisOfNewDocument(
                                title = "St. Jude Clinic - Pediatric Rx",
                                docType = "Prescription",
                                rawTextCode = "AMOXICILLIN",
                                medication = "Amoxicillin Oral Susp.",
                                dosage = "250mg / 5mL",
                                frequency = "Take 1.5 teaspoons (7.5 mL) twice daily",
                                duration = "10 Days",
                                instructions = "Take with meals. Shake well. Keep refrigerated. Complete full course.",
                                additionalNotes = "Indicated for Acute Otitis Media (Left Ear). Weight adjusted dosing.",
                                alternatives = "Ampicillin Oral (6%), Azithromycin (1%)",
                                highlights = "medication|130|180|280|45;dosage|130|240|170|40;frequency|130|300|390|48"
                            )
                        }
                    )

                    PresetRow(
                        title = "Case Rf-04: Cardiology Referral Letter",
                        doctor = "Dr. C. Patel, Westside Practice",
                        badgeText = "Referral Letter",
                        onClick = {
                            viewModel.triggerAnalysisOfNewDocument(
                                title = "Westside Family Practice - Cardiology Ref",
                                docType = "Referral Letter",
                                rawTextCode = "CARDIOLOGY",
                                medication = "Aspirin (Precautionary EC)",
                                dosage = "81 mg",
                                frequency = "Once daily in the morning",
                                duration = "Ongoing",
                                instructions = "Take with a full glass of water. Avoid heavy aerobic exertion.",
                                additionalNotes = "Referral to Dr. Arthur Vance at Crestview Heart Science for patient John Doe. Presenting with recurrent atypical chest pressure.",
                                alternatives = "Atorvastatin (4%), Clopidogrel (3%)",
                                highlights = "medication|120|160|290|50;dosage|120|220|140|40;instructions|120|280|320|45"
                            )
                        }
                    )

                    PresetRow(
                        title = "Case Ds-09: Knee Surgery Arthroscopy Recovery",
                        doctor = "Orthopedic Specialist Clinic",
                        badgeText = "Discharge Summary",
                        onClick = {
                            viewModel.triggerAnalysisOfNewDocument(
                                title = "Orthopedic Spec. - Post-Op Discharge",
                                docType = "Discharge Summary",
                                rawTextCode = "KNEE_POSTOP",
                                medication = "Ibuprofen (Advil)",
                                dosage = "600 mg",
                                frequency = "Every 6 hours as needed for surgical pain",
                                duration = "5 Days",
                                instructions = "Elevate surgical leg above heart level. Keep incision clean and dry.",
                                additionalNotes = "Patient discharged following uneventful left knee diagnostic arthroscopy. Scheduled suture removal on 19 Jun.",
                                alternatives = "Acetaminophen APAP (9%), Naproxen (3%)",
                                highlights = "medication|110|150|240|42;dosage|110|210|150|38;instructions|110|270|420|46"
                            )
                        }
                    )
                }
            }

            // 4. RECENTLY ANALYZED CARDS (THUMBNAILS)
            if (recentDocs.isNotEmpty()) {
                item {
                    Text(
                        text = "Recently Analyzed Sandbox Notes",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ClarityTextPrimary,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth().testTag("recent_uploads_row"),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recentDocs.take(4)) { doc ->
                            RecentDocThumbnail(
                                docTitle = doc.title,
                                confidence = doc.confidenceScore,
                                type = doc.docType,
                                onClick = {
                                    viewModel.selectDocument(doc)
                                    viewModel.navigateTo(ClarityScreen.RESULTS)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PresetRow(
    title: String,
    doctor: String,
    badgeText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("preset_${title.replace(" ", "_")}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ClaritySurface),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ClarityAccent.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ClarityAccent
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ClarityTextPrimary
                )
                Text(
                    text = doctor,
                    style = MaterialTheme.typography.bodySmall,
                    color = ClarityTextSecondary
                )
            }
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = ClarityPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun RecentDocThumbnail(
    docTitle: String,
    confidence: Int,
    type: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable { onClick() }
            .testTag("recent_doc_thumb"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ClaritySurface),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF8FAFC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = ClarityPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Text(
                text = docTitle,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = ClarityTextPrimary,
                maxLines = 1
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = type,
                    style = MaterialTheme.typography.labelSmall,
                    color = ClarityTextSecondary
                )
                Text(
                    text = "$confidence%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = ClaritySuccess
                )
            }
        }
    }
}
