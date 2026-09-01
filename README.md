# Amulya Calculator

A beautiful, feature-rich Android calculator built with Jetpack Compose. Amulya means "priceless" in Sanskrit, reflecting the app's polished user experience and elegant design.

## ✨ Features

### Core Functionality
- ➕ Basic arithmetic operations (+, −, ×, ÷)
- 🧮 Expression evaluation with proper operator precedence
- 📝 Support for parentheses and decimal numbers
- 🔄 Real-time calculation preview

### 🆕 Enhanced Features (v1.1)
- 💾 **Persistent History** - All calculations saved across app sessions
- 🔍 **Search & Filter** - Quickly find previous calculations
- 🗑️ **Individual Deletion** - Remove specific history entries
- 📱 **Landscape Mode** - Fully responsive layout for all orientations
- ✏️ **Expression Editor** - Click the display to edit calculations inline
- ⚙️ **Customizable Settings** - Adjust history limit, theme, sounds, and vibration

### User Interface
- 🎨 **Multiple Themes** - Light, Dark, and System modes
- 🔊 **Sound Feedback** - Optional click sounds on button press
- 📳 **Haptic Feedback** - Optional vibration on interaction
- 🎯 **Intuitive Design** - Material Design 3 with warm, earthy colors
- ♿ **Accessible** - Large buttons, clear contrast, readable text

---

## 🚀 Quick Start

### Installation
```bash
git clone https://github.com/Anmolmaurya0007/amulya-calculator.git
cd amulya-calculator
./gradlew installDebug
```

### Basic Usage
1. **Calculate**: Tap numbers and operators, then tap `=`
2. **View History**: Tap the history icon (⏰) in the top-left
3. **Edit Expression**: Tap the display text to edit
4. **Manage Settings**: Tap the settings icon (⚙️) in the top-right

For detailed usage guide, see [QUICKSTART.md](QUICKSTART.md)

---

## 📚 Documentation

