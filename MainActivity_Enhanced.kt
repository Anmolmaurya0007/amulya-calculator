package com.example.amulya

// Add to AndroidManifest.xml, outside the <application> tag, for vibration to work:
// <uses-permission android:name="android.permission.VIBRATE" />

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

// ---------- feedback (sound + vibration) ----------

/** Wraps a ToneGenerator so callers don't need to think about lifecycle. */
class ClickSound {
    private var toneGenerator: ToneGenerator? = null

    private fun get(): ToneGenerator {
        var tg = toneGenerator
        if (tg == null) {
            tg = ToneGenerator(AudioManager.STREAM_SYSTEM, 60)
            toneGenerator = tg
        }
        return tg
    }

    fun play() {
        try {
            get().startTone(ToneGenerator.TONE_PROP_BEEP, 40)
        } catch (e: Exception) {
            // Some devices/emulators restrict the tone stream; fail silently.
        }
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}

fun vibrateShort(context: Context) {
    try {
        val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(12)
        }
    } catch (e: Exception) {
        // No vibrator hardware, or permission missing; fail silently.
    }
}

// ---------- theme ----------

data class AmulyaColors(
    val bg: Color,
    val frame: Color,
    val display: Color,
    val ink: Color,
    val sub: Color,
    val key: Color,
    val keyOp: Color,
    val keyOpText: Color,
    val accent: Color,
    val accent2: Color,
    val divider: Color,
    val danger: Color,
)

val LightColors = AmulyaColors(
    bg = Color(0xFFEFE9DC),
    frame = Color(0xFFFBF8F1),
    display = Color(0xFFF7F3E8),
    ink = Color(0xFF2B2620),
    sub = Color(0xFF8A8172),
    key = Color(0xFFFFFFFF),
    keyOp = Color(0xFFF1E6D2),
    keyOpText = Color(0xFF8A5A2B),
    accent = Color(0xFFB8763A),
    accent2 = Color(0xFF2F6F62),
    divider = Color(0xFFE4DCC8),
    danger = Color(0xFFB4472F),
)

val DarkColors = AmulyaColors(
    bg = Color(0xFF1B1812),
    frame = Color(0xFF241F17),
    display = Color(0xFF241F17),
    ink = Color(0xFFF3EEE2),
    sub = Color(0xFF9A917E),
    key = Color(0xFF2E2A20),
    keyOp = Color(0xFF3A2E1D),
    keyOpText = Color(0xFFE3A45E),
    accent = Color(0xFFE3A45E),
    accent2 = Color(0xFF5FB6A2),
    divider = Color(0xFF352F24),
    danger = Color(0xFFE38168),
)

enum class ThemeMode { LIGHT, DARK, SYSTEM }
enum class Screen { CALCULATOR, SETTINGS, HISTORY }

data class HistoryEntry(
    val expression: String,
    val result: String,
    val timestamp: Long,
    val id: String = UUID.randomUUID().toString()
)

data class AppSettings(
    val theme: ThemeMode = ThemeMode.LIGHT,
    val buttonSounds: Boolean = true,
    val vibration: Boolean = false,
    val historyLimit: Int = 100,
)

// ---------- persistence layer ----------

class HistoryManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("amulya_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val historyKey = "history_entries"

    fun saveHistory(entries: List<HistoryEntry>) {
        val json = gson.toJson(entries)
        prefs.edit().putString(historyKey, json).apply()
    }

    fun loadHistory(): List<HistoryEntry> {
        return try {
            val json = prefs.getString(historyKey, "[]") ?: "[]"
            val type = object : TypeToken<List<HistoryEntry>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearHistory() {
        prefs.edit().remove(historyKey).apply()
    }

    fun deleteEntry(id: String, entries: List<HistoryEntry>): List<HistoryEntry> {
        val updated = entries.filter { it.id != id }
        saveHistory(updated)
        return updated
    }
}

// ---------- calculation ----------

fun safeEvaluate(expr: String): Double? {
    val cleaned = expr.replace("÷", "/").replace("×", "*").replace("−", "-")
    if (cleaned.isEmpty() || cleaned.any { it !in "0123456789+-*/(). " }) return null
    return try {
        val result = evalExpression(cleaned)
        if (result.isNaN() || result.isInfinite()) null else Math.round(result * 1e10) / 1e10
    } catch (e: Exception) {
        null
    }
}

// Minimal recursive-descent parser: handles + - * / and parentheses, no external deps.
private fun evalExpression(input: String): Double {
    var pos = -1
    var ch = ' '

    fun nextChar() {
        pos++
        ch = if (pos < input.length) input[pos] else Char.MIN_VALUE
    }

    fun eat(charToEat: Char): Boolean {
        while (ch == ' ') nextChar()
        if (ch == charToEat) {
            nextChar()
            return true
        }
        return false
    }

    fun parseExpr(): Double {
        fun parseFactor(): Double {
            if (eat('+')) return parseFactor()
            if (eat('-')) return -parseFactor()
            val x: Double
            val startPos = pos
            if (eat('(')) {
                x = parseExpr()
                eat(')')
            } else {
                while (ch in '0'..'9' || ch == '.') nextChar()
                x = input.substring(startPos, pos).toDouble()
            }
            return x
        }

        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                if (eat('*')) x *= parseFactor()
                else if (eat('/')) x /= parseFactor()
                else return x
            }
        }

        var x = parseTerm()
        while (true) {
            if (eat('+')) x += parseTerm()
            else if (eat('-')) x -= parseTerm()
            else return x
        }
    }

    nextChar()
    return parseExpr()
}

