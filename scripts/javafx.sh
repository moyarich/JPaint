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
# Prefer the installed SDK supplied for this project; environment overrides win.
installed_fx=/Library/Java/JavaVirtualMachines/javafx-sdk-26.0.2
if [[ -n "${JAVAFX_HOME:-}" ]]; then
  fx_home="$JAVAFX_HOME"
elif [[ -f "$installed_fx/lib/javafx.controls.jar" ]]; then
  fx_home="$installed_fx"
else
  fx_home="$jpaint_root/.javafx/javafx-sdk-$fx_version"
  fetch_fx sdk "$fx_home"
fi
[[ -f "$fx_home/lib/javafx.controls.jar" ]] || { echo "Invalid JAVAFX_HOME: $fx_home" >&2; exit 1; }
sdk_version="$(sed -n 's/^javafx.version=//p' "$fx_home/lib/javafx.properties" | tr -d '\r')"
[[ "$sdk_version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]] || { echo 'Cannot determine the selected JavaFX SDK version.' >&2; exit 1; }
fx_version="$sdk_version"
if [[ "$fx_os" == osx ]]; then
  runtime_arch="$("$java_bin" -XshowSettings:properties -version 2>&1 | sed -n 's/.*os.arch = //p')"
  native_arch=arm64
  [[ "$runtime_arch" == x86_64 || "$runtime_arch" == amd64 ]] && native_arch=x86_64
  fx_arch=aarch64
  [[ "$native_arch" == x86_64 ]] && fx_arch=x64
  if ! file "$fx_home/lib/libglass.dylib" | grep -q "$native_arch"; then
    if [[ -n "${JAVAFX_HOME:-}" ]]; then
      echo "JAVAFX_HOME native libraries do not match Java architecture $runtime_arch: $fx_home" >&2
      echo 'Unset JAVAFX_HOME to use automatic compatible SDK detection.' >&2
      exit 1
    fi
    echo "Installed SDK architecture differs from Java; using a compatible JavaFX $fx_version cache."
    fx_arch=aarch64
    [[ "$native_arch" == x86_64 ]] && fx_arch=x64
    fx_home="$jpaint_root/.javafx/javafx-sdk-$fx_version"
    fetch_fx sdk "$fx_home"
  fi
fi
fx_modules="$fx_home/lib"
echo "JavaFX $fx_version: $fx_home"
# Ask the selected runtime to resolve the SDK before compilation or packaging.
"$java_bin" --module-path "$fx_modules" --add-modules javafx.controls --validate-modules || {
  echo "The selected Java runtime cannot load JavaFX $fx_version. Set JAVA_HOME to a compatible JDK (JDK 26 for JavaFX 26)." >&2
  exit 1
}
mkdir -p .javafx
if [[ "$action" == package ]]; then
  fx_jmods="${JAVAFX_JMODS:-$jpaint_root/.javafx/javafx-jmods-$fx_version}"
  if [[ -z "${JAVAFX_JMODS:-}" ]]; then fetch_fx jmods "$fx_jmods"; fi
  [[ -f "$fx_jmods/javafx.controls.jmod" ]] || { echo "Invalid JAVAFX_JMODS: $fx_jmods" >&2; exit 1; }
fi

if [[ "$action" == package ]]; then
  jmod_description="$("$(dirname "$javac_bin")/jmod" describe "$fx_jmods/javafx.controls.jmod")"
  [[ "$jmod_description" == "javafx.controls@$fx_version"* ]] || {
    echo "JAVAFX_JMODS must match SDK version $fx_version." >&2; exit 1
  }
fi
