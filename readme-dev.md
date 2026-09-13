# JPaint developer guide

`main` contains the JavaFX implementation. The original Swing app is preserved
on `swing-code`; its launch and build instructions live on that branch.

## Java and JavaFX configuration

The launcher validates both `java` and `javac`, checking these locations in order:

1. `JAVA_HOME`, if configured and valid.
2. Registered macOS JDKs reported by `/usr/libexec/java_home`.
3. Homebrew OpenJDK installations on macOS.
4. The JDK available on `PATH`.

An invalid `JAVA_HOME` produces a warning and automatic detection continues.
Use `./run.sh java` to see which Java tools were selected. This command reports
Java only; build/run commands also report the selected JavaFX SDK.

| JavaFX setup | JDK to use |
| --- | --- |
| Installed/cached JavaFX 26.0.2 on this Mac | JDK 26 |
| Default JavaFX 21.0.8 fallback | JDK 21 recommended; JDK 17+ supported |
| Explicit `JAVAFX_HOME` | A JDK compatible with that SDK |

`/usr/libexec/java_home` is a command, **not a directory**. Do not `cd` to it.
For the registered JDK 26 installation on this Mac:

```bash
export JAVA_HOME="$(/usr/libexec/java_home -v 26)"
unset JAVAFX_HOME
./run.sh java
./run.sh
```

### SDK selection and architecture

The launcher uses an explicit `JAVAFX_HOME` first. Otherwise it recognizes
`/Library/Java/JavaVirtualMachines/javafx-sdk-26.0.2` when present, or downloads
the pinned JavaFX 21.0.8 fallback from Gluon.

The supplied system SDK contains **Intel (`x86_64`) native libraries**. With this
Mac's Apple Silicon (`aarch64`) JDK, the launcher leaves the system folder alone
and downloads the matching JavaFX 26.0.2 Apple Silicon SDK into `.javafx/`.
An explicitly configured SDK with the wrong architecture is rejected with a
clear message rather than silently replaced. Use `unset JAVAFX_HOME` to return
to automatic selection.

The selected SDK's `lib/javafx.properties` determines the packaging dependency
version. JavaFX jmods must match that version. The launcher checks that the
selected Java runtime can resolve the SDK modules before compiling.

### Offline or explicit dependencies

Set these to local installations for the same OS, architecture, and JavaFX version:

```bash
export JAVAFX_HOME=/path/to/javafx-sdk-26.0.2
export JAVAFX_JMODS=/path/to/javafx-jmods-26.0.2
```

`JAVAFX_HOME` points to the SDK root containing `lib/`. `JAVAFX_JMODS` points
directly to the folder containing `javafx.controls.jmod`. Only packaging needs
jmods. First-time automatic downloads require `curl`, `unzip`, and Internet
access; later builds reuse the ignored `.javafx/` cache.

## Launch, build, and test

From this checkout:

```bash
cd /private/var/www/+ai-apps/JPaint
./run.sh              # Compile and launch
./run.sh build        # Compile only
./run.sh test         # Model regression checks; no display required
./run.sh smoke        # JavaFX UI and interaction checks; desktop required
./run.sh java         # Show Java tools without building
./run.sh package      # Build a standalone app
./run.sh menu         # Optional interactive menu
./run.sh --help
```

Use your own checkout path if different. The root launcher delegates to
`scripts/run.sh` and `scripts/run-app.sh`; dependency setup is in
`scripts/javafx.sh`. Direct commands do not require `fzf`. The optional menu uses
`fzf` when available and otherwise falls back to a Bash menu.

Scripts can be invoked from another directory and handle checkout paths with
spaces. Use `bash run.sh` if executable permissions are missing; do not use `sh`
because the scripts require Bash. No Maven or Gradle installation is needed.

Compilation uses a fresh temporary directory before replacing `build/classes`,
so switching between Swing and JavaFX does not retain stale compiled classes.

### Validation

`./run.sh test` runs 26 model checks covering movement, selection independence,
nested groups, clipboard copies, repeated paste offsets, layer order, undo/redo,
hit testing, reverse drags, and no-op handling. It does not initialize a GUI.

`./run.sh smoke` starts JavaFX and checks rendering, the color wheel, mouse drawing,
selection, movement, and undo/redo buttons. It writes these diagnostic renders,
then exits:

- `build/javafx-window.ppm`
- `build/javafx-picker.ppm`

To verify the bundled runtime after packaging on macOS:

