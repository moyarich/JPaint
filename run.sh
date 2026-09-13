#!/usr/bin/env bash
set -euo pipefail
jpaint_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
exec bash "$jpaint_root/scripts/run.sh" "$@"
