# Building Amulya Calculator APK

Complete guide to building debug and release APKs for the Amulya Calculator.

---

## Prerequisites

Before building, ensure you have:

- ✅ Android Studio (latest version)
- ✅ JDK 17 or newer
- ✅ Android SDK 34 (Android 14)
- ✅ Gradle 8.0 or newer
- ✅ At least 2GB free disk space

### Check Java Version
```bash
java -version
# Should show: openjdk version "17.x.x" or similar
```

### Check Android SDK
```bash
# List installed SDK packages
sdkmanager --list_installed
```

---

## Build Types

### Debug APK (for testing)
```bash
./gradlew assembleDebug
```

**Output**: `app/build/outputs/apk/debug/app-debug.apk`

**Features**:
- Debuggable (can inspect with Android Studio)
- Faster build time
- Larger file size
- Pre-signed with debug key

**Installation**:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Release APK (for Play Store)
```bash
./gradlew assembleRelease
```

**Output**: `app/build/outputs/apk/release/app-release-unsigned.apk`

**Features**:
- Optimized and minified code
- Smaller file size
- Unsigned (needs your signing key)
- Production-ready

**Note**: Must be signed before uploading to Play Store

---

## Step-by-Step Build Guide

### Step 1: Clone Repository
```bash
git clone https://github.com/Anmolmaurya0007/amulya-calculator.git
cd amulya-calculator
```

### Step 2: Sync Gradle
```bash
./gradlew sync
```

Or open in Android Studio and wait for automatic Gradle sync.

### Step 3: Build Debug APK (Recommended for Testing)
```bash
./gradlew assembleDebug
```

### Step 4: Locate Built APK
Debug APK will be at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Step 5: Install on Device/Emulator
**Using Android Studio**:
1. Connect device or open emulator
2. Click `Run` (Shift + F10) in Android Studio
3. Select device and click OK

**Using ADB**:
```bash
adb devices  # List connected devices
adb install app/build/outputs/apk/debug/app-debug.apk
```

**From File Explorer**:
1. Copy `app-debug.apk` to device
2. Open file manager and tap APK file
3. Follow installation prompts

---

## Signing Release APK

### Step 1: Create Signing Key (First Time Only)

**Using Android Studio**:
1. Build → Generate Signed Bundle/APK
2. Select APK (not Bundle)
3. Click "Create new..." for keystore
4. Fill in details:
   - Key store path: `/path/to/amulya.jks`
   - Password: (create secure password)
   - Key alias: `amulya`
   - Key password: (can be same as keystore)
   - Validity: 25 years
5. Save and remember the passwords!

**Using Command Line**:
```bash
keytool -genkey -v -keystore amulya.jks \
  -keyalg RSA -keysize 2048 -validity 9125 \
  -alias amulya
```

### Step 2: Sign Release APK

**Using Android Studio**:
1. Build → Generate Signed Bundle/APK
2. Select APK
3. Choose your keystore file
4. Enter passwords
5. Click Finish

**Using Command Line**:
```bash
# First build unsigned release APK
./gradlew assembleRelease

# Sign the APK
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore amulya.jks \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  amulya

# Enter keystore password when prompted
```

### Step 3: Verify Signature
```bash
jarsigner -verify -verbose -certs \
  app/build/outputs/apk/release/app-release-unsigned.apk
```

---

## Full Build Workflow

### Complete Debug Build & Install
```bash
# Clean previous builds
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install on connected device
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Verify installation
adb shell pm list packages | grep amulya
```

### Complete Release Build & Sign
```bash
# Clean previous builds
./gradlew clean

# Build release APK (unsigned)
./gradlew assembleRelease

# Sign the release APK
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore amulya.jks \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  amulya

# Output: app/build/outputs/apk/release/app-release-unsigned.apk (now signed)
```

---

## Optimization Flags

### Faster Debug Builds
```bash
./gradlew assembleDebug --no-daemon
```

### Smaller APK Size
```bash
# Enable code shrinking for release
./gradlew assembleRelease -x lint
```

### With Logging
```bash
./gradlew assembleDebug --info
# or
./gradlew assembleDebug --debug
```

---

## Troubleshooting

### Build Fails: "SDK not found"
**Solution**:
```bash
# Set ANDROID_HOME environment variable
export ANDROID_HOME=~/Android/Sdk
# or on Windows:
set ANDROID_HOME=C:\Users\YourUser\AppData\Local\Android\Sdk
```