```bash
./dist/JPaint.app/Contents/MacOS/JPaint --smoke-test
```

## Package the desktop app

```bash
./run.sh package
open dist/JPaint.app
```

Packaging requires a full JDK with `jar` and `jpackage`, plus matching JavaFX
jmods. It produces a native application image containing Java, JavaFX, and JPaint.
On macOS, double-click `dist/JPaint.app` or move it into Applications. End users
do not need a separate JDK or SDK. Quit any running old version before opening
the rebuilt app.

Existing packages are preserved in unique `dist/previous.*` directories before
replacement. `build/`, `.javafx/`, and `dist/` are ignored by Git. A Git checkout
therefore contains source, not a prebuilt app.

Build on the destination OS and architecture. macOS Apple Silicon has been
validated. Linux and Windows paths are handled by the launcher but have not
been tested here; some architectures require manually supplied dependencies.
Packaging produces an app image, not a DMG/MSI installer. Signing and notarization
for public macOS distribution are not configured.

## App behavior

- **Draw:** select a shape and shading, then drag on the canvas. The preview tracks
  the drag. Zero-width or zero-height shapes are ignored.
- **Select:** click the topmost matching shape, or drag a region to select objects
  whose bounds overlap it.
- **Move:** drag the existing selection. One completed gesture is one undo step.
- **Colors:** Primary and Secondary open the color wheel. Angle controls hue;
  distance from the center controls saturation. Brightness, RGB, hex, and palette
  choices stay synchronized. Arrow keys on the wheel adjust hue/saturation.
  Apply color commits the choice; Cancel retains the previous color.
- **Shading:** Primary is the fill, or the stroke for outline-only shapes.
  Secondary is the stroke for Fill and outline. Changes affect new shapes.
- **Groups:** group two or more selected objects; ungroup releases one nesting level.
- **Clipboard:** copies are independent snapshots; successive pastes add 24-pixel offsets.
- **History:** undo/redo restores artwork and selection. Empty edits preserve redo.

The artboard is scrollable and measures 1600 × 1000. Drawings are stored only in
memory. There is no save/open/export workflow. See [README.md](README.md) for
keyboard shortcuts.

## Source layout and IDE setup

| Path | Purpose |
| --- | --- |
| `src/main/java/jpaint/Main.java` | JavaFX app, layout, input, and smoke checks |
| `src/main/java/jpaint/model/` | Immutable artwork and snapshot history; no UI dependencies |
| `src/main/java/jpaint/ui/` | Shape renderer, color wheel, diagnostic snapshot writer |
| `src/main/resources/jpaint/studio.css` | Interface styling |
| `src/main/java/module-info.java` | Java module declaration |
| `tests/RegressionTest.java` | Model regression checks |
| `scripts/` | Dependency detection, build, launch, and packaging |

The module is `jpaint` and the main class is `jpaint.Main`. Configure the IDE with
a compatible JDK and the selected JavaFX SDK on the module path. Mark
`src/main/java` as sources and `src/main/resources` as resources. The scripts are
the reference build workflow.

## Troubleshooting

| Symptom | Action |
| --- | --- |
| `cd: not a directory: /usr/libexec/java_home` | Run the command directly, or use the `export JAVA_HOME` example above. |
| No working JDK found | Install a JDK and set `JAVA_HOME` to its root, not its `bin` folder. |
| SDK architecture mismatch | Match Intel/Apple Silicon SDK and JDK architectures; unset `JAVAFX_HOME` for automatic selection. |
| Java cannot load SDK modules | Use a JDK compatible with the selected JavaFX version. |
| No suitable graphics pipeline | Check SDK architecture first; launch in a graphical desktop session. |
| JavaFX jmods version mismatch | Point `JAVAFX_JMODS` at matching jmods, or unset it to download the matching version. |
| Download failure | Check connectivity and retry, or configure local dependencies. |
| Old UI after rebuilding | Quit the running app and open the new `dist/JPaint.app`. |

## Published branches

- `main`: current JavaFX app.
- `javafx-rewrite`: JavaFX rewrite, promoted to `main` after validation.
- `swing-code`: preserved complete Swing implementation.

All three branches are published to `origin` at `github.com/moyarich/JPaint`.
Switch branches with a clean working tree and rebuild afterward. Generated apps
and caches are shared across branch switches; `dist/` may still contain an app
built from another branch. Historical `Project_information/` documents apply to
the original project and have been retained as reference material.
