# Clarity

Transforming handwritten medical documents into clear, structured insights.

## Overview

Clarity is an AI-powered application designed to convert difficult-to-read handwritten medical documents into structured, understandable information.

The goal is to help users interpret handwritten prescriptions, clinical notes, referral letters, discharge summaries, and other medical documents by combining handwriting recognition with intelligent medical context analysis.

Rather than simply extracting text, Clarity aims to identify and organize meaningful medical information such as medications, dosages, frequencies, durations, and instructions while providing confidence scores and alternative interpretations when ambiguity exists.

---

## Features

### Handwriting Recognition

Process handwritten medical documents and convert them into readable digital text.

### Medical Context Analysis

Identify and extract key medical information including:

* Medications
* Dosages
* Frequencies
* Treatment durations
* Clinical instructions
* Additional notes

### Structured Results

Present extracted information in a clear, organized format rather than raw text.

### Confidence Scoring

Display confidence levels for recognized information to help users evaluate reliability.

### Alternative Interpretations

Provide possible alternatives when handwriting is ambiguous.

### Document History

Store and review previously processed documents.

---

## Vision

Medical handwriting remains a significant challenge for patients and healthcare professionals alike.

Clarity aims to improve accessibility and understanding by transforming handwritten medical information into structured digital insights while maintaining transparency about uncertainty.

---

## Technology Stack

### Mobile Application

* Kotlin
* Android SDK
* Jetpack Compose

### Architecture

* MVVM Architecture
* Repository Pattern
* State Management with ViewModels

### AI & Processing

* OCR Pipeline
* Medical Entity Recognition
* Confidence Analysis
* AI-Assisted Interpretation

---

## Project Structure

```text
app/
├── data/
├── domain/
├── presentation/
├── ui/
├── navigation/
├── viewmodel/
├── repository/
└── utils/
```

---

## Getting Started

### Prerequisites

* Android Studio
* JDK 17+
* Android SDK

### Installation

Clone the repository:

```bash
git clone https://github.com/<username>/clarity.git
```

Open the project in Android Studio and allow Gradle to sync dependencies.

Run the application on an emulator or physical Android device.

---

## Roadmap

* Handwritten prescription recognition
* Medical terminology extraction
* Confidence scoring system
* Alternative interpretation suggestions
* Document history
* AI-powered medical context understanding
* Enhanced OCR accuracy
* Cloud synchronization

---

## Disclaimer

Clarity is a research and educational project.

The application is not intended for clinical decision-making, diagnosis, treatment recommendations, or professional medical use. All outputs should be reviewed and verified by qualified healthcare professionals.
