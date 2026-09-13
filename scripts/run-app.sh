#!/usr/bin/env bash
set -euo pipefail
jpaint_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$jpaint_root"
action="${1:-run}"
case "$action" in
  run|build|test|smoke|java|package) ;;
  -h|--help|help) echo 'Usage: ./run.sh [run|build|test|smoke|java|package|menu]'; exit 0 ;;
  *) echo "Unknown action: $action. Use ./run.sh --help." >&2; exit 2 ;;
esac

# Validate both tools; macOS /usr/bin/java can exist without an installed JDK.
java_bin=''
javac_bin=''
use_jdk() {
  local candidate="$1" version major runtime
  [[ -x "$candidate/java" && -x "$candidate/javac" ]] || return 1
  version="$("$candidate/javac" -version 2>&1)" || return 1
  runtime="$("$candidate/java" -version 2>&1)" || return 1
  [[ "$version" =~ javac[[:space:]]+([0-9]+) ]] || return 1
  major="${BASH_REMATCH[1]}"
  (( major >= 17 )) || return 1
  [[ "$runtime" =~ version[[:space:]]+\"([0-9]+) ]] || return 1
  (( ${BASH_REMATCH[1]} >= 17 )) || return 1
  java_bin="$candidate/java"; javac_bin="$candidate/javac"
}
if [[ -n "${JAVA_HOME:-}" ]]; then
  if ! use_jdk "$JAVA_HOME/bin"; then
    echo "JAVA_HOME does not contain a working JDK 17+: $JAVA_HOME" >&2
    echo 'Continuing with automatic detection. Use unset JAVA_HOME to remove the invalid setting.' >&2
  fi
fi
if [[ -z "$java_bin" && "$(uname -s)" == Darwin ]]; then
  detected_home="$(/usr/libexec/java_home -v '17+' 2>/dev/null || true)"
  if [[ -n "$detected_home" ]]; then use_jdk "$detected_home/bin" || true; fi
  if [[ -z "$java_bin" ]]; then
    for candidate in /opt/homebrew/opt/openjdk*/bin /usr/local/opt/openjdk*/bin; do
      if use_jdk "$candidate"; then break; fi
    done
  fi
fi
if [[ -z "$java_bin" ]]; then
  path_compiler="$(command -v javac || true)"
  if [[ -n "$path_compiler" ]]; then use_jdk "$(dirname "$path_compiler")" || true; fi
fi
if [[ -z "$java_bin" ]]; then
  cat >&2 <<'HELP'
JPaint needs a JDK 17 or newer, including java and javac.
On macOS with Homebrew, install one with:
  brew install openjdk@21
Then run this launcher again; it detects Homebrew JDKs automatically.
For a registered macOS JDK, you can run:
  /usr/libexec/java_home
  export JAVA_HOME="$(/usr/libexec/java_home)"
java_home is a command, not a directory. Do not use cd with it.
On other systems, install a JDK and add its bin directory to PATH,
or set JAVA_HOME to the JDK directory (not its bin directory).
HELP
  exit 1
fi
if [[ "$action" == java ]]; then
  echo "Java: $java_bin"
  "$java_bin" -version
  "$javac_bin" -version
  exit 0
fi
source "$jpaint_root/scripts/javafx.sh"
module_separator=':'
case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) module_separator=';' ;; esac
# Compile in a clean temporary folder; branch switching cannot retain Swing classes.
compile_dir="$(mktemp -d "$jpaint_root/.javafx/classes.XXXXXX")"
trap 'rm -rf "$compile_dir"' EXIT
mkdir -p build
find src/main/java -name '*.java' -print > build/sources.txt
"$javac_bin" --release 17 --module-path "$fx_modules" -encoding UTF-8 -d "$compile_dir" @build/sources.txt
cp -R src/main/resources/. "$compile_dir/"
mkdir -p build/classes
# Only generated files are removed here.
rm -rf build/classes
mv "$compile_dir" build/classes
trap - EXIT
case "$action" in
  build) echo 'Built JavaFX JPaint in build/classes' ;;
  test)
    mkdir -p build/test-classes
    "$javac_bin" --release 17 -encoding UTF-8 -cp build/classes -d build/test-classes tests/RegressionTest.java
    separator=':'
    case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) separator=';' ;; esac
    "$java_bin" -cp "build/classes${separator}build/test-classes" RegressionTest ;;
  run|smoke)
    launch_args=()
    [[ "$action" == smoke ]] && launch_args+=(--smoke-test)
    exec "$java_bin" --enable-native-access=javafx.graphics --module-path "$fx_modules${module_separator}build/classes" --module jpaint/jpaint.Main "${launch_args[@]}" ;;
  package)
    jdk_bin="$(dirname "$javac_bin")"
    [[ -x "$jdk_bin/jpackage" && -x "$jdk_bin/jar" ]] || { echo 'Packaging needs a full JDK 17+ with jpackage.' >&2; exit 1; }
    package_work="$(mktemp -d "$jpaint_root/build/package.XXXXXX")"
    trap 'rm -rf "$package_work"' EXIT
    mkdir -p "$package_work/input" "$package_work/output" dist
    "$jdk_bin/jar" --create --file "$package_work/input/jpaint.jar" --main-class jpaint.Main -C build/classes .
    echo 'Packaging JPaint with Java and JavaFX…'
    "$jdk_bin/jpackage" --type app-image --name JPaint \
      --module-path "$fx_jmods${module_separator}$package_work/input" --module jpaint/jpaint.Main \
      --dest "$package_work/output" --app-version 2.0.0 --vendor 'Moya Richards' \
      --description 'JPaint JavaFX drawing studio' --java-options '-Dfile.encoding=UTF-8' --java-options '--enable-native-access=javafx.graphics'
    package_name=JPaint
    [[ "$(uname -s)" == Darwin ]] && package_name=JPaint.app
    if [[ -e "dist/$package_name" ]]; then
      backup_dir="$(mktemp -d "$jpaint_root/dist/previous.XXXXXX")"
      mv "dist/$package_name" "$backup_dir/"
      echo "Previous app preserved in $backup_dir"
    fi
    mv "$package_work/output/$package_name" dist/
    echo "App ready: $jpaint_root/dist/$package_name"
    ;;
esac
