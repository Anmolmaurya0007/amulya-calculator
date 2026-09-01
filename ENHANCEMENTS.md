# Amulya Calculator - Enhancement Guide

## Overview
The Amulya calculator has been enhanced with three major features:
1. **Persistent History** - Save and manage calculation history across app sessions
2. **Landscape Mode Support** - Responsive layout for portrait and landscape orientations
3. **Expression Editor** - Click on the display to edit expressions inline

---

## Feature 1: Persistent History (Feature #2)

### What's New
- **Automatic Saving**: All calculations are automatically saved to device storage using SharedPreferences
- **Search/Filter**: Search through history by expression or result
- **Individual Deletion**: Delete specific history entries with a dropdown menu
- **History Limit**: Configurable history limit in settings (10-200 entries)
- **Timestamps**: Each entry shows when the calculation was made

### How It Works

#### HistoryManager Class
```kotlin
class HistoryManager(private val context: Context) {
    fun saveHistory(entries: List<HistoryEntry>)     // Save to SharedPreferences
    fun loadHistory(): List<HistoryEntry>            // Load on startup
    fun clearHistory()                               // Clear all entries
    fun deleteEntry(id: String, entries: List<HistoryEntry>): List<HistoryEntry>
}
```

#### HistoryEntry Data Class
```kotlin
data class HistoryEntry(
    val expression: String,  // e.g., "10 + 5"
    val result: String,      // e.g., "15"
    val timestamp: Long,     // System.currentTimeMillis()
    val id: String           // Unique identifier for deletion
)
```

### Using History

1. **View History**: Tap the History icon (⏰) in the top-left of the calculator
2. **Search**: Use the search field to filter by expression or result
3. **Use Result**: Tap any history item to use its result as the new expression
4. **Copy Expression**: Tap the menu (⋮) and select "Copy Expression" to edit the original calculation
5. **Delete Entry**: Tap the menu (⋮) and select "Delete" to remove a single entry
6. **Clear All**: Tap the trash icon (🗑️) to delete all history

### Technical Details
- Uses **SharedPreferences** for storage (data persists across app restarts)
- Uses **Gson** library for JSON serialization of history list
- Stored key: `"history_entries"`
- File location: `/data/data/com.example.amulya/shared_prefs/amulya_prefs.xml`

---

## Feature 2: Landscape Mode Support (Feature #5)

### What's New
- **Responsive Layout**: Settings screen now uses `verticalScroll()` for landscape compatibility
- **Improved UX**: All content is accessible in both portrait and landscape modes
- **Smooth Transitions**: Layout automatically adjusts when device is rotated

### Implementation

#### Settings Screen Enhancement
```kotlin
Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
    // Theme selector
    // Sound toggle
    // Vibration toggle
    // History limit slider
}
```

#### Key Changes
1. Wrapped settings content in `Column` with `verticalScroll()`
2. Settings remain fully functional in landscape
3. Display screen maintains fixed width constraint with `widthIn(max = 380.dp)`

### Supported Orientations
- ✅ Portrait (default)
- ✅ Landscape
- ✅ Auto-rotation (if enabled in device settings)

### Testing Landscape Mode
1. Enable device rotation in settings
2. Rotate device to landscape
3. All screens adapt automatically
4. Calculator buttons remain accessible

---

## Feature 3: Expression Editor (Feature #10)

### What's New
- **Click to Edit**: Tap the display to edit the current expression
- **Dialog Interface**: Clean modal dialog for editing
- **Safe Editing**: Cancel without changes or apply to update expression
- **Non-destructive**: "Cancel" button discards changes

### How It Works

#### ExpressionEditorDialog Composable
```kotlin
@Composable
fun ExpressionEditorDialog(
    colors: AmulyaColors,
    expression: String,
    onDismiss: () -> Unit,
    onApply: (String) -> Unit,
)
```

### Using Expression Editor

1. **Open Editor**: Tap the display text (showing your expression)
2. **Edit**: Modify the expression using the keyboard
3. **Apply**: Tap "Apply" to update and close the dialog
4. **Cancel**: Tap "Cancel" to discard changes
5. **Feedback**: Sound and vibration feedback on dialog actions

### Features
- Only available when expression is not empty and not "Error"
- TextField with same theme colors as calculator
- Apply/Cancel buttons with feedback
- Expression is set to `justEvaluated = false` after editing (ready for new input or calculation)

### Example Workflow
```
User enters: 10 + 5
Display shows: "10 + 5"
User taps display → Editor opens with "10 + 5"
User changes to: 10 + 6
User taps Apply → Expression updates to "10 + 6"
User can now press = to get result
```

