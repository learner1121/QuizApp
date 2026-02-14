# QuizApp: Modern CS Fundamentals Quiz App

A robust, offline-capable Android application built with **Jetpack Compose** and **Kotlin**. This app uses a sophisticated dual-layer data strategy (Room + Retrofit) to provide a seamless user experience for practicing core CS subjects like **CN, OOPS, DBMS, and OS**.

---

## 🚀 Quick Start
*   **Download the App:** [Click here to download the latest APK](https://github.com/learner1121/QuizApp/releases/tag/v1.0)
*   **Backend Status:** Hosted on Render (Please allow 30-50 s for initial wake-up)

---

## 📱 Key Features
*   **Google One-Tap Login:** Secure, frictionless authentication via **Firebase Auth**.
*   **Hybrid Quiz Engine:**
    *   **Baseline Set:** Foundational questions stored in **Room Database** for instant, offline-first access.
    *   **Dynamic Set:** Randomized questions fetched via **Express.js API** for every new session.
*   **Offline-First Design:** Smart caching ensures the UI remains responsive and functional even during network fluctuations.
*   **Modern UI/UX:** 100% declarative UI built with **Jetpack Compose** and **Material 3** guidelines.

---

## 🛠 Tech Stack

### Frontend (Android)
*   **Language:** Kotlin
*   **UI:** Jetpack Compose (Declarative UI)
*   **Concurrency:** Kotlin Coroutines & Flow
*   **Local DB:** Room Persistence Library
*   **Networking:** Retrofit 2 & OkHttp
*   **Auth:** Firebase Authentication (Google Sign-In)

### Backend & Database
*   **Runtime:** Node.js & Express.js
*   **Database:** MongoDB Atlas (Cloud)
*   **Hosting:** Render 

---

## 🏗 Architecture & Design
The app follows **MVVM (Model-View-ViewModel)** and **Clean Architecture** principles to ensure a scalable and testable codebase.

### Data Strategy (The "Single Source of Truth")
1.  **Repository Pattern:** Manages data flow between the local Room DB and the remote API.
2.  **Latency Optimization:** The app displays the "Baseline Set" from Room immediately upon launch after first launch with internet, while Coroutines fetch the "Dynamic Set" in the background.
3.  **Randomization Logic:** The Node.js backend uses a MongoDB aggregation pipeline to ensure a unique set of questions for every request.

---

## 📸 Demo & Screenshots

| Login Experience | Category Selection | Interactive Quiz |
| :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/a2873409-7f5e-4efd-89ea-04923dab5469" width="200" /> | <img src="https://github.com/user-attachments/assets/a5e6dc68-580c-4705-86f2-c35e4df0f6ed" width="200" /> | <img src="https://github.com/user-attachments/assets/507b1d89-6f79-4487-b0bf-eb45be31f17b" width="200" /> |

> **Note:** The UI is built entirely using **Jetpack Compose** for a smooth, modern Android experience.


## ⚙️ Installation & Setup

### Android App
#### Android Studio Setup
1. Clone the repo: `git clone https://github.com/learner1121/QuizApp`
2. Build and Run via **Android Studio Dolphin** or higher.
   



