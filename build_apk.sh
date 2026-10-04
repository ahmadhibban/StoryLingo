#!/bin/bash
# ==============================================================================
# StoryLingo - One-Click Standalone CLI Builder (Termux / Linux)
# ==============================================================================
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

echo "=========================================="
echo " Building APK: StoryLingo"
echo "=========================================="

ANDROID_JAR=""
if [ -f "/data/data/com.termux/files/home/android-sdk-jar/android.jar" ]; then
    ANDROID_JAR="/data/data/com.termux/files/home/android-sdk-jar/android.jar"
elif [ -n "$ANDROID_HOME" ] && [ -f "$ANDROID_HOME/platforms/android-34/android.jar" ]; then
    ANDROID_JAR="$ANDROID_HOME/platforms/android-34/android.jar"
elif [ -f "/data/data/com.termux/files/usr/share/java/android.jar" ]; then
    ANDROID_JAR="/data/data/com.termux/files/usr/share/java/android.jar"
elif [ -f "$HOME/android-sdk-jar/android.jar" ]; then
    ANDROID_JAR="$HOME/android-sdk-jar/android.jar"
fi

if [ -z "$ANDROID_JAR" ] || [ ! -f "$ANDROID_JAR" ]; then
    echo "ERROR: android.jar not found!"
    exit 1
fi

echo "-> Using android.jar: $ANDROID_JAR"

rm -rf bin gen
mkdir -p bin gen

echo "==> 0. Compiling Stories Database..."
python3 build_stories.py

echo "==> 1. Generating R.java (aapt)..."
aapt package -f -m -J gen/ -M AndroidManifest.xml -S res/ -I "$ANDROID_JAR"

echo "==> 2. Compiling Java sources (javac)..."
javac -d bin/ -cp "$ANDROID_JAR" $(find gen src -name "*.java")

echo "==> 3. Converting to Dalvik bytecode (d8)..."
d8 --lib "$ANDROID_JAR" --output bin/ $(find bin/ -name "*.class")

echo "==> 4. Packaging APK (aapt)..."
ASSETS_FLAG=""
if [ -d "assets" ] && [ "$(ls -A assets 2>/dev/null)" ]; then
    ASSETS_FLAG="-A assets/"
fi
aapt package -f -M AndroidManifest.xml -S res/ $ASSETS_FLAG -I "$ANDROID_JAR" -F bin/app.unsigned.apk
cd bin
aapt add app.unsigned.apk classes.dex
cd ..

echo "==> 5. Signing APK (apksigner)..."
mkdir -p apk
OUT_APK="apk/StoryLingo.apk"
KEYSTORE="debug.keystore"
if [ ! -f "$KEYSTORE" ]; then
    keytool -genkey -v -keystore "$KEYSTORE" -storepass android -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US" 2>/dev/null
fi

apksigner sign --ks "$KEYSTORE" --ks-pass pass:android --out "$OUT_APK" bin/app.unsigned.apk

echo "==> 6. Verifying APK..."
apksigner verify -v "$OUT_APK"

echo "=========================================="
echo " BUILD SUCCESSFUL!"
echo " Signed APK: $OUT_APK"
echo "=========================================="
