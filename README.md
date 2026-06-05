# Clarity

Transforming handwritten medical documents into clear, structured insights.

## Overview

Clarity is an AI-powered medical document intelligence platform designed to convert difficult-to-read handwritten medical documents into structured, readable, and understandable information.

Healthcare professionals, pharmacists, and patients often encounter handwritten prescriptions, clinical notes, referral letters, and discharge summaries that are challenging to interpret. Clarity aims to bridge that gap by combining handwriting recognition, medical terminology understanding, and intelligent document analysis.

Rather than simply transcribing text, Clarity focuses on extracting meaning and presenting information in a structured format with confidence scoring and alternative interpretations.

---

## Features

### Handwriting Recognition

Upload images, scans, or PDFs containing handwritten medical content and receive a readable interpretation.

### Medical Context Understanding

Recognizes medications, dosages, frequencies, durations, instructions, and other clinically relevant information.

### Structured Output

Transforms unstructured handwriting into organized sections such as:

* Medication
* Dosage
* Frequency
* Duration
* Instructions
* Additional Notes

### Confidence Scoring

Each extracted field includes a confidence estimate to help users evaluate reliability.

### Alternative Interpretations

When handwriting is ambiguous, Clarity presents likely alternatives instead of forcing a single prediction.

### Document History

Store, search, and revisit previously processed documents.

---

## Vision

Most OCR systems focus on recognizing characters.

Clarity focuses on understanding medical information.

The long-term goal is to create a platform capable of interpreting a wide range of handwritten healthcare documents while maintaining transparency about uncertainty and ambiguity.

---

## Technology Stack

### Frontend

* Next.js
* TypeScript
* Tailwind CSS
* shadcn/ui
* Framer Motion

### Backend

* PostgreSQL
* Prisma

### AI & Processing

* OpenAI API
* OCR Pipeline
* Medical Entity Extraction
* Confidence Analysis

---

## Project Structure

```text
app/
components/
lib/
prisma/
public/
types/
hooks/
```

The application follows a modular architecture focused on scalability, maintainability, and clear separation of concerns.

---

## Development

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

Open:

```text
http://localhost:3000
```

---

## Disclaimer

Clarity is an educational and research project.

The platform is not intended for clinical decision-making, diagnosis, treatment recommendations, or any medical use where inaccuracies could impact patient care.

All generated interpretations should be verified by qualified healthcare professionals.

---

Built to make medical information more accessible, understandable, and transparent.
