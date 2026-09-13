# JPaint — JavaFX Studio

A desktop drawing app for creating and arranging rectangles, ellipses, and
triangles. Built with JavaFX controls, vector shapes, and CSS styling.

## Features

- Draw with live previews and fill, outline, or combined shading.
- Choose primary and secondary colors using a color wheel, brightness slider,
  RGB values, hex input, or palette swatches.
- Select, move, copy, paste, delete, group, and ungroup shapes.
- Undo and redo edits, including movement and nested groups.
- Work on a scrollable 1600 × 1000 artboard with keyboard shortcuts.
- Package a standalone desktop app with Java and JavaFX included.

**Drawings are session-only.** Closing JPaint discards the drawing. Saving,
opening drawing files, and exporting images are not implemented.

## Launch from source

Clone the repository, then run the launcher from its root:

```bash
git clone https://github.com/moyarich/JPaint.git
cd JPaint
./run.sh
```

A compatible JDK is required. Use **JDK 26 with JavaFX 26.0.2**, or **JDK 21
with the JavaFX 21.0.8 fallback**. The fallback supports JDK 17 or newer.

The launcher detects Java automatically. On the configured Mac, it recognizes
`/Library/Java/JavaVirtualMachines/javafx-sdk-26.0.2`. Because that installation
contains Intel libraries, an Apple Silicon Java runtime uses a cached Apple
Silicon SDK of the same version instead. The system SDK remains untouched.
Other machines use the JavaFX 21.0.8 download fallback unless `JAVAFX_HOME` is set.

First-time dependency downloads require Internet access, `curl`, and `unzip`.
Subsequent launches use the cache. No Maven, Gradle, or mandatory `fzf` setup is
needed. See [the developer guide](readme-dev.md) for configuration and troubleshooting.

## Launcher commands

| Command | Action |
| --- | --- |
| `./run.sh` | Compile and launch |
| `./run.sh build` | Compile only |
| `./run.sh test` | Run model regression checks |
| `./run.sh smoke` | Check the JavaFX UI and interactions; desktop required |
| `./run.sh java` | Show the detected Java tools |
| `./run.sh package` | Build a standalone app |
| `./run.sh menu` | Open the optional interactive menu |

## Standalone macOS app

```bash
./run.sh package
open dist/JPaint.app
```

You can also double-click `dist/JPaint.app` or move it into Applications. The
packaged app includes its own runtime, so it does not need Java installed to run.
Quit the old version before opening a rebuilt app. Previous packages are kept in
`dist/previous.*`. Generated apps are local build outputs, not tracked in Git.

## Keyboard shortcuts

Use **Command** on macOS and **Ctrl** elsewhere.

| Shortcut | Action |
| --- | --- |
| Command/Ctrl + Z | Undo |
| Command/Ctrl + Shift + Z, or Command/Ctrl + Y | Redo |
| Command/Ctrl + C / V | Copy / Paste |
| Command/Ctrl + G / Shift + G | Group / Ungroup |
| Command/Ctrl + A | Select all |
| Delete or Backspace | Delete selection |
| Escape | Cancel a gesture and clear selection |

## Branches

- [`main`](https://github.com/moyarich/JPaint/tree/main): current JavaFX app.
- [`javafx-rewrite`](https://github.com/moyarich/JPaint/tree/javafx-rewrite): JavaFX rewrite branch.
- [`swing-code`](https://github.com/moyarich/JPaint/tree/swing-code): preserved Swing app,
  including its color wheel, UI improvements, tests, and packaging.

Historical documents in `Project_information/` describe the original project.
Use the Swing branch when referring to its original architecture.
