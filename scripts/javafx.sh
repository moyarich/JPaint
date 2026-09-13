#!/usr/bin/env bash
# Sourced by run-app.sh after it changes to the project root.
fx_version=21.0.8
case "$(uname -s)" in
  Darwin) fx_os=osx ;;
  Linux) fx_os=linux ;;
  MINGW*|MSYS*|CYGWIN*) fx_os=windows ;;
  *) echo 'Unsupported operating system for automatic JavaFX download.' >&2; exit 1 ;;
esac
case "$(uname -m)" in
  arm64|aarch64) fx_arch=aarch64 ;;
  x86_64|amd64) fx_arch=x64 ;;
  *) echo 'Unsupported architecture for automatic JavaFX download.' >&2; exit 1 ;;
esac
fetch_fx() {
  local kind="$1" target="$2" archive
  [[ -d "$target" ]] && return
  command -v curl >/dev/null && command -v unzip >/dev/null || {
    echo 'Automatic JavaFX setup needs curl and unzip, or configure JAVAFX_HOME / JAVAFX_JMODS.' >&2; exit 1;
  }
  mkdir -p .javafx
  archive="$(mktemp "$jpaint_root/.javafx/download.XXXXXX")"
  echo "Downloading JavaFX $fx_version $kind from Gluon…"
  if ! curl -fL --retry 2 "https://download2.gluonhq.com/openjfx/$fx_version/openjfx-${fx_version}_${fx_os}-${fx_arch}_bin-${kind}.zip" -o "$archive"; then
    rm -f "$archive"; echo 'JavaFX download failed. Check the connection or configure the local SDK.' >&2; exit 1
  fi
  unzip -q -o "$archive" -d .javafx
  rm -f "$archive"
}
fx_home="${JAVAFX_HOME:-$jpaint_root/.javafx/javafx-sdk-$fx_version}"
if [[ -z "${JAVAFX_HOME:-}" ]]; then fetch_fx sdk "$fx_home"; fi
[[ -f "$fx_home/lib/javafx.controls.jar" ]] || { echo "Invalid JAVAFX_HOME: $fx_home" >&2; exit 1; }
fx_modules="$fx_home/lib"
if [[ "$action" == package ]]; then
  fx_jmods="${JAVAFX_JMODS:-$jpaint_root/.javafx/javafx-jmods-$fx_version}"
  if [[ -z "${JAVAFX_JMODS:-}" ]]; then fetch_fx jmods "$fx_jmods"; fi
  [[ -f "$fx_jmods/javafx.controls.jmod" ]] || { echo "Invalid JAVAFX_JMODS: $fx_jmods" >&2; exit 1; }
fi
