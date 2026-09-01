# Amulya Calculator

A beautiful, minimal calculator app with History and Settings screens, built with Python's Tkinter GUI toolkit.

## Features

- **Clean Calculator Interface** — Intuitive keypad with digits, operations, and special functions
- **Dark & Light Themes** — Seamlessly switch between carefully designed color schemes
- **Calculation History** — Track all your calculations with timestamps
- **Settings Screen** — Customize theme and enable/disable button sounds
- **Safe Arithmetic** — Restricted expression evaluation prevents code injection
- **No Dependencies** — Uses only Python's standard library (Tkinter)

## Installation

### Requirements
- Python 3.6+
- Tkinter (usually included with Python)

### Run the App

```bash
python amulya_calculator.py
```

## Usage

### Calculator Screen
- **Keypad**: Enter numbers and arithmetic operations (+, −, ×, ÷)
- **AC**: Clear all
- **⌫**: Backspace (delete last character)
- **.**: Decimal point (one per number)
- **=**: Calculate and add to history
- **⏱ Clock**: Open History screen
- **⚙ Gear**: Open Settings screen

### History Screen
- View all past calculations in chronological order (newest first)
- Each entry shows timestamp, expression, and result
- **🗑 Trash**: Delete entire history

### Settings Screen
- **Theme**: Toggle between Light and Dark modes
- **Button Sounds**: Enable or disable click sounds
- **✓ Check**: Save changes and return to calculator
- **← Back**: Cancel changes and return to calculator

## Architecture

### Main Components

**`AmulyaApp` Class**
- Root Tkinter window managing three screens
- State management for expressions, history, and settings
- Screen navigation via `clear_container()` and screen methods

**Three Screens**
1. **Calculator** (`show_calculator`) — Main interface with keypad and display
2. **Settings** (`show_settings`) — Theme and sound preferences with draft mode
3. **History** (`show_history`) — Chronological log of calculations

**Safe Evaluation**
- `safe_eval(expr)` — Restricts input to arithmetic characters only (`0-9+-*/(). `)
- Uses `eval()` with an empty builtins dictionary for security
- Catches exceptions and invalid types gracefully

**Theming**
- Two complete color palettes (light/dark)
- Active theme determined by current settings (or draft settings during configuration)
- All UI elements updated dynamically on theme switch

## Project Structure

```
amulya-calculator/
├── amulya_calculator.py    # Main application
├── README.md               # This file
└── LICENSE                 # License (if applicable)
```

## Technical Details

- **Language**: Python 3
- **GUI Framework**: Tkinter
- **Window Size**: 360×640 pixels (phone-like aspect ratio)
- **Precision**: Results rounded to 10 decimal places
- **History Storage**: In-memory list (persists during session, resets on app close)

## Future Enhancements

Potential features for future versions:
- Persistent history storage (JSON or SQLite)
- Additional operations (square root, exponentiation, trigonometry)
- Keyboard input support
- Custom theme creation
- Undo/Redo functionality
- Scientific notation support

## License

This project is open source. See LICENSE file for details.

---

Made with ❤️ by Anmol Maurya
