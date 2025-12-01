#!/bin/bash
set -e

INCREMENT_TYPE=$1
GRADLE_FILE="app/build.gradle.kts"

if [[ ! "$INCREMENT_TYPE" =~ ^(major|minor|patch)$ ]]; then
  echo "Usage: $0 {major|minor|patch}"
  exit 1
fi

CURRENT_CODE=$(grep 'versionCode = ' "$GRADLE_FILE" | sed 's/.*versionCode = \([0-9]*\).*/\1/')
NEW_CODE=$((CURRENT_CODE + 1))

CURRENT_NAME=$(grep 'versionName = ' "$GRADLE_FILE" | sed 's/.*versionName = "\([^"]*\)".*/\1/')
MAJOR=$(echo "$CURRENT_NAME" | cut -d. -f1)
MINOR=$(echo "$CURRENT_NAME" | cut -d. -f2)
PATCH=$(echo "$CURRENT_NAME" | cut -d. -f3)

case $INCREMENT_TYPE in
  major)
    NEW_NAME="$((MAJOR + 1)).0.0"
    ;;
  minor)
    NEW_NAME="$MAJOR.$((MINOR + 1)).0"
    ;;
  patch)
    NEW_NAME="$MAJOR.$MINOR.$((PATCH + 1))"
    ;;
esac

sed -i "s/versionCode = .*/versionCode = $NEW_CODE/" "$GRADLE_FILE"
sed -i "s/versionName = .*/versionName = \"$NEW_NAME\"/" "$GRADLE_FILE"

echo "Updated version: $CURRENT_NAME -> $NEW_NAME (versionCode: $CURRENT_CODE -> $NEW_CODE)"
