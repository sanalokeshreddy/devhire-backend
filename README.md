# 📌 DevHire AI – Resume Analyzer (Backend)

This is the **Spring Boot-based backend** for DevHire AI, which analyzes resumes using **Google Gemini AI**. It processes PDFs, extracts skills, and evaluates resumes against job descriptions.

🌐 **Live API**: [https://devhire-backend-6h12.onrender.com](https://devhire-backend-6h12.onrender.com)

---

## 🧠 Responsibilities

- 📄 Extract resume text (Apache Tika)
- 🤖 Use Gemini AI to:
  - Match resume to JD
  - Return match %, skills, suggestions
  - Generate a summary
- 🔎 Extract skills from job descriptions
- 🧼 Clean Markdown/code from Gemini response
- ⚙️ Send structured JSON to frontend

---

## 🛠️ Tech Stack

- **Spring Boot**
- **Apache Tika** – Resume text extraction
- **OkHttp** – Gemini API calls
- **Jackson** – JSON processing
- **Google Gemini API**
- **Deployed on Render (Docker)**

---

## 📁 Project Structure

```
backend/
├── src/main/java/com/devhire/
│   ├── controller/
│   ├── model/
│   ├── service/
│   └── DevHireAIApplication.java
├── Dockerfile
└── pom.xml
```

---

## 🔧 Environment Variable

Set via `application.properties` or Render dashboard:

```
gemini.api.key=YOUR_GEMINI_API_KEY
```

---

## ▶️ Run Locally

```bash
cd backend
./mvnw spring-boot:run
```

---

## 🚀 Deployment

- Deployed to **Render** using Docker
- Auto-rebuilds on every `main` branch push

---
