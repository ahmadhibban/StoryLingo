# StoryLingo 📚✨
### 1,000 Curated Bilingual English Stories with Sentence-by-Sentence Bengali Translations

**StoryLingo** is a full-featured bilingual reading, listening, and learning platform designed to help Bengali speakers master intermediate and advanced English through immersive, high-quality storytelling.

---

## 🌟 Key Features
- **1,000 Complete Stories**: Spanning 57 categories including Science, History, Nature, Space Exploration, World Literature, Mythology, Computing & Technology, and more.
- **7,732 Line-by-Line Bilingual Sentences**: Natural, idiomatic, human-grade Bengali translations alongside nuanced English prose.
- **Interactive Audio & Speech**: Teenage boy voice narration with synchronized real-time word highlighting.
- **Vocabulary Study Mode**: Interactive blur-to-reveal translation cards for active recall and vocabulary retention.
- **Progressive Web App & Native Android APK**:
  - Live Web & PWA: [https://ahmadhibban.github.io/StoryLingo/](https://ahmadhibban.github.io/StoryLingo/)
  - Standalone Offline Android App (`StoryLingo.apk`) with live cloud update synchronization.
  - Zero-reinstall updates: Any content change pushed to this repository immediately reflects in the Android application without requiring an APK reinstallation!

---

## 📂 Project Architecture
```
StoryLingo/
├── index.html              # Progressive Web App UI & Reader Engine
├── stories.js              # Compiled Master Database (1,000 Stories, 7,732 Sentence Pairs)
├── tailwind.js             # Offline Tailwind Engine
├── sw.js                   # Service Worker (Instant offline cache & background updates)
├── manifest.json           # Web App Manifest
├── icon.png                # App Icon
├── fonts/                  # Custom Typography (Poppins & Hind Siliguri)
├── assets/                 # Packaged Assets for Android APK
│   ├── data/               # 63 Modular JSON Story Batches (batch_00 to batch_62)
│   └── stories.js          # Synchronized Local Offline Database
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
