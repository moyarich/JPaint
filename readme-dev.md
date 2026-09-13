# JPaint developer guide — JavaFX

## Prerequisites

Use a JDK 17 or newer; JDK 21 is recommended with the pinned JavaFX 21.0.8 release.
The launcher detects JAVA_HOME, registered macOS JDKs, Homebrew OpenJDK, or PATH.
It validates both java and javac. Packaging also needs jar and jpackage.

On macOS with Homebrew:

```bash
brew install openjdk@21
./run.sh java
```

`/usr/libexec/java_home` is a command, not a folder. Do not use `cd` with it.
For a registered JDK, optionally set:

```bash
export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
```

## Run and build

```bash
cd /private/var/www/+ai-apps/JPaint
./run.sh              # Compile and launch JavaFX Studio
./run.sh build        # Compile only
./run.sh test         # 26 model regression checks, no graphical desktop needed
./run.sh smoke        # Native JavaFX UI/renderer/picker smoke checks
./run.sh java         # Report detected Java tools without compiling
./run.sh package      # Create a standalone app
./run.sh menu         # Optional fzf menu, with a built-in Bash fallback
```

Scripts work from other directories and checkout paths with spaces. Use
`bash run.sh` if execution permissions are missing. Bash, curl, and unzip are
needed for automatic setup; no Maven or Gradle installation is necessary.

First build downloads the platform-specific JavaFX SDK from Gluon into `.javafx/`.
Packaging also downloads JavaFX jmods. Downloads are version-pinned and cached;
the launcher exits on download failures. Network access is needed for first setup.
For offline builds, supply matching local platform/architecture SDK and jmods:

```bash
export JAVAFX_HOME=/path/to/javafx-sdk-21.0.8
export JAVAFX_JMODS=/path/to/javafx-jmods-21.0.8
```

The module is `jpaint`, main class `jpaint.Main`. JavaFX modules are supplied on
the module path. IDE users should configure JDK 17+, the JavaFX SDK module path,
`src/main/java` as sources and `src/main/resources` as resources. Follow the
[official JavaFX setup guide](https://openjfx.io/openjfx-docs/) for IDE details.

## Package and launch the app

```bash
./run.sh package
open dist/JPaint.app
```

On macOS, double-click `dist/JPaint.app` or move it to Applications. It includes
Java and JavaFX; no separate runtime is needed. Quit any old app before opening
the new one. Rebuilding preserves previous packages in `dist/previous.*`.

Build on the destination OS and architecture. macOS Apple Silicon was validated;
Linux/Windows platform paths are supported by the script but have not been tested
here. Some platform/architecture combinations may need a manually supplied SDK.
This is an app image, not a DMG or MSI installer. Distribution to other Macs
requires appropriate signing/notarization; this build is for local use.

## Controls

- Draw, Select, Move are directly accessible mode buttons.
- Shape and Shading dropdowns change newly drawn shapes.
- Primary/Secondary open a JavaFX color wheel. Angle selects hue; distance from
  the center selects saturation. Brightness, RGB, hex, and swatches stay in sync.
  Arrow keys on the wheel adjust hue/saturation. Apply commits; Cancel preserves
  the previous color. Primary is fill or outline-only stroke; Secondary is the
  stroke when using Fill and outline.
- Drag to draw. Select by clicking the topmost shape or dragging an overlap region.
- Move drags the current selection; one gesture is one undo step.
- Group two or more objects; Ungroup releases one level of a nested group.
- Copy/paste creates independent objects with successive 24-pixel offsets.
- Escape cancels a gesture and clears selection.

Use Command on macOS, Ctrl elsewhere: Z undo; Shift+Z/Y redo; C copy; V paste;
G group; Shift+G ungroup; A select all. Delete/Backspace deletes selected objects.
The artboard is 1600 × 1000 and scrollable. Drawings are session-only; no persistence
or file export is implemented.

## Implementation and validation

- `src/main/java/jpaint/model`: immutable artwork, groups, selection, clipboard,
  snapshot-based undo/redo. No UI dependencies.
- `src/main/java/jpaint/ui`: JavaFX shape renderer and color wheel.
- `src/main/java/jpaint/Main.java`: JavaFX layout, controls, and mouse/keyboard input.
- `src/main/resources/jpaint/studio.css`: visual styling.
- `tests/RegressionTest.java`: movement, nested grouping, layer order, history,
  clipboard independence, hit testing, reverse drags, and no-op handling.

The smoke command starts JavaFX, renders artwork and the picker, writes diagnostic
PPM renders to build/, and exits with an error on failed assertions. It requires a
desktop session. With JDKs newer than 21, JavaFX 21 may print upstream deprecation
warnings; prefer JDK 21 if those are distracting.

## Branch history

`swing-code` preserves the prior complete Swing app. `javafx-rewrite` contains
this implementation; `main` is promoted after validation. Switch branches with a
clean working tree. Build, cache, and packaged app folders are ignored by Git and
are shared when switching branches. Rebuild after switching; do not assume the
app in dist/ matches the currently checked-out branch.
