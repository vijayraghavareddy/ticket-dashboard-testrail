#!/bin/bash

# Usage: ./run-tests.sh [options]
#   -t, --tags      Cucumber tag filter (e.g. @smoke)
#   -u, --base-url  Override base URL (e.g. https://staging.reqres.in/api)
#   -h, --help      Show this help message

TAGS=""
BASE_URL=""

while [[ $# -gt 0 ]]; do
  case $1 in
    -t|--tags)      TAGS="$2"; shift 2 ;;
    -u|--base-url)  BASE_URL="$2"; shift 2 ;;
    -h|--help)
      sed -n '2,6p' "$0"
      exit 0 ;;
    *) echo "Unknown option: $1"; exit 1 ;;
  esac
done

ARGS=()
[[ -n "$TAGS" ]]    && ARGS+=("-Dcucumber.filter.tags=$TAGS")
[[ -n "$BASE_URL" ]] && ARGS+=("-DbaseUrl=$BASE_URL")

echo "Running tests..."
mvn test "${ARGS[@]}"