// ---------- main entry ----------

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AmulyaRoot(this) }
    }
}

@Composable
fun AmulyaRoot(context: Context) {
    var screen by remember { mutableStateOf(Screen.CALCULATOR) }
    var expr by remember { mutableStateOf("") }
    var justEvaluated by remember { mutableStateOf(false) }
    var history by remember { mutableStateOf(listOf<HistoryEntry>()) }
    var settings by remember { mutableStateOf(AppSettings()) }
    var draftSettings by remember { mutableStateOf<AppSettings?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var editingExpr by remember { mutableStateOf<String?>(null) }

    val historyManager = remember { HistoryManager(context) }
    val clickSound = remember { ClickSound() }
    
    DisposableEffect(Unit) {
        // Load history on startup
        history = historyManager.loadHistory()
        onDispose { clickSound.release() }
    }

    fun activeSettings() = draftSettings ?: settings

    fun feedback() {
        val active = activeSettings()
        if (active.buttonSounds) clickSound.play()
        if (active.vibration) vibrateShort(context)
    }

    val systemDark = isSystemInDarkThemeCompat()
    val activeTheme = draftSettings?.theme ?: settings.theme
    val colors = when (activeTheme) {
        ThemeMode.LIGHT -> LightColors
        ThemeMode.DARK -> DarkColors
        ThemeMode.SYSTEM -> if (systemDark) DarkColors else LightColors
    }

    fun formatResult(value: Double): String =
        if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()

    fun onEquals() {
        if (expr.isEmpty()) return
        val value = safeEvaluate(expr)
        if (value == null) {
            expr = "Error"
            return
        }
        val resultText = formatResult(value)
        val newEntry = HistoryEntry(expr, resultText, System.currentTimeMillis())
        history = listOf(newEntry) + history
        historyManager.saveHistory(history)
        expr = resultText
        justEvaluated = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .background(colors.frame, RoundedCornerShape(28.dp))
                .padding(18.dp)
                .widthIn(max = 380.dp)
        ) {
            when (screen) {
                Screen.CALCULATOR -> {
                    editingExpr?.let { editing ->
                        ExpressionEditorDialog(
                            colors = colors,
                            expression = editing,
                            onDismiss = { editingExpr = null },
                            onApply = { newExpr ->
                                expr = newExpr
                                justEvaluated = false
                                editingExpr = null
                                feedback()
                            }
                        )
                    }
                    CalculatorScreen(
                        colors = colors,
                        expr = if (expr.isEmpty()) "0" else expr,
                        onHistoryClick = {
                            feedback()
                            searchQuery = ""
                            screen = Screen.HISTORY
                        },
                        onSettingsClick = {
                            feedback()
                            draftSettings = settings
                            screen = Screen.SETTINGS
                        },
                        onEditExpr = {
                            feedback()
                            editingExpr = expr.takeIf { it.isNotEmpty() && it != "Error" }
                        },
                        onKey = { key ->
                            feedback()
                            when (key) {
                                "AC" -> {
                                    expr = ""
                                    justEvaluated = false
                                }
                                "⌫" -> {
                                    expr = expr.dropLast(1)
                                    justEvaluated = false
                                }
                                "=" -> onEquals()
                                "." -> {
                                    val lastSegment = expr.split('+', '-', '×', '÷').lastOrNull() ?: ""
                                    if (!lastSegment.contains(".")) {
                                        expr = if (expr.isEmpty()) "0." else expr + "."
                                    }
                                    justEvaluated = false
                                }
                                "+", "−", "×", "÷" -> {
                                    justEvaluated = false
                                    expr = if (expr.isNotEmpty() && expr.last() in "+−×÷") {
                                        expr.dropLast(1) + key
                                    } else if (expr.isNotEmpty() || key == "−") {
                                        expr + key
                                    } else expr
                                }
                                else -> {
                                    expr = if (justEvaluated) key else if (expr == "0") key else expr + key
                                    justEvaluated = false
                                }
                            }
                        },
                    )
                }
                Screen.SETTINGS -> draftSettings?.let { draft ->
                    SettingsScreen(
                        colors = colors,
                        draft = draft,
                        onBack = {
                            feedback()
                            draftSettings = null
                            screen = Screen.CALCULATOR
                        },
                        onSave = {
                            feedback()
                            settings = draft
                            draftSettings = null
                            screen = Screen.CALCULATOR
                        },
                        onChange = {
                            feedback()
                            draftSettings = it
                        },
                    )
                }
                Screen.HISTORY -> HistoryScreen(
                    colors = colors,
                    entries = history,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    onBack = {
                        feedback()
                        searchQuery = ""
                        screen = Screen.CALCULATOR
                    },
                    onDeleteAll = {
                        feedback()
                        history = emptyList()
                        historyManager.clearHistory()
                    },
                    onDeleteEntry = { entryId ->
                        feedback()
                        history = historyManager.deleteEntry(entryId, history)
                    },
                    onUseEntry = { entry ->
                        feedback()
                        expr = entry.result
                        justEvaluated = true
                        screen = Screen.CALCULATOR
                    },
                    onCopyEntry = { entry ->
                        feedback()
                        expr = entry.expression
                        justEvaluated = false
                        screen = Screen.CALCULATOR
                    },
                )
            }
        }
    }
}

