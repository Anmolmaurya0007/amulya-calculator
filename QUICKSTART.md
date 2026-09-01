# Amulya Calculator - Quick Start Guide

## Installation & Setup

### Prerequisites
- Android Studio Giraffe or newer
- JDK 17 or newer
- Android SDK 34+
- Gradle 8.0+

### Step 1: Clone the Repository
```bash
git clone https://github.com/Anmolmaurya0007/amulya-calculator.git
cd amulya-calculator
```

### Step 2: Open in Android Studio
1. Open Android Studio
2. Click "Open" → Select the `amulya-calculator` folder
3. Wait for Gradle sync to complete
4. Accept any SDK updates if prompted

### Step 3: Build the Project
```bash
./gradlew build
```

### Step 4: Run on Device or Emulator
```bash
./gradlew installDebug
```
Or use Android Studio's Run button (Shift + F10)

---

## Using the Enhanced Calculator

### Main Calculator Screen
```
┌─────────────────────────┐
│ ⏰  Amulya  ⚙️          │  ← Top bar with history and settings
├─────────────────────────┤
│         15              │  ← Display (tap to edit)
├─────────────────────────┤
│ AC  ⌫  .  +             │
│ 7   8  9  ÷             │
│ 4   5  6  ×             │
│ 1   2  3  −             │
│    0        =           │
└─────────────────────────┘
```

### Quick Features

#### 1. **Basic Calculation**
1. Tap numbers to enter expression
2. Tap operators (+, −, ×, ÷)
3. Tap = to calculate
4. Result appears in display

**Example**: `10 + 5 =` → Shows `15`

#### 2. **Edit Expression**
1. Tap on the display text
2. Edit in the dialog that appears
3. Tap "Apply" to update
4. Tap "Cancel" to discard

**Example**: Display shows `10 + 5`, tap it → edit to `10 + 6` → Apply

#### 3. **View History**
1. Tap the History icon (⏰) in top-left
2. See all your calculations with timestamps
3. Search by typing in the search box

#### 4. **Use History**
- **Use Result**: Tap any entry to load its result
- **Copy Expression**: Tap ⋮ menu → "Copy Expression"
- **Delete Entry**: Tap ⋮ menu → "Delete"
- **Clear All**: Tap trash icon (🗑️)

#### 5. **Settings**
1. Tap Settings icon (⚙️) in top-right
2. Choose theme (Light/Dark/System)
3. Toggle button sounds
4. Toggle vibration feedback
5. Adjust history limit (10-200 entries)
6. Tap ✓ to save

---

## Key Shortcuts

| Action | How |
|--------|-----|
| Clear all | Tap AC |
| Delete last digit | Tap ⌫ |
| Add decimal | Tap . |
| Calculate | Tap = |
| Edit expression | Tap display text |
| View history | Tap ⏰ icon |
| Open settings | Tap ⚙️ icon |
| Delete calculation | History → Tap ⋮ → Delete |

---

## Calculation Examples

### Basic Arithmetic
```
10 + 5 =                    → 15
20 − 8 =                    → 12
3 × 4 =                     → 12
20 ÷ 4 =                    → 5
```

### With Decimals
```
5.5 + 2.5 =                 → 8
10.25 − 0.5 =               → 9.75
2.5 × 4 =                   → 10
```

### Complex Expressions
```
(10 + 5) × 2 =              → 30
100 ÷ (5 + 5) =             → 10
(20 − 8) × 3 =              → 36
```

---

## Settings Guide

### Theme Options
- **Light**: Warm beige and tan colors
- **Dark**: Dark brown tones with orange accents
- **System**: Follows device theme (light/dark)

### Button Sounds
- **On**: Beep sound on every button press
- **Off**: Silent mode

### Vibration
- **On**: Short vibration feedback on button press
- **Off**: No haptic feedback

### History Limit
- Slider from 10 to 200 entries
- Older entries are removed when limit is reached
- Default: 100 entries

---

## History Search

### Search Examples
```
Search: "10"               → Shows all calculations with 10
Search: "25"               → Shows results containing 25
Search: "+"                → Shows all addition operations
Search: "5 ×"              → Shows multiplications with 5
```

### Case-Insensitive
Search works regardless of text case:
- "10 + 5" matches "10 + 5"
- Case matters only for symbols

---

## Landscape Mode

### Enable Rotation
1. Open device Settings
2. Search for "Display"
3. Enable "Auto-rotate screen"

