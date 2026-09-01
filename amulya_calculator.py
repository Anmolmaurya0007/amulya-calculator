"""
Amulya - a small calculator app with History and Settings screens,
built with Tkinter (Python's standard GUI toolkit).

Run with:  python amulya_calculator.py
"""

import tkinter as tk
from tkinter import font as tkfont
from datetime import datetime

THEMES = {
    "light": {
        "bg": "#EFE9DC",
        "frame": "#FBF8F1",
        "display": "#F7F3E8",
        "ink": "#2B2620",
        "sub": "#8A8172",
        "key": "#FFFFFF",
        "key_op": "#F1E6D2",
        "key_op_text": "#8A5A2B",
        "accent": "#B8763A",
        "accent2": "#2F6F62",
        "divider": "#E4DCC8",
        "danger": "#B4472F",
    },
    "dark": {
        "bg": "#1B1812",
        "frame": "#241F17",
        "display": "#241F17",
        "ink": "#F3EEE2",
        "sub": "#9A917E",
        "key": "#2E2A20",
        "key_op": "#3A2E1D",
        "key_op_text": "#E3A45E",
        "accent": "#E3A45E",
        "accent2": "#5FB6A2",
        "divider": "#352F24",
        "danger": "#E38168",
    },
}

ALLOWED_CHARS = set("0123456789+-*/(). ")


def safe_eval(expr):
    """Evaluate a plain arithmetic expression, or return None if unsafe/invalid."""
    if not expr or any(ch not in ALLOWED_CHARS for ch in expr):
        return None
    try:
        # Only digits/operators reach eval, so this is safe arithmetic only.
        result = eval(expr, {"__builtins__": {}}, {})
        if not isinstance(result, (int, float)):
            return None
        return round(result, 10)
    except Exception:
        return None


