#!/usr/bin/env bash

set -e

VIEWER_DIR="$(dirname "$(realpath "${BASH_SOURCE[0]:-$0}")")"

exec "$VIEWER_DIR/jre/bin/java" -m org.glavo.viewer "$@"