@Composable
fun isSystemInDarkThemeCompat(): Boolean =
    androidx.compose.foundation.isSystemInDarkTheme()

@Composable
fun TopBar(
    colors: AmulyaColors,
    title: String,
    leftIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onLeftClick: () -> Unit,
    rightIcon: androidx.compose.ui.graphics.vector.ImageVector,
    rightTint: Color,
    onRightClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onLeftClick) {
            Icon(leftIcon, contentDescription = null, tint = colors.sub)
        }
        Text(
            title,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = colors.ink,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )
        IconButton(onClick = onRightClick) {
            Icon(rightIcon, contentDescription = null, tint = rightTint)
        }
    }
}

@Composable
fun CalculatorScreen(
    colors: AmulyaColors,
    expr: String,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onEditExpr: () -> Unit,
    onKey: (String) -> Unit,
) {
    TopBar(
        colors = colors,
        title = "Amulya",
        leftIcon = Icons.Filled.History,
        onLeftClick = onHistoryClick,
        rightIcon = Icons.Filled.Settings,
        rightTint = colors.sub,
        onRightClick = onSettingsClick,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.display, RoundedCornerShape(18.dp))
            .clickable { onEditExpr() }
            .padding(20.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Text(
            text = expr,
            color = if (expr == "Error") colors.danger else colors.ink,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.End,
        )
    }

    Spacer(Modifier.height(14.dp))

    val rows = listOf(
        listOf("AC", "⌫", ".", "+"),
        listOf("7", "8", "9", "÷"),
        listOf("4", "5", "6", "×"),
        listOf("1", "2", "3", "−"),
    )

    rows.forEach { row ->
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            row.forEach { key -> CalcKey(colors, key, Modifier.weight(1f), onKey) }
        }
    }
    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        CalcKey(colors, "0", Modifier.weight(2f), onKey)
        CalcKey(colors, "=", Modifier.weight(2f), onKey)
    }
}

@Composable
fun CalcKey(colors: AmulyaColors, label: String, modifier: Modifier, onKey: (String) -> Unit) {
    val isOp = label in listOf("+", "−", "×", "÷")
    val isEquals = label == "="
    val isUtil = label in listOf("AC", "⌫", ".")
    val bg = when {
        isEquals -> colors.accent
        isOp -> colors.keyOp
        else -> colors.key
    }
    val fg = when {
        isEquals -> Color.White
        isOp -> colors.keyOpText
        isUtil -> colors.sub
        else -> colors.ink
    }
    Button(
        onClick = { onKey(label) },
        modifier = modifier.padding(4.dp).height(58.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = fg),
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(label, fontSize = 18.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ExpressionEditorDialog(
    colors: AmulyaColors,
    expression: String,
    onDismiss: () -> Unit,
    onApply: (String) -> Unit,
) {
    var editedExpr by remember { mutableStateOf(expression) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Expression", color = colors.ink) },
        text = {
            TextField(
                value = editedExpr,
                onValueChange = { editedExpr = it },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colors.display,
                    unfocusedContainerColor = colors.display,
                    focusedTextColor = colors.ink,
                    unfocusedTextColor = colors.ink,
                ),
                singleLine = true,
            )
        },
        confirmButton = {
            Button(
                onClick = { onApply(editedExpr) },
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent2)
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = colors.divider)
            ) {
                Text("Cancel")
            }
        },
        containerColor = colors.frame,
    )
}

