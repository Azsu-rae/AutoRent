#!/usr/bin/env bash

set -e

echo ""
DB_PATH="Backend/ressources/databases/AutoRent.db"
if [ -f "$DB_PATH" ]; then
    rm "$DB_PATH"
    echo "Deleted database"
else
    echo "No database found"
fi
echo ""

SRC_DIR="Backend/src/main/java/"
OUT_DIR="Backend/bin/"
SQLITE_DIR="Backend/lib/sqlite-jdbc-3.50.3.0.jar"

echo "Compilling files from: $SRC_DIR"

mkdir -p "$OUT_DIR"
javac -d "$OUT_DIR" $($MSYS/find "$SRC_DIR" -name "*.java")

echo "Compilation successful!"

java -cp "$OUT_DIR;$SQLITE_DIR" Main