### Build Fails: "JDK not found"
**Solution**:
```bash
# Set JAVA_HOME environment variable
export JAVA_HOME=/path/to/jdk17
# Verify:
java -version
```

### Build Fails: "Gradle sync failed"
**Solution**:
```bash
# Clean gradle cache
./gradlew clean

# Force re-download dependencies
rm -rf ~/.gradle/caches
./gradlew build
```

### APK Installation Fails: "App already installed"
**Solution**:
```bash
# Uninstall first
adb uninstall com.example.amulya

# Then install
adb install app/build/outputs/apk/debug/app-debug.apk
```

### APK Installation Fails: "INSTALL_PARSE_FAILED_NO_CERTIFICATES"
**Solution**: This happens with unsigned APKs. Use debug APK instead or sign the release APK.

---

## APK File Information

### Debug APK Details
```
Filename: app-debug.apk
Size: ~10-15 MB
Signed: Yes (with debug key)
Installable: Yes (direct install)
Debuggable: Yes
Use: Testing and development
```

### Release APK Details (Before Signing)
```
Filename: app-release-unsigned.apk
Size: ~6-10 MB
Signed: No
Installable: No (requires signing)
Debuggable: No
Use: Play Store submission (after signing)
```

### Release APK Details (After Signing)
```
Filename: app-release-unsigned.apk (same name, now signed)
Size: ~6-10 MB
Signed: Yes (with your key)
Installable: Yes
Debuggable: No
Use: Google Play Store distribution
```

---

## Uploading to Google Play Store

### Prerequisites
- Google Play Developer account ($25 one-time fee)
- Signed APK (release-signed)
- App icon and screenshots
- Store listing information

### Steps

1. **Sign in to Google Play Console**
   - Visit: https://play.google.com/console
   - Sign in with Google account

2. **Create New App**
   - Click "Create app"
   - Fill in app details

3. **Upload APK**
   - Go to Release → Production
   - Click "Create new release"
   - Upload signed APK
   - Add release notes

4. **Configure Store Listing**
   - Add app title, description, category
   - Add icon, screenshots, feature graphic
   - Set content rating

5. **Review and Submit**
   - Review app policy compliance
   - Submit for review
   - Wait for approval (usually 1-2 hours)

### Important
- Keep your keystore file and passwords safe!
- Use the same key for all releases
- Never share keystore or passwords

---

## GitHub Actions (Automated Builds)

To enable automatic builds on every push, create `.github/workflows/build.yml`:

```yaml
name: Build APK

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - uses: android-actions/setup-android@v3
      - run: chmod +x gradlew
      - run: ./gradlew assembleDebug
      - uses: actions/upload-artifact@v4
        with:
          name: amulya-debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
```

---

## Useful ADB Commands

```bash
# List connected devices
adb devices

# Install APK
adb install /path/to/app.apk

# Uninstall app
adb uninstall com.example.amulya

# Clear app data
adb shell pm clear com.example.amulya

# View app logs
adb logcat | grep amulya

# Restart app
adb shell am force-stop com.example.amulya
adb shell am start -n com.example.amulya/.MainActivity
```

---

## Build Performance Tips

1. **Disable unused features**
   - Disable testing frameworks if not needed
   - Remove unused dependencies

2. **Use Gradle daemon**
   - Speeds up consecutive builds
   - Enabled by default

3. **Parallel build**
   ```bash
   ./gradlew assembleDebug --parallel
   ```

4. **Incremental builds**
   - Only rebuild changed files
   - Much faster than clean builds

5. **Use build cache**
   ```bash
   ./gradlew assembleDebug --build-cache
   ```

---

## Version Information

- **App Version**: 1.1
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **JDK**: 17+
- **Gradle**: 8.0+

---

## Support

**Build Issues?**
1. Check this guide's troubleshooting section
2. Review Gradle output for error messages
3. Check Android Studio's "Build" tab for details
4. Search GitHub Issues: https://github.com/Anmolmaurya0007/amulya-calculator/issues

**Questions?**
- Email: anmolmaurya0007@gmail.com
- GitHub: https://github.com/Anmolmaurya0007

---

**Last Updated**: September 2026
**Version**: 1.1
