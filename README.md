# StoryLingo 📚✨
### 100 Grand Bilingual Islamic Stories with Word-by-Word Vocabulary & Sentence-by-Sentence Bengali Meaning

> 🌐 **Live Web Application**: [https://ahmadhibban.github.io/StoryLingo/](https://ahmadhibban.github.io/StoryLingo/) — *Open and read directly in any web browser without installation.*

**StoryLingo** is a full-featured bilingual Islamic reading, listening, and language-learning platform. It presents 100 comprehensive, historically authentic Islamic chronicles with sentence-by-sentence literary Bengali contextual meaning (ভাবার্থ) and detailed word-by-word vocabulary breakdowns (শব্দার্থ) for language mastery.

---

## 🌟 Key Features
- **100 Grand Islamic Stories**:
  - **Stories of the Prophets (35 stories)**: From the Creation of Adam, Nuh, Ibrahim, Yusuf, Musa, Dawud, Sulaiman to Isa (peace be upon them all).
  - **Noble Companions (30 stories)**: Unwavering loyalty of Abu Bakr, Umar, Uthman, Ali, Bilal, Salman, Khalid ibn al-Walid, Mus'ab ibn Umayr, Khadijah, Aisha, and more.
  - **Quranic & Hadith Chronicles (20 stories)**: Ashab al-Kahf, Dhul-Qarnayn, The Man Who Killed 99 People, Jurayj the Monk, The Sinner and the Thirsty Dog, Ashab al-Fil, and more.
  - **Islamic Golden Age (15 stories)**: Caliph Umar ibn Abdul Aziz, Salahuddin Ayyubi, Tariq ibn Ziyad, The Four Imams (Abu Hanifa, Malik, Shafi'i, Ahmad), Al-Khwarizmi, Ibn Sina, Sultan Mehmed II, Al-Zahrawi, Al-Biruni, Al-Jazari, and the Prophet's ﷺ Farewell Sermon.
- **888 Sentence Pairs with Contextual Bengali Meaning (ভাবার্থ)**: Precise literary translation conveying spiritual depth and linguistic nuances.
- **3,756 Word-by-Word Vocabulary Breakdowns (ওয়ার্ড বাই ওয়ার্ড অর্থ)**: High-visibility interactive word chips displaying English and corresponding Bengali definitions for every sentence.
- **Instant Audio Narration & Pronunciation**: Fluent English speech synthesis with real-time word glow animation, plus one-tap pronunciation on every individual word chip.
- **Instant Search & Category Filter Pills**: Rapidly filter stories by collection or search titles in English and Bengali.
- **Progressive Web App & Native Android APK**:
  - Live Web & PWA: [https://ahmadhibban.github.io/StoryLingo/](https://ahmadhibban.github.io/StoryLingo/)
  - Standalone Offline Android App (`StoryLingo.apk`) with live cloud update synchronization.
  - Zero-reinstall updates: Any content change pushed to this repository immediately reflects in the Android application without requiring an APK reinstallation!

---

## 📂 Project Architecture
```
StoryLingo/
├── index.html              # Progressive Web App UI & Reader Engine
├── stories.js              # Compiled Master Database (100 Stories, 888 Sentences, 3,756 Word Chips)
├── tailwind.js             # Offline Tailwind Engine
├── sw.js                   # Service Worker (Instant offline cache & background updates)
├── manifest.json           # Web App Manifest
├── icon.png                # App Icon
├── fonts/                  # Custom Typography (Poppins & Hind Siliguri)
├── assets/                 # Packaged Assets for Android APK
│   ├── data/               # 10 Curated JSON Story Batches (batch_01 to batch_10)
│   ├── stories.js          # Synchronized Local Offline Database
│   └── legacy_short_fables/# Archived legacy short stories
├── src/                    # Native Android Java Sources (MainActivity.java)
├── res/                    # Android Layouts, Strings, and Mipmap Icons
├── AndroidManifest.xml     # Android App Manifest
├── build_stories.py        # Automated Database Compiler
└── build_apk.sh            # One-Click Standalone Termux/Linux APK Builder
```

---

## 🛠️ Build & Development

### 1. Recompile Stories
```bash
python3 build_stories.py
```

### 2. Build Android APK
```bash
bash build_apk.sh
```

## 👤 Author

**Ahmad Hibban**
- GitHub: [@ahmadhibban](https://github.com/ahmadhibban)

---

## 📄 License

MIT License. Copyright © Ahmad Hibban.
