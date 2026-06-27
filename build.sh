#! /usr/bin/env bash

set -e

SQLITE_JAR="./lib/sqlite-jdbc-3.50.3.0.jar"
JSON_JAR="./lib/json-20250517.jar"

CALENDAR_JAR="./lib/jcalendar-1.4.jar"
FLATLAF_JAR="./lib/flatlaf-3.6.2.jar"

MAIN_DIR="./src/main/"
SRC_DIR="./src/main/java/"

OUT_DIR="./bin/"

mkdir -p "$OUT_DIR"

echo "Compiling files from: $SRC_DIR"

ls "$SRC_DIR" > /dev/null 2>&1
ls "$OUT_DIR" > /dev/null 2>&1

ls "$SQLITE_JAR" > /dev/null 2>&1
ls "$JSON_JAR" > /dev/null 2>&1

ls "$CALENDAR_JAR" > /dev/null 2>&1
ls "$FLATLAF_JAR" > /dev/null 2>&1

CLASSPATH="$SQLITE_JAR:$JSON_JAR:$CALENDAR_JAR:$FLATLAF_JAR:$OUT_DIR:$MAIN_DIR"
JAVAFILES=$(find "$SRC_DIR" -type f -name "*.java")

javac -cp $CLASSPATH -d $OUT_DIR $JAVAFILES

echo "done."
echo ""