@Composable
fun SettingsScreen(
    colors: AmulyaColors,
    draft: AppSettings,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onChange: (AppSettings) -> Unit,
) {
    TopBar(
        colors = colors,
        title = "Settings",
        leftIcon = Icons.Filled.ArrowBack,
        onLeftClick = onBack,
        rightIcon = Icons.Filled.Check,
        rightTint = colors.accent2,
        onRightClick = onSave,
    )

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        SettingLabel(colors, "Theme")
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
            listOf(ThemeMode.LIGHT to "Light", ThemeMode.DARK to "Dark", ThemeMode.SYSTEM to "System")
                .forEach { (mode, label) ->
                    val active = draft.theme == mode
                    OutlinedButton(
                        onClick = { onChange(draft.copy(theme = mode)) },
                        modifier = Modifier.weight(1f).padding(horizontal = 3.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (active) colors.keyOp else colors.key,
                            contentColor = if (active) colors.keyOpText else colors.ink,
                        ),
                    ) { Text(label, fontSize = 13.sp) }
                }
        }

        SettingLabel(colors, "Button sounds")
        SettingToggle(colors, draft.buttonSounds) { onChange(draft.copy(buttonSounds = it)) }

        Spacer(Modifier.height(20.dp))
        SettingLabel(colors, "Vibration")
        SettingToggle(colors, draft.vibration) { onChange(draft.copy(vibration = it)) }

        Spacer(Modifier.height(20.dp))
        SettingLabel(colors, "History Limit")
        Text("${draft.historyLimit} entries", color = colors.ink, fontSize = 14.sp)
        Slider(
            value = draft.historyLimit.toFloat(),
            onValueChange = { onChange(draft.copy(historyLimit = it.toInt())) },
            valueRange = 10f..200f,
            steps = 18,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = colors.accent2,
                activeTrackColor = colors.accent2,
            )
        )
    }
}

@Composable
fun SettingLabel(colors: AmulyaColors, text: String) {
    Text(
        text,
        color = colors.sub,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
fun SettingToggle(colors: AmulyaColors, value: Boolean, onToggle: (Boolean) -> Unit) {
    Switch(
        checked = value,
        onCheckedChange = onToggle,
        colors = SwitchDefaults.colors(
            checkedTrackColor = colors.accent2,
            uncheckedTrackColor = colors.key,
        ),
    )
}

@Composable
fun HistoryScreen(
    colors: AmulyaColors,
    entries: List<HistoryEntry>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onBack: () -> Unit,
    onDeleteAll: () -> Unit,
    onDeleteEntry: (String) -> Unit,
    onUseEntry: (HistoryEntry) -> Unit,
    onCopyEntry: (HistoryEntry) -> Unit,
) {
    TopBar(
        colors = colors,
        title = "History",
        leftIcon = Icons.Filled.ArrowBack,
        onLeftClick = onBack,
        rightIcon = Icons.Filled.Delete,
        rightTint = colors.danger,
        onRightClick = onDeleteAll,
    )

    TextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = { Text("Search...", color = colors.sub) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.display,
            unfocusedContainerColor = colors.display,
            focusedTextColor = colors.ink,
            unfocusedTextColor = colors.ink,
        ),
        singleLine = true,
    )

    val filteredEntries = if (searchQuery.isEmpty()) {
        entries
    } else {
        entries.filter {
            it.expression.contains(searchQuery, ignoreCase = true) ||
            it.result.contains(searchQuery, ignoreCase = true)
        }
    }

    if (filteredEntries.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                if (searchQuery.isEmpty()) "No calculations yet" else "No results found",
                color = colors.ink,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                if (searchQuery.isEmpty()) "Results you calculate will show up here." else "Try a different search term.",
                color = colors.sub,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
        }
        return
    }

    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }

    LazyColumn {
        items(filteredEntries, key = { it.id }) { entry ->
            HistoryItem(
                colors = colors,
                entry = entry,
                timeFormat = timeFormat,
                onUse = { onUseEntry(entry) },
                onCopy = { onCopyEntry(entry) },
                onDelete = { onDeleteEntry(entry.id) },
            )
        }
    }
}

@Composable
fun HistoryItem(
    colors: AmulyaColors,
    entry: HistoryEntry,
    timeFormat: SimpleDateFormat,
    onUse: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(colors.key, RoundedCornerShape(14.dp))
            .clickable { onUse() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(timeFormat.format(Date(entry.timestamp)), color = colors.sub, fontSize = 11.sp)
                Text(entry.expression, color = colors.sub, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                Text(
                    entry.result,
                    color = colors.ink,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = null, tint = colors.sub)
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Use Result") },
                        onClick = { onUse(); showMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Copy Expression") },
                        onClick = { onCopy(); showMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = { onDelete(); showMenu = false }
                    )
                }
            }
        }
    }
}