---

## Additional Enhancements

### History Item Actions
Each history entry now has a three-option menu:
1. **Use Result** - Load the result as new expression
2. **Copy Expression** - Load the original expression for editing
3. **Delete** - Remove this entry from history

### Settings Enhancements
- **History Limit Slider**: Adjust how many calculations to keep (10-200)
- **Live Preview**: Changes preview immediately in settings mode
- **Persistent Settings**: Theme, sounds, and vibration preferences saved

### UI/UX Improvements
- Search field in history for quick filtering
- Empty state messages for history and search results
- Timestamps in 12-hour format with AM/PM
- More info button (⋮) on history items for actions

---

## Dependencies Added

### Gson Library
```gradle
implementation("com.google.code.gson:gson:2.10.1")
```
- Used for JSON serialization/deserialization of history
- Lightweight and reliable
- No conflicts with existing dependencies

---

## File Structure

```
app/src/main/kotlin/com/example/amulya/
├── MainActivity.kt              (Enhanced main file)
│   ├── HistoryManager          (Persistence logic)
│   ├── AmulyaRoot              (Main composable)
│   ├── CalculatorScreen        (Display + buttons)
│   ├── SettingsScreen          (With landscape scroll)
│   ├── HistoryScreen           (With search & filter)
│   ├── HistoryItem             (With dropdown menu)
│   └── ExpressionEditorDialog  (Click-to-edit)
```

---

## Configuration

### AppSettings Data Class
```kotlin
data class AppSettings(
    val theme: ThemeMode = ThemeMode.LIGHT,
    val buttonSounds: Boolean = true,
    val vibration: Boolean = false,
    val historyLimit: Int = 100,  // NEW: Configurable limit
)
```

### Customization Options

1. **Change History Limit**
   - In Settings → History Limit slider (10-200)
   - Default: 100 entries

2. **Change Theme**
   - Light, Dark, or System (follows device setting)
   - Applied on next calculation or reopening app

3. **Toggle Feedback**
   - Button Sounds: On/Off
   - Vibration: On/Off
   - Changes take effect immediately

---

## Testing Checklist

### Persistent History
- [ ] Make 5 calculations
- [ ] Close and reopen app
- [ ] Verify history still exists
- [ ] Search for a calculation
- [ ] Delete an entry and verify it's gone
- [ ] Delete all and verify history is empty

### Landscape Mode
- [ ] Rotate device to landscape
- [ ] All buttons remain visible
- [ ] Settings screen scrolls properly
- [ ] Calculator still functions
- [ ] Rotate back to portrait smoothly

### Expression Editor
- [ ] Tap display to open editor
- [ ] Edit expression and tap Apply
- [ ] Verify expression updates
- [ ] Tap Cancel to discard changes
- [ ] Verify Cancel doesn't modify expression
- [ ] Use "Copy Expression" from history to open in editor

### General
- [ ] All sounds and vibrations work (if enabled)
- [ ] Theme changes apply correctly
- [ ] Search filters work (case-insensitive)
- [ ] No crashes or errors

---

## Troubleshooting

### History Not Saving
- Ensure app has permission to write to storage
- Check that SharedPreferences is accessible
- Verify Gson dependency is properly included

### Landscape Not Rotating
- Enable "Auto-rotate" in device settings
- Check that app hasn't disabled rotation in manifest
- Rotate past 90 degrees to trigger rotation

### Expression Editor Not Opening
- Expression must not be empty
- Expression must not be "Error"
- Tap directly on the text, not the box padding

### Search Not Finding Results
- Search is case-insensitive
- Search checks both expression AND result
- Partial matches are supported

---

## Future Enhancement Ideas

1. **Export History**: Save calculations as CSV or JSON file
2. **Scientific Mode**: Add sin, cos, tan, sqrt, log, etc.
3. **Memory Functions**: M+, M-, MR, MC buttons
4. **Keyboard Input**: Physical keyboard support for numbers/operators
5. **Undo/Redo**: Quick undo of calculation
6. **Calculator Modes**: Standard, Scientific, Programmer
7. **Custom Themes**: User-defined color schemes
8. **Cloud Sync**: Sync history across devices

---

## Version Info

**Enhanced Version**: 1.1
**Last Updated**: September 2026
**Target Android**: 24+ (API 7.0+)
**Kotlin Version**: 1.9+

---

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review logcat output for error messages
3. Verify all dependencies are properly installed
4. Ensure manifest has required permissions

