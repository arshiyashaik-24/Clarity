package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun HandwrittenDocView(
    rawTextCode: String,
    activeHighlightField: String?,
    modifier: Modifier = Modifier,
    onFieldClicked: (String) -> Unit = {}
) {
    // Infinite bouncing scanner animation to match Sleek design
    val infiniteTransition = rememberInfiniteTransition(label = "ScannerAnim")
    val scannerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScannerBeam"
    )

    // Elegant warm clinical off-white textured background
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 380.dp, max = 500.dp)
            .testTag("handwritten_document_card"),
        shape = RoundedCornerShape(24.dp), // Premium rounded 3xl silhouette
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAF7)),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)), // Light slate border
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            
            // 1. Ruled lines & Medical Watermark Background Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Custom Rx Watermark
                drawCircle(
                    color = Color(0x060F4C81),
                    radius = w * 0.25f,
                    center = Offset(w * 0.65f, h * 0.55f)
                )

                // Margin lines (Red left margin)
                drawLine(
                    color = Color(0xFFFDA4AF),
                    start = Offset(45.dp.toPx(), 0f),
                    end = Offset(45.dp.toPx(), h),
                    strokeWidth = 2.dp.toPx()
                )

                // Light blue rule guidance lines
                val lineSpacing = 35.dp.toPx()
                val offsetTop = 80.dp.toPx()
                var currentY = offsetTop
                while (currentY < h) {
                    drawLine(
                        color = Color(0xFFE2E8F0),
                        start = Offset(0f, currentY),
                        end = Offset(w, currentY),
                        strokeWidth = 1.dp.toPx()
                    )
                    currentY += lineSpacing
                }
            }

            // 2. Structured Medical Note Format (Letterhead)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                // Clinic Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "METRO HEALTH INTEGRATED CLINIC",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = ClarityPrimary.copy(alpha = 0.8f),
                                letterSpacing = 2.sp
                            )
                        )
                        Text(
                            text = "802 Medical Plaza, Ste 400 • License #NY-8294812",
                            style = MaterialTheme.typography.labelSmall,
                            color = ClarityTextSecondary
                        )
                    }
                    Text(
                        text = "Rx",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontFamily = FontFamily.Serif,
                            color = ClarityPrimary,
                            fontWeight = FontWeight.Black
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Patient Information Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "PATIENT: Johnathan Doe",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            color = ClarityTextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "DATE: 05 Jun 2026",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            color = ClarityTextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // 3. Render Simulated Organic Clinical Calligraphy with Interactive Regions
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (rawTextCode.uppercase()) {
                        "KNEE_POSTOP" -> {
                            RenderInkField(
                                text = "Ibuprofen 600mg tabs",
                                topOffset = 18.dp,
                                leftOffset = 42.dp,
                                isActive = activeHighlightField == "medication",
                                onClick = { onFieldClicked("medication") }
                            )

                            RenderInkField(
                                text = "#20 tabs (Sixty)",
                                topOffset = 52.dp,
                                leftOffset = 42.dp,
                                isActive = activeHighlightField == "dosage",
                                onClick = { onFieldClicked("dosage") }
                            )

                            RenderInkField(
                                text = "1 tab PO q6h prn pain w/ food",
                                topOffset = 88.dp,
                                leftOffset = 42.dp,
                                isActive = activeHighlightField == "frequency" || activeHighlightField == "instructions",
                                onClick = { onFieldClicked("instructions") }
                            )

                            RenderInkField(
                                text = "Elevate knee + RICE strategy.",
                                topOffset = 124.dp,
                                leftOffset = 42.dp,
                                isActive = activeHighlightField == "additionalNotes",
                                onClick = { onFieldClicked("additionalNotes") }
                            )
                        }
                        "CARDIOLOGY" -> {
                            RenderInkField(
                                text = "Aspirin EC (Enteric Coated)",
                                topOffset = 18.dp,
                                leftOffset = 42.dp,
                                isActive = activeHighlightField == "medication",
                                onClick = { onFieldClicked("medication") }
                            )

                            RenderInkField(
                                text = "81 mg PO Daily (Morning)",
                                topOffset = 52.dp,
                                leftOffset = 42.dp,
                                isActive = activeHighlightField == "dosage" || activeHighlightField == "frequency",
                                onClick = { onFieldClicked("dosage") }
                            )

                            RenderInkField(
                                text = "Disp: 30 days initial supply",
                                topOffset = 88.dp,
                                leftOffset = 42.dp,
                                isActive = activeHighlightField == "duration",
                                onClick = { onFieldClicked("duration") }
                            )

                            RenderInkField(
                                text = "Referral to Crestview Cardiology (Dr. Vance)",
                                topOffset = 124.dp,
                                leftOffset = 42.dp,
                                isActive = activeHighlightField == "additionalNotes",
                                onClick = { onFieldClicked("additionalNotes") }
                            )
                        }
                        else -> { // Default AMOXICILLIN
                            RenderInkField(
                                text = "Amoxicillin Oral Susp (Berry)",
                                topOffset = 18.dp,
                                leftOffset = 42.dp,
                                isActive = activeHighlightField == "medication",
                                onClick = { onFieldClicked("medication") }
                            )

                            RenderInkField(
                                text = "250mg / 5mL (Disp 150mL)",
                                topOffset = 52.dp,
                                leftOffset = 42.dp,
                                isActive = activeHighlightField == "dosage",
                                onClick = { onFieldClicked("dosage") }
                            )

                            RenderInkField(
                                text = "1.5 tsp (7.5 mL) PO BID",
                                topOffset = 88.dp,
                                leftOffset = 42.dp,
                                isActive = activeHighlightField == "frequency",
                                onClick = { onFieldClicked("frequency") }
                            )

                            RenderInkField(
                                text = "x 10 Days • Take w/ food • Refrigerate",
                                topOffset = 124.dp,
                                leftOffset = 42.dp,
                                isActive = activeHighlightField == "duration" || activeHighlightField == "instructions",
                                onClick = { onFieldClicked("instructions") }
                            )
                        }
                    }
                }
                
                // Doctor Sign-off Watermark Bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Dr. S. Evans",
                            fontStyle = FontStyle.Italic,
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1B3A4F).copy(alpha = 0.5f)
                        )
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(1.dp)
                                .background(Color.LightGray)
                        )
                        Text(
                            text = "Authorized Signature / DEA",
                            style = MaterialTheme.typography.labelSmall,
                            color = ClarityTextSecondary
                        )
                    }
                }
            }

            // Beautiful glowing laser scanner bloom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .offset(y = (maxHeight - 60.dp) * scannerProgress)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                ClarityAccent.copy(alpha = 0.05f),
                                ClarityAccent.copy(alpha = 0.22f),
                                ClarityAccent.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )
            // Precision sharp neon scanning bar core
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .offset(y = (maxHeight - 2.dp) * scannerProgress)
                    .background(ClarityAccent)
            )
        }
    }
}

@Composable
fun RenderInkField(
    text: String,
    topOffset: androidx.compose.ui.unit.Dp,
    leftOffset: androidx.compose.ui.unit.Dp,
    isActive: Boolean,
    onClick: () -> Unit
) {
    // Beautiful transition animations matching premium aesthetic guidelines
    val highlightAlpha by animateFloatAsState(
        targetValue = if (isActive) 0.15f else 0.02f,
        animationSpec = tween(300), label = ""
    )
    val strokeWidth by animateFloatAsState(
        targetValue = if (isActive) 2f else 1f,
        animationSpec = tween(300), label = ""
    )
    val highlightColor by animateColorAsState(
        targetValue = if (isActive) ClarityAccent else ClarityPrimary.copy(alpha = 0.1f),
        animationSpec = tween(300), label = ""
    )

    Box(
        modifier = Modifier
            .offset(x = leftOffset, y = topOffset)
            .height(30.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(highlightColor.copy(alpha = highlightAlpha))
            .border(
                width = strokeWidth.dp,
                color = if (isActive) ClarityAccent else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp,
            color = Color(0xFF1E3A8A).copy(alpha = if (isActive) 1f else 0.88f) // Deep cursive ink blue
        )
    }
}
