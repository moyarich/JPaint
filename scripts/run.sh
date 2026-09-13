#!/usr/bin/env bash
set -euo pipefail
jpaint_scripts="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
# Direct commands work without fzf and in non-interactive environments.
if [[ "${1:-run}" != menu ]]; then
  exec bash "$jpaint_scripts/run-app.sh" "${@:-run}"
fi
if [[ ! -t 0 || ! -t 1 ]]; then
  echo 'The menu needs a terminal. Use ./run.sh run, build, test, java, or package.' >&2
  exit 2
fi
if command -v fzf >/dev/null 2>&1; then
  choice="$(printf '%s\n' Run Build Test Package 'Java info' Exit | fzf --height=40% --layout=reverse --border --prompt='JPaint > ')" || exit 0
else
  PS3='JPaint: choose an action: '
  select choice in Run Build Test Package 'Java info' Exit; do
    [[ -n "$choice" ]] && break
  done
fi
case "${choice:-Exit}" in
  Run) action=run ;; Build) action=build ;; Test) action=test ;;
  Package) action=package ;;
  'Java info') action=java ;; *) exit 0 ;;
esac
exec bash "$jpaint_scripts/run-app.sh" "$action"
