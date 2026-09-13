#!/usr/bin/env bash
set -euo pipefail
jpaint_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$jpaint_root"
action="${1:-run}"
case "$action" in
  run|build|test|java|package) ;;
  -h|--help|help) echo 'Usage: ./run.sh [run|build|test|java|package|menu]'; exit 0 ;;
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
  (( major >= 11 )) || return 1
  [[ "$runtime" =~ version[[:space:]]+\"([0-9]+) ]] || return 1
  (( ${BASH_REMATCH[1]} >= 11 )) || return 1
  java_bin="$candidate/java"; javac_bin="$candidate/javac"
}
if [[ -n "${JAVA_HOME:-}" ]]; then
  if ! use_jdk "$JAVA_HOME/bin"; then
    echo "JAVA_HOME does not contain a working JDK 11+: $JAVA_HOME" >&2
    echo 'Continuing with automatic detection. Use unset JAVA_HOME to remove the invalid setting.' >&2
  fi
fi
if [[ -z "$java_bin" && "$(uname -s)" == Darwin ]]; then
  detected_home="$(/usr/libexec/java_home -v '11+' 2>/dev/null || true)"
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
JPaint needs a JDK 11 or newer, including java and javac.
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
mkdir -p build/classes
find src -name '*.java' -print > build/sources.txt
"$javac_bin" --release 11 -encoding UTF-8 -d build/classes @build/sources.txt
case "$action" in
  build) echo 'Built JPaint in build/classes' ;;
  test)
    mkdir -p build/test-classes
    "$javac_bin" --release 11 -encoding UTF-8 -cp build/classes -d build/test-classes tests/RegressionTest.java
    separator=':'
    case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) separator=';' ;; esac
    "$java_bin" -Djava.awt.headless=true -cp "build/classes${separator}build/test-classes" RegressionTest ;;
  package)
    jdk_bin="$(dirname "$javac_bin")"
    if [[ ! -x "$jdk_bin/jpackage" || ! -x "$jdk_bin/jar" ]]; then
      echo 'Packaging requires a full JDK 17+ with jpackage. Set JAVA_HOME to that JDK and try again.' >&2
      exit 1
    fi
    # Build in a fresh directory so stale jars never enter the application image.
    package_work="$(mktemp -d "$jpaint_root/build/package.XXXXXX")"
    trap 'rm -rf "$package_work"' EXIT
    mkdir -p "$package_work/input" "$package_work/output" dist
    "$jdk_bin/jar" --create --file "$package_work/input/JPaint.jar" --main-class main.Main -C build/classes .
    echo 'Packaging JPaint with its own Java runtime…'
    "$jdk_bin/jpackage" --type app-image --name JPaint \
      --input "$package_work/input" --main-jar JPaint.jar --main-class main.Main \
      --dest "$package_work/output" --app-version 1.0.0 --vendor 'Moya Richards' \
      --description 'JPaint shape drawing studio' --java-options '-Dfile.encoding=UTF-8'
    # Retain existing packages rather than deleting a previous working app.
    package_name=JPaint
    [[ "$(uname -s)" == Darwin ]] && package_name=JPaint.app
    if [[ -e "dist/$package_name" ]]; then
      backup_dir="$(mktemp -d "$jpaint_root/dist/previous.XXXXXX")"
      mv "dist/$package_name" "$backup_dir/"
      echo "Previous app preserved in $backup_dir"
    fi
    mv "$package_work/output/$package_name" dist/
    echo "App ready: $jpaint_root/dist/$package_name"
    if [[ "$(uname -s)" == Darwin ]]; then
      echo 'Launch with: open dist/JPaint.app'
      echo 'You can also move JPaint.app into Applications.'
    fi
    ;;
  run) exec "$java_bin" -cp build/classes main.Main ;;
esac
