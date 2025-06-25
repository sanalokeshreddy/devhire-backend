# DevHire AI - Backend

## 🚀 Overview

DevHire AI Backend is the robust API server that powers the DevHire AI platform. It handles all the heavy lifting, including resume processing, AI integration with Google Gemini, skill extraction, career roadmap generation, and more. This backend serves both the student/developer and recruiter functionalities of the platform.

## ✨ Features

* **Resume Analysis & Job Fit Scoring:**
    * Accepts multiple resumes and a job description.
    * Utilizes Gemini AI to calculate a match percentage.
    * Identifies missing skills and suggests improvements.
    * Extracts GitHub links, a summary, and skills from resumes.
* **Career Roadmap Generation:**
    * Takes input parameters like target role, current skills, experience, and timeframe.
    * Generates a personalized career roadmap including weekly plans, recommended courses, and mini-project ideas.
    * Provides career insights like job demand and salary ranges.
* **AI Skill Extractor:**
    * Parses a given job description and extracts only the most relevant skills using Gemini AI.
* **Heatmap Data Generation:**
    * Prepares data for the frontend's JD-Resume Match Heatmap visualization, showing alignment between job descriptions and resumes.

## 🛠️ Tech Stack

* **Spring Boot 3:** A powerful framework for building production-grade, stand-alone, Spring-based applications.
* **Java 17+:** The programming language used for the backend development.
* **OkHttp:** An efficient HTTP client for making API calls.
* **Jackson:** A high-performance JSON processor.
* **Lombok:** A library that helps reduce boilerplate code.
* **Gemini 1.5 Flash API:** Integrated for advanced AI capabilities including resume scoring, career roadmap generation, and skill extraction.

## ⚙️ Installation & Setup

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/sanalokeshreddy/devhire-backend.git](https://github.com/sanalokeshreddy/devhire-backend.git)
    cd devhire-backend
    ```

2.  **Configure Environment Variables:**
    Create an `application.properties` or `application.yml` file (if not already present) in `src/main/resources` and add your Google Gemini API key:
    ```properties
    # application.properties
    gemini.api.key=YOUR_GEMINI_API_KEY
    ```
    *Replace `YOUR_GEMINI_API_KEY` with your actual API key obtained from Google Cloud.*

3.  **Build the project:**
    **Using Gradle:**
    ```bash
    ./gradlew build
    ```
    **Using Maven:**
    ```bash
    ./mvnw clean install
    ```

4.  **Run the application:**
    ```bash
    java -jar build/libs/devhire-ai-backend-0.0.1-SNAPSHOT.jar # Adjust version as needed
    ```
    The backend will typically start on port `8080`.

## 🌍 Deployment

The backend is configured for deployment on platforms like Render.

* **Render:**
    * Connect your GitHub repository to Render.
    * Configure the service as a Java web service.
    * Ensure Java 17 is selected for the runtime.
    * Set the build command (e.g., `./gradlew build` or `./mvnw clean install`).
    * Set the start command (e.g., `java -jar build/libs/your-artifact-name.jar`).
    * Expose a public API.
    * Set your `gemini.api.key` as an environment variable in Render's dashboard for secure storage.
    * Health check endpoint: `/api/health`

## 📊 API Endpoints

* **`POST /api/analyze`**: Analyze multiple resumes against a job description.
* **`POST /api/analyze-single`**: Analyze a single resume against a job description.
* **`POST /api/extract-skills`**: Extract relevant skills from a job description.
* **`POST /api/career-roadmap`**: Generate a personalized career roadmap.
* **`GET /api/health`**: Health check endpoint.

## 🙏 Acknowledgements

* Powered by Google Gemini AI
* Apache Tika (for robust document parsing)