### Landscape Layout
- Calculator screen remains fully functional
- Settings screen scrolls vertically
- All features accessible in both orientations
- Smooth transitions when rotating

---

## Troubleshooting

### App Won't Start
**Problem**: App crashes on launch
**Solution**:
1. Ensure JDK 17 is installed: `java -version`
2. Rebuild project: `./gradlew clean build`
3. Clear app data: Settings → Apps → Amulya → Clear Data
4. Reinstall: `./gradlew installDebug`

### History Not Saving
**Problem**: Calculations disappear after closing app
**Solution**:
1. Check app storage permissions
2. In Android 6.0+, grant permission: Settings → Apps → Amulya → Permissions
3. Ensure device has sufficient storage space

### Landscape Not Working
**Problem**: Screen doesn't rotate
**Solution**:
1. Enable auto-rotate in device settings
2. Close and reopen app
3. Rotate device >90 degrees to trigger rotation

### Buttons Not Responding
**Problem**: Buttons appear frozen or slow
**Solution**:
1. Close other apps to free up memory
2. Restart the app
3. Clear cache: Settings → Apps → Amulya → Storage → Clear Cache

### Expression Editor Won't Open
**Problem**: Tapping display doesn't open editor
**Solution**:
1. Make sure expression is not empty
2. Make sure expression is not "Error"
3. Tap directly on the text, not the surrounding area
4. Try double-tapping

---

## Performance Tips

1. **Reduce History Size**: Settings → History Limit → Lower number
2. **Disable Sounds**: Settings → Button sounds → Off (uses less CPU)
3. **Use Light Theme**: Uses slightly less battery than dark theme
4. **Close Background Apps**: Frees up memory

---

## Data & Privacy

### What Gets Stored
- Calculation history (expressions and results)
- User preferences (theme, sounds, vibration)
- History limit setting

### Where It's Stored
- **Location**: `/data/data/com.example.amulya/shared_prefs/`
- **Format**: XML (encrypted by Android)
- **Accessible**: Only by the Amulya app

### Clearing Data
1. Settings → Apps → Amulya → Storage
2. Tap "Clear Cache" (keeps history)
3. Tap "Clear Storage" (deletes everything)

---

## Developer Info

### Build Configuration
```gradle
Android SDK: 34 (Android 14)
Min SDK: 24 (Android 7.0)
Target SDK: 34
JDK: 17
Kotlin: 1.9+
```

### Dependencies
```gradle
androidx.core:core-ktx:1.13.1
androidx.activity:activity-compose:1.9.0
androidx.compose.material3:material3
androidx.compose.material:material-icons-extended
com.google.code.gson:gson:2.10.1
```

### Building APK
```bash
# Debug APK
./gradlew assembleDebug

# Release APK (requires signing key)
./gradlew assembleRelease
```

---

## Keyboard Shortcuts (Future)

*Currently, physical keyboard input is not supported. This is planned for v1.2*

---

## Version History

### v1.1 (Current)
- ✅ Persistent history with search
- ✅ Landscape mode support
- ✅ Expression editor
- ✅ Individual entry deletion
- ✅ History limit settings

### v1.0 (Original)
- Basic calculator
- Theme switching
- Sound and vibration
- Simple history display

---

## Feedback & Contributing

### Report Issues
1. Open GitHub issues tab
2. Click "New Issue"
3. Describe the problem with steps to reproduce
4. Include device info and Android version

### Contribute
1. Fork the repository
2. Create a feature branch
3. Make changes and test
4. Submit a pull request

---

## FAQ

**Q: Can I export my calculation history?**
A: Not yet, but it's planned for v1.2

**Q: Does the app work offline?**
A: Yes! The calculator works completely offline. History is stored locally on device.

**Q: Can I use the calculator with one hand?**
A: Yes, all buttons are reachable in portrait mode. Landscape mode recommended for larger phones.

**Q: How much storage does the history use?**
A: Approximately 100-200 bytes per calculation. 100 entries use ~10-20 KB.

**Q: Can I restore deleted history?**
A: No, deletion is permanent. Be careful when using "Clear All".

**Q: Why does the app request storage permission?**
A: To save calculation history to device storage.

**Q: Can I use scientific functions (sin, cos, etc.)?**
A: Not in v1.1, but planned for v1.2

---

## Support

For questions or issues:
1. Check this guide
2. Read ENHANCEMENTS.md for detailed feature docs
3. Open an issue on GitHub
4. Contact: anmolmaurya0007@gmail.com

---

**Last Updated**: September 2026
**Version**: 1.1
**License**: MIT
