# JPaint — JavaFX Studio

A desktop shape editor built with JavaFX. Draw rectangles, ellipses, and triangles;
choose custom colors with a color wheel; select, move, group, copy/paste, and undo
or redo edits. The interface uses JavaFX controls, scene-graph shapes, and CSS.

## Start

Install a JDK 17+ (JDK 21 recommended), then run:

```bash
./run.sh
```

The launcher downloads the pinned JavaFX 21.0.8 SDK from Gluon on first use.
Subsequent launches use the local cache. See [the developer guide](readme-dev.md)
for setup, testing, packaging, keyboard shortcuts, and offline configuration.

```bash
./run.sh test       # Model regressions; no desktop required
./run.sh smoke      # JavaFX window, renderer, and picker checks; desktop required
./run.sh package    # Native app with Java and JavaFX included
```

On macOS the packaged app is `dist/JPaint.app`. Drawings remain in memory and
are discarded when the app closes; file saving/export is not yet implemented.

## Branches

- `swing-code`: preserved Swing implementation, including the custom color wheel,
  modernized UI, regression tests, and native packaging.
- `javafx-rewrite`: JavaFX replacement and its tests.
- `main`: promoted JavaFX implementation.

Historical project documents are retained in `Project_information/`; their Swing
architecture descriptions apply to the preserved branch, not the JavaFX rewrite.
