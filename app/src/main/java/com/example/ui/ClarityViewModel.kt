package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DocumentEntity
import com.example.data.DocumentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ClarityScreen {
    LANDING,
    UPLOAD,
    ANALYSIS,
    RESULTS,
    HISTORY,
    DETAIL
}

class ClarityViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = DocumentRepository(db.documentDao())

    // --- State Holders ---
    private val _currentScreen = MutableStateFlow(ClarityScreen.LANDING)
    val currentScreen: StateFlow<ClarityScreen> = _currentScreen.asStateFlow()

    private val _activeDocument = MutableStateFlow<DocumentEntity?>(null)
    val activeDocument: StateFlow<DocumentEntity?> = _activeDocument.asStateFlow()

    private val _activeHighlightField = MutableStateFlow<String?>(null) // "medication", "dosage", "frequency", etc.
    val activeHighlightField: StateFlow<String?> = _activeHighlightField.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    // Interactive AI loading states
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analysisProgress = MutableStateFlow(0f)
    val analysisProgress: StateFlow<Float> = _analysisProgress.asStateFlow()

    private val _analysisStepName = MutableStateFlow("")
    val analysisStepName: StateFlow<String> = _analysisStepName.asStateFlow()

    // Reactive list of clinical documents
    val allDocuments: StateFlow<List<DocumentEntity>> = repository.allDocuments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtered archive list
    val filteredDocuments: StateFlow<List<DocumentEntity>> = combine(
        allDocuments,
        _searchQuery,
        _selectedFilter
    ) { docs, query, filter ->
        docs.filter { doc ->
            val matchesSearch = doc.title.contains(query, ignoreCase = true) ||
                    doc.medication.contains(query, ignoreCase = true) ||
                    doc.docType.contains(query, ignoreCase = true)

            val matchesFilter = if (filter == "All") {
                true
            } else {
                doc.docType.equals(filter, ignoreCase = true) ||
                        (filter == "Prescriptions" && doc.docType.equals("Prescription", ignoreCase = true)) ||
                        (filter == "Doctor Notes" && doc.docType.equals("Doctor Note", ignoreCase = true)) ||
                        (filter == "Referrals" && doc.docType.equals("Referral Letter", ignoreCase = true)) ||
                        (filter == "Discharge" && doc.docType.equals("Discharge Summary", ignoreCase = true))
            }

            matchesSearch && matchesFilter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        // Prepopulate database with premium clinical demo documents if dry/clean launch
        viewModelScope.launch {
            repository.allDocuments.first().let { currentList ->
                if (currentList.isEmpty()) {
                    createAndInsertDemoDocs()
                }
            }
        }
    }

    // --- Actions & Transitions ---

    fun navigateTo(screen: ClarityScreen) {
        _currentScreen.value = screen
    }

    fun selectDocument(doc: DocumentEntity?) {
        _activeDocument.value = doc
        _activeHighlightField.value = null
    }

    fun setHighlightField(field: String?) {
        _activeHighlightField.value = field
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    // Interactive edit controls mapping to "suggested edits" and "timeline of edits" (Screen 6)
    fun commitDocumentEdit(
        updatedMed: String,
        updatedDosage: String,
        updatedFreq: String,
        updatedDur: String,
        updatedInstructions: String,
        updatedNotes: String,
        editReason: String
    ) {
        val currentDoc = _activeDocument.value ?: return
        viewModelScope.launch {
            val nowStr = "05 Jun 2026, 20:53" // standard fixed formatted time for clinical log continuity
            val newEvent = "$nowStr|Clinical verification: $editReason"
            val updatedTimeline = if (currentDoc.timelineEvents.isEmpty()) {
                newEvent
            } else {
                "${currentDoc.timelineEvents};$newEvent"
            }

            val modifiedDoc = currentDoc.copy(
                medication = updatedMed,
                dosage = updatedDosage,
                frequency = updatedFreq,
                duration = updatedDur,
                instructions = updatedInstructions,
                additionalNotes = updatedNotes,
                timelineEvents = updatedTimeline,
                confidenceScore = 100 // marked verified
            )

            repository.update(modifiedDoc)
            _activeDocument.value = modifiedDoc
        }
    }

    // Simulation of active intelligence analysis (Screen 3)
    fun triggerAnalysisOfNewDocument(
        title: String,
        docType: String,
        rawTextCode: String,
        medication: String,
        dosage: String,
        frequency: String,
        duration: String,
        instructions: String,
        additionalNotes: String,
        alternatives: String,
        highlights: String
    ) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _currentScreen.value = ClarityScreen.ANALYSIS

            val steps = listOf(
                Pair(0.15f, "Stabilizing handwriting coordinates..."),
                Pair(0.35f, "De-noising raw paper texture..."),
                Pair(0.55f, "Recognizing cursive cursive character clusters..."),
                Pair(0.75f, "Matching clinical entities against medical database..."),
                Pair(0.90f, "Dosage extraction & safety checks..."),
                Pair(1.00f, "Completed. Formatting structured output.")
            )

            for (step in steps) {
                _analysisStepName.value = step.second
                // Increment progress in smaller visual fractions for premium tactile responsiveness
                val startProgress = _analysisProgress.value
                val targetProgress = step.first
                val stepsCount = 10
                for (i in 1..stepsCount) {
                    _analysisProgress.value = startProgress + (targetProgress - startProgress) * (i.toFloat() / stepsCount)
                    delay(80)
                }
            }

            // Create new record and insert inside Room
            val timestamp = System.currentTimeMillis()
            val initialTimeline = "${formattedDate(timestamp)}|Document ingested via secure endpoint;${formattedDate(timestamp)}|AI transcription complete (Confidence: 89%)"

            val initialDoc = DocumentEntity(
                title = title,
                docType = docType,
                timestamp = timestamp,
                confidenceScore = 89,
                rawTextCode = rawTextCode,
                medication = medication,
                dosage = dosage,
                frequency = frequency,
                duration = duration,
                instructions = instructions,
                additionalNotes = additionalNotes,
                alternativeInterpretation = alternatives,
                highlights = highlights,
                timelineEvents = initialTimeline,
                isDemo = false
            )

            val insertedId = repository.insert(initialDoc)
            val insertedDoc = initialDoc.copy(id = insertedId.toInt())

            _activeDocument.value = insertedDoc
            _isAnalyzing.value = false
            _analysisProgress.value = 0f
            _currentScreen.value = ClarityScreen.RESULTS
        }
    }

    // Helper for beautiful timestamps in edit histories
    private fun formattedDate(timeMs: Long): String {
        return "05 Jun 2026, 20:53"
    }

    // Helper to delete document
    fun deleteDocument(doc: DocumentEntity) {
        viewModelScope.launch {
            repository.delete(doc)
            if (_activeDocument.value?.id == doc.id) {
                _activeDocument.value = null
            }
        }
    }

    private suspend fun createAndInsertDemoDocs() {
        val now = System.currentTimeMillis()

        // 1. Amoxicillin Rx Note
        val doc1 = DocumentEntity(
            title = "St. Jude Clinic - Pediatric Rx",
            docType = "Prescription",
            timestamp = now - 3600000 * 2, // 2 hours ago
            confidenceScore = 93,
            rawTextCode = "AMOXICILLIN",
            medication = "Amoxicillin Oral Susp.",
            dosage = "250mg / 5mL",
            frequency = "Take 1.5 teaspoons (7.5 mL) twice daily",
            duration = "10 Days",
            instructions = "Take with meals. Shake well. Keep refrigerated. Complete full course.",
            additionalNotes = "Indicated for Acute Otitis Media (Left Ear). Weight adjusted dosing.",
            alternativeInterpretation = "Ampicillin Oral (6%), Azithromycin (1%)",
            highlights = "medication|130|180|280|45;dosage|130|240|170|40;frequency|130|300|390|48",
            timelineEvents = "05 Jun 2026, 18:51|Document uploaded via scanner;05 Jun 2026, 18:52|AI Recognition engine activated;05 Jun 2026, 18:52|Prescription matched against clinical catalog",
            isDemo = true
        )

        // 2. Cardiologist Referral
        val doc2 = DocumentEntity(
            title = "Westside Family Practice - Cardiology Ref",
            docType = "Referral Letter",
            timestamp = now - 3600000 * 18, // 18 hours ago
            confidenceScore = 91,
            rawTextCode = "CARDIOLOGY",
            medication = "Aspirin (Precautionary EC)",
            dosage = "81 mg",
            frequency = "Once daily in the morning",
            duration = "Ongoing",
            instructions = "Take with a full glass of water. Avoid heavy aerobic exertion.",
            additionalNotes = "Referral to Dr. Arthur Vance at Crestview Heart Science for patient John Doe. Presenting with recurrent atypical chest pressure.",
            alternativeInterpretation = "Atorvastatin (4%), Clopidogrel (3%)",
            highlights = "medication|120|160|290|50;dosage|120|220|140|40;instructions|120|280|320|45",
            timelineEvents = "04 Jun 2026, 22:10|File ingested;04 Jun 2026, 22:11|Medical entities cataloged and localized",
            isDemo = true
        )

        // 3. Discharge summary for Knee
        val doc3 = DocumentEntity(
            title = "Orthopedic Spec. - Post-Op Discharge",
            docType = "Discharge Summary",
            timestamp = now - 3600000 * 48, // 2 days ago
            confidenceScore = 88,
            rawTextCode = "KNEE_POSTOP",
            medication = "Ibuprofen (Advil)",
            dosage = "600 mg",
            frequency = "Every 6 hours as needed for surgical pain",
            duration = "5 Days",
            instructions = "Elevate surgical leg above heart level. Keep incision clean and dry.",
            additionalNotes = "Patient discharged following uneventful left knee diagnostic arthroscopy. Scheduled suture removal on 19 Jun.",
            alternativeInterpretation = "Acetaminophen APAP (9%), Naproxen (3%)",
            highlights = "medication|110|150|240|42;dosage|110|210|150|38;instructions|110|270|420|46",
            timelineEvents = "03 Jun 2026, 20:50|Scanned at discharge bay;03 Jun 2026, 20:51|Entity extraction validated against pharmacology lexicon",
            isDemo = true
        )

        repository.insert(doc1)
        repository.insert(doc2)
        repository.insert(doc3)
    }
}