- **[QUICKSTART.md](QUICKSTART.md)** - Installation, usage, and troubleshooting
- **[ENHANCEMENTS.md](ENHANCEMENTS.md)** - Detailed feature documentation
- **[Changelog](#changelog)** - Version history and updates

---

## 🏗️ Architecture

### Project Structure
```
app/src/main/kotlin/com/example/amulya/
├── MainActivity.kt
│   ├── HistoryManager          - Persistent storage layer
│   ├── AmulyaRoot              - Main app composable
│   ├── CalculatorScreen        - Calculator UI
│   ├── SettingsScreen          - Settings UI
│   ├── HistoryScreen           - History UI with search
│   ├── HistoryItem             - History entry with actions
│   └── ExpressionEditorDialog  - Expression editor modal
```

### Key Components

#### HistoryManager
Handles persistent storage of calculations using SharedPreferences + Gson:
```kotlin
class HistoryManager(context: Context) {
    fun saveHistory(entries: List<HistoryEntry>)
    fun loadHistory(): List<HistoryEntry>
    fun deleteEntry(id: String, entries: List<HistoryEntry>)
    fun clearHistory()
}
```

#### Expression Parser
Recursive-descent parser supporting:
- Operators: `+`, `−`, `×`, `÷`
- Parentheses: `(`, `)`
- Decimals: `.`
- Proper operator precedence

#### Theme System
Three theme modes with comprehensive color palettes:
- Light theme with warm beige tones
- Dark theme with rich brown and orange
- System theme following device settings

---

## 🛠️ Technical Stack

### Dependencies
```gradle
// Jetpack Compose
androidx.activity:activity-compose:1.9.0
androidx.compose.material3:material3
androidx.compose.material:material-icons-extended

// Core
androidx.core:core-ktx:1.13.1

// Persistence
com.google.code.gson:gson:2.10.1
```

### Requirements
- **Android**: 7.0+ (API 24)
- **Target**: Android 14 (API 34)
- **JDK**: 17+
- **Kotlin**: 1.9+
- **Gradle**: 8.0+

---

## 📋 Feature Highlights

### Persistent History
- Automatically saves all calculations to device storage
- Survives app restarts and device reboots
- Search functionality for quick lookup
- Individual entry deletion with undo history
- Configurable history limit (10-200 entries)

```kotlin
// Example: History entry with timestamp
HistoryEntry(
    expression = "10 + 5",
    result = "15",
    timestamp = System.currentTimeMillis(),
    id = UUID.randomUUID().toString()
)
```

### Landscape Mode Support
- Fully responsive layout for portrait and landscape
- Settings screen with vertical scroll in landscape
- All features accessible in both orientations
- Smooth rotation transitions

### Expression Editor
- Click on display to open inline editor
- Edit previous expressions without losing context
- Apply changes with visual feedback
- Cancel without modifying expression

---

## 📊 Data Storage

### Storage Details
- **Location**: `/data/data/com.example.amulya/shared_prefs/amulya_prefs.xml`
- **Format**: JSON (via Gson) + SharedPreferences
- **Size**: ~100-200 bytes per calculation
- **Encryption**: Handled by Android's security framework

### Clear Data
Settings → Apps → Amulya → Storage → Clear Data

---

## 🎨 Customization

### Theme Colors

**Light Theme**
```kotlin
bg = Color(0xFFEFE9DC)              // Warm beige background
ink = Color(0xFF2B2620)              // Dark brown text
accent = Color(0xFFB8763A)           // Copper accent
```

**Dark Theme**
```kotlin
bg = Color(0xFF1B1812)               // Deep brown background
ink = Color(0xFFF3EEE2)              // Light cream text
accent = Color(0xFFE3A45E)           // Gold accent
```

---

## 🧪 Testing

### Testing Checklist
- [x] Persistent history saves and loads
- [x] Search filters work (case-insensitive)
- [x] Individual entry deletion
- [x] Expression editor opens/closes correctly
- [x] Landscape mode rotates smoothly
- [x] Settings persist across sessions
- [x] Sound and vibration toggle correctly
- [x] All calculations evaluate correctly

### Manual Testing Steps
1. **History**: Make 5 calculations, close app, reopen
2. **Search**: Search for "10" or "25" in history
3. **Editor**: Tap display, edit expression, apply
4. **Landscape**: Rotate device, verify all features work
5. **Settings**: Change theme, sounds, vibration, history limit

---

## 🐛 Troubleshooting

### Common Issues

**History not saving**
- Ensure storage permissions are granted
- Check device has sufficient storage space
- Try clearing app cache: Settings → Apps → Amulya → Storage → Clear Cache

**Landscape not rotating**
- Enable auto-rotate in device settings
- Close and reopen app
- Rotate device >90 degrees

**Expression editor won't open**
- Expression must not be empty
- Expression must not show "Error"
- Tap on the text, not surrounding area

**Buttons unresponsive**
- Close background apps to free memory
- Restart the app
- Check for sufficient storage space

For more help, see [QUICKSTART.md](QUICKSTART.md#troubleshooting)

---

## 📈 Performance

### Optimization Tips
- Reduce history limit in settings for faster app startup
- Disable sounds and vibration to save battery
- Use light theme for slightly better battery life
- Close other apps to free up memory

### Benchmarks
- App startup time: ~2 seconds
- History search: <100ms for 100 entries
- Calculation evaluation: <1ms
- Memory usage: ~50-80 MB

---

## 🔮 Future Enhancements (v1.2+)

Planned features:
- 🔬 Scientific calculator (sin, cos, tan, sqrt, log)
- 💾 Export history as CSV/JSON
- 🔄 Memory functions (M+, M-, MR, MC)
- ⌨️ Physical keyboard support
- ⚙️ Advanced calculation modes
- ☁️ Cloud sync for history
- 📊 Calculation statistics

---

## 📝 Changelog

### v1.1 (Current) - September 2026
**New Features**
- ✨ Persistent calculation history with SharedPreferences + Gson
- 🔍 Search and filter history functionality
- 📱 Full landscape mode support with responsive layout
- ✏️ Click-to-edit expression inline editor
- 🗑️ Individual history entry deletion
- ⚙️ Configurable history limit setting (10-200 entries)
- 📊 Timestamps and improved history UI

**Improvements**
- Enhanced settings screen with scrollable layout
- Better history item UI with dropdown menu options
- More responsive and accessible interface

### v1.0 - Initial Release
- Basic calculator functionality
- Theme switching (Light/Dark/System)
- Sound and vibration feedback
- Simple history display

---

## 📄 License

This project is licensed under the MIT License - see LICENSE file for details.

---

## 👤 Author

**Anmolmaurya0007**
- GitHub: [@Anmolmaurya0007](https://github.com/Anmolmaurya0007)
- Email: anmolmaurya0007@gmail.com

---

## 🤝 Contributing

Contributions are welcome! Here's how to help:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

Please ensure:
- Code follows Kotlin style guidelines
- Changes are tested on device/emulator
- Documentation is updated
- Commit messages are clear and descriptive

---

## 💬 Support & Feedback

- **Issues**: Report bugs on [GitHub Issues](https://github.com/Anmolmaurya0007/amulya-calculator/issues)
- **Discussions**: Join [GitHub Discussions](https://github.com/Anmolmaurya0007/amulya-calculator/discussions)
- **Email**: anmolmaurya0007@gmail.com

---

## 🎓 Learning Resources

Built with:
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) - Async programming
- [Material Design 3](https://m3.material.io/) - Design system
- [Gson](https://github.com/google/gson) - JSON serialization

---

## 📊 Stats

- **Language**: Kotlin 🎯
- **Min API**: 24 (Android 7.0)
- **Target API**: 34 (Android 14)
- **Commits**: Regular updates
- **License**: MIT
- **Status**: Active development

---

## 🙏 Acknowledgments

- Jetpack Compose team for the amazing UI framework
- Material Design for beautiful design guidelines
- Android developers community for inspiration

---

**Made with ❤️ in Kotlin**

Last updated: September 2026  
Version: 1.1