class AmulyaApp(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("Amulya")
        self.geometry("360x640")
        self.resizable(False, False)

        # --- app state ---
        self.expr = ""
        self.just_evaluated = False
        self.history = []  # list of dicts: expression, result, ts
        self.settings = {"theme": "light", "button_sounds": True}
        self.draft_settings = None

        self.container = tk.Frame(self)
        self.container.pack(fill="both", expand=True)

        self.show_calculator()

    # ---------- helpers ----------
    def theme(self):
        active = self.draft_settings if self.draft_settings else self.settings
        return THEMES[active["theme"]]

    def clear_container(self):
        for w in self.container.winfo_children():
            w.destroy()

    def beep(self):
        if self.settings["button_sounds"]:
            self.bell()

    # ---------- calculator screen ----------
    def show_calculator(self):
        self.clear_container()
        t = self.theme()
        self.configure(bg=t["bg"])
        self.container.configure(bg=t["bg"])

        frame = tk.Frame(self.container, bg=t["frame"], padx=16, pady=16)
        frame.pack(padx=14, pady=20, fill="both", expand=True)

        # top bar
        top = tk.Frame(frame, bg=t["frame"])
        top.pack(fill="x", pady=(0, 12))
        tk.Button(
            top, text="\u23F1", command=self.show_history, bd=0,
            bg=t["frame"], fg=t["sub"], font=("Segoe UI", 12),
            activebackground=t["frame"],
        ).pack(side="left")
        tk.Label(
            top, text="Amulya", bg=t["frame"], fg=t["ink"],
            font=("Segoe UI", 12, "bold"),
        ).pack(side="left", expand=True)
        tk.Button(
            top, text="\u2699", command=self.show_settings, bd=0,
            bg=t["frame"], fg=t["sub"], font=("Segoe UI", 12),
            activebackground=t["frame"],
        ).pack(side="right")

        # display
        display_frame = tk.Frame(frame, bg=t["display"], padx=14, pady=14)
        display_frame.pack(fill="x", pady=(0, 14))
        self.display_var = tk.StringVar(value=self.expr or "0")
        tk.Label(
            display_frame, textvariable=self.display_var, bg=t["display"],
            fg=t["ink"], font=("Consolas", 30, "bold"), anchor="e",
            justify="right", wraplength=300,
        ).pack(fill="x")

        # keypad
        keys = [
            ["AC", "\u232B", ".", "+"],
            ["7", "8", "9", "\u00F7"],
            ["4", "5", "6", "\u00D7"],
            ["1", "2", "3", "\u2212"],
            ["0", "="],
        ]
        for row in keys:
            row_frame = tk.Frame(frame, bg=t["frame"])
            row_frame.pack(fill="x", pady=4)
            for key in row:
                is_op = key in "\u00F7\u00D7\u2212+"
                is_eq = key == "="
                is_util = key in ("AC", "\u232B", ".")
                bg = t["key"]
                fg = t["ink"]
                if is_op:
                    bg, fg = t["key_op"], t["key_op_text"]
                if is_eq:
                    bg, fg = t["accent"], "#FFFFFF"
                if is_util:
                    fg = t["sub"]
                width = 12 if key == "0" else (12 if key == "=" else 5)
                btn = tk.Button(
                    row_frame, text=key, bg=bg, fg=fg, bd=0,
                    font=("Consolas", 14, "bold"), height=2,
                    activebackground=bg,
                    command=lambda k=key: self.on_key(k),
                )
                btn.pack(side="left", expand=True, fill="x", padx=3)

    def on_key(self, key):
        self.beep()
        if key == "AC":
            self.expr = ""
            self.just_evaluated = False
        elif key == "\u232B":
            self.expr = self.expr[:-1]
            self.just_evaluated = False
        elif key == ".":
            parts = [self.expr]
            for op in "+-*/":
                parts = sum((p.split(op) for p in parts), [])
            if "." not in parts[-1]:
                self.expr = "0." if self.expr == "" else self.expr + "."
            self.just_evaluated = False
        elif key in "\u00F7\u00D7\u2212+":
            op_map = {"\u00F7": "/", "\u00D7": "*", "\u2212": "-", "+": "+"}
            op = op_map[key]
            self.just_evaluated = False
            if self.expr and self.expr[-1] in "+-*/":
                self.expr = self.expr[:-1] + op
            elif self.expr or op == "-":
                self.expr += op
        elif key == "=":
            self.equals()
        else:
            if self.just_evaluated:
                self.expr = key
                self.just_evaluated = False
            else:
                self.expr = key if self.expr == "0" else self.expr + key

        self.display_var.set(self.expr if self.expr else "0")

    def equals(self):
        if not self.expr:
            return
        value = safe_eval(self.expr)
        if value is None:
            self.display_var.set("Error")
            return
        self.history.insert(
            0, {"expression": self.expr, "result": value, "ts": datetime.now()}
        )
        self.expr = str(value)
        self.just_evaluated = True

    # ---------- settings screen ----------
    def show_settings(self):
        if self.draft_settings is None:
            self.beep()
            self.draft_settings = dict(self.settings)
        self.clear_container()
        t = self.theme()

        frame = tk.Frame(self.container, bg=t["frame"], padx=16, pady=16)
        frame.pack(padx=14, pady=20, fill="both", expand=True)

        top = tk.Frame(frame, bg=t["frame"])
        top.pack(fill="x", pady=(0, 18))
        tk.Button(
            top, text="\u2190", command=self.cancel_settings, bd=0,
            bg=t["frame"], fg=t["sub"], font=("Segoe UI", 12),
        ).pack(side="left")
        tk.Label(
            top, text="Settings", bg=t["frame"], fg=t["ink"],
            font=("Segoe UI", 12, "bold"),
        ).pack(side="left", expand=True)
        tk.Button(
            top, text="\u2713", command=self.save_settings, bd=0,
            bg=t["frame"], fg=t["accent2"], font=("Segoe UI", 12, "bold"),
        ).pack(side="right")

        tk.Label(
            frame, text="Theme", bg=t["frame"], fg=t["sub"],
            font=("Segoe UI", 10), anchor="w",
        ).pack(fill="x", pady=(0, 6))
        theme_row = tk.Frame(frame, bg=t["frame"])
        theme_row.pack(fill="x", pady=(0, 20))
        for opt, label in [("light", "Light"), ("dark", "Dark")]:
            active = self.draft_settings["theme"] == opt
            tk.Button(
                theme_row, text=label,
                bg=t["key_op"] if active else t["key"],
                fg=t["key_op_text"] if active else t["ink"],
                bd=1, relief="solid",
                command=lambda o=opt: self.set_draft_theme(o),
            ).pack(side="left", expand=True, fill="x", padx=3)

        tk.Label(
            frame, text="Button sounds", bg=t["frame"], fg=t["sub"],
            font=("Segoe UI", 10), anchor="w",
        ).pack(fill="x", pady=(0, 6))
        sound_label = "On" if self.draft_settings["button_sounds"] else "Off"
        tk.Button(
            frame, text=sound_label, bg=t["key"], fg=t["ink"], bd=1,
            relief="solid", width=8, command=self.toggle_draft_sound,
        ).pack(anchor="w")

    def set_draft_theme(self, opt):
        self.draft_settings["theme"] = opt
        self.show_settings()

    def toggle_draft_sound(self):
        self.draft_settings["button_sounds"] = not self.draft_settings["button_sounds"]
        self.show_settings()

    def save_settings(self):
        self.settings = dict(self.draft_settings)
        self.draft_settings = None
        self.show_calculator()

    def cancel_settings(self):
        self.draft_settings = None
        self.show_calculator()

    # ---------- history screen ----------
    def show_history(self):
        self.beep()
        self.clear_container()
        t = self.theme()

        frame = tk.Frame(self.container, bg=t["frame"], padx=16, pady=16)
        frame.pack(padx=14, pady=20, fill="both", expand=True)

        top = tk.Frame(frame, bg=t["frame"])
        top.pack(fill="x", pady=(0, 12))
        tk.Button(
            top, text="\u2190", command=self.show_calculator, bd=0,
            bg=t["frame"], fg=t["sub"], font=("Segoe UI", 12),
        ).pack(side="left")
        tk.Label(
            top, text="History", bg=t["frame"], fg=t["ink"],
            font=("Segoe UI", 12, "bold"),
        ).pack(side="left", expand=True)
        tk.Button(
            top, text="\U0001F5D1", command=self.delete_history, bd=0,
            bg=t["frame"], fg=t["danger"], font=("Segoe UI", 12),
        ).pack(side="right")

        if not self.history:
            tk.Label(
                frame, text="No calculations yet", bg=t["frame"], fg=t["ink"],
                font=("Segoe UI", 11, "bold"),
            ).pack(pady=(60, 4))
            tk.Label(
                frame, text="Results you calculate will show up here.",
                bg=t["frame"], fg=t["sub"], font=("Segoe UI", 9),
            ).pack()
            return

        canvas = tk.Canvas(frame, bg=t["frame"], bd=0, highlightthickness=0)
        canvas.pack(fill="both", expand=True)
        for entry in self.history:
            row = tk.Frame(canvas, bg=t["key"], padx=12, pady=8)
            row.pack(fill="x", pady=4)
            tk.Label(
                row, text=entry["ts"].strftime("%I:%M %p"), bg=t["key"],
                fg=t["sub"], font=("Segoe UI", 8), anchor="w",
            ).pack(fill="x")
            tk.Label(
                row, text=entry["expression"], bg=t["key"], fg=t["sub"],
                font=("Consolas", 10), anchor="w",
            ).pack(fill="x")
            tk.Label(
                row, text=str(entry["result"]), bg=t["key"], fg=t["ink"],
                font=("Consolas", 16, "bold"), anchor="w",
            ).pack(fill="x")

    def delete_history(self):
        self.history = []
        self.show_history()


if __name__ == "__main__":
    app = AmulyaApp()
    app.mainloop()
