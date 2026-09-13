# JPaint — developer guide

JPaint is a Java Swing desktop drawing app. It has no third-party dependencies,
web server, package manager, API keys, or database. Drawings are kept in memory;
closing the app discards them. Saving/exporting files is not implemented.

## Requirements

- A JDK, version 11 or newer (a JRE alone cannot compile the source).
- A graphical desktop for launching the interface.
- Bash for the included launcher (macOS, Linux, or Git Bash on Windows).

Check `java -version` and `javac -version`. Both must resolve to a working JDK.
If necessary, set JAVA_HOME to your JDK home directory, containing bin/java and
bin/javac. On macOS, after installing a JDK:

    /usr/libexec/java_home
    export JAVA_HOME="$(/usr/libexec/java_home)"

`/usr/libexec/java_home` is an executable command, not a folder; do not `cd` to it.
You normally do not need JAVA_HOME: the launcher checks it, registered macOS JDKs,
Homebrew OpenJDK installations, and PATH. Invalid or old JDKs are skipped with
an explanation. If no JDK is installed, on macOS with Homebrew run:

    brew install openjdk@21

Then launch again. The launcher does not install software automatically.

## Launch

From Terminal:

    cd /private/var/www/+ai-apps/JPaint
    ./run.sh

The launcher compiles the source into build/classes and opens the desktop window.
You can run /absolute/path/to/JPaint/run.sh from another directory too.
If your checkout is elsewhere, replace the path above with that location.

    ./run.sh build     # Compile only
    ./run.sh test      # Compile and run headless regression checks
    ./run.sh java      # Report the detected Java tools without compiling
    ./run.sh menu      # Optional menu; uses fzf when available, otherwise Bash
    ./run.sh --help

The root launcher delegates to scripts/run.sh and scripts/run-app.sh. Both script
paths also accept direct commands. No fzf installation is required. Commands work
from other directories and from paths containing spaces.

To launch without Bash, run these commands from the project root in PowerShell:

    New-Item -ItemType Directory -Force build/classes | Out-Null
    Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { '"' + $_.FullName + '"' } | Set-Content build/sources.txt
    javac --release 11 -encoding UTF-8 -d build/classes '@build/sources.txt'
    java -cp build/classes main.Main

In IntelliJ IDEA, open the project, select a JDK 11+ as the project SDK, mark src
as Sources Root, and run main.Main. No Maven or Gradle setup is needed.

## Drawing and editing

- Choose the mode, shape, shading, and colors in the left sidebar.
- Click Primary or Secondary to open the color picker. Drag the color wheel (angle selects hue, distance from the center selects saturation),
  adjust brightness or RGB values, choose palette swatches, or enter a six-digit hex color such as #4F46E5. Press Enter
  to preview a hex value, then Apply color to apply. Cancel preserves the previous color.
  Sidebar swatches show the selected colors. Colors affect newly drawn shapes;
  primary is the fill (or outline-only stroke), secondary is the combined-style outline.
- Draw: drag across the white canvas; the dashed outline previews the shape.
- Select: click a shape or drag a region to select overlapping objects.
- Move: drag to move the current selection. One gesture is one undo step.
- Copy/paste creates independent shapes, offset on each successive paste.
- Select two or more objects to group them. Select a group to ungroup it.
- Scroll to reach the rest of the 1600 × 1000 canvas.

Shortcuts use Command on macOS and Ctrl on other platforms:

    Z             Undo
    Shift+Z / Y   Redo
    C / V         Copy / Paste
    G / Shift+G   Group / Ungroup
    Delete or Backspace   Delete selected objects (no modifier)

## Troubleshooting

“Unable to locate a Java Runtime” / JDK requirement message: install a JDK and
set JAVA_HOME to its home folder. The launcher honors JAVA_HOME when set.

“HeadlessException” / no display: launch in a desktop session. Use ./run.sh test
for automated validation on machines without a display.

If scripts are not executable, use `bash run.sh`.

## Code layout and verification

src/main: startup on Swing's event dispatch thread.
src/view: window, dialogs, keyboard shortcuts, and repaint-based rendering.
src/controller: button actions and mouse gestures.
src/model: shapes, groups, selection, clipboard, and undoable commands.
tests/RegressionTest.java: movement/history, copy/group/paste, delete ordering,
empty-command history preservation, and offscreen rendering regression checks.

Run ./run.sh test after changing behavior. For visual checks, launch the app,
draw each shape in both drag directions, change shading/colors, then select,
move, group, copy/paste, undo/redo, and resize the window. Build output is ignored
by Git. Existing Project_information files are historical reference material.

## Build a standalone desktop app

Packaging requires a full JDK 17+ containing jpackage (JDK 21 is recommended).
The app includes its own Java runtime, so people opening the packaged app do not
need Java installed. Build on the target operating system and architecture.

    ./run.sh package

On macOS the result is dist/JPaint.app. Double-click it in Finder, or run:

    open dist/JPaint.app

You can drag JPaint.app into Applications. Existing packages are moved into a
unique dist/previous.* folder before replacement. Build output is ignored by Git.
The optional ./run.sh menu includes Package as well.

This creates an application image, not a DMG installer. It is intended for local
use; distribution to other Macs requires Apple signing/notarization to avoid
Gatekeeper warnings. Packaging on Linux or Windows produces a native app folder
for that platform; it does not cross-compile a macOS app.
