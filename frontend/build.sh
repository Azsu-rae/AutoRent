#! /usr/bin/env bash

set -e

SQLITE_JAR="./backend/lib/sqlite-jdbc-3.50.3.0.jar"
JSON_JAR="./backend/lib/json-20250517.jar"

CALENDAR_JAR="./frontend/lib/jcalendar-1.4.jar"
FLATLAF_JAR="./frontend/lib/flatlaf-3.6.2.jar"

FE_MAIN_DIR="./frontend/src/main/"
FE_SRC_DIR="./frontend/src/main/java/"

BE_OUT_DIR="./backend/bin/"
FE_OUT_DIR="./frontend/bin/"

mkdir -p "$FE_OUT_DIR"

echo "Compiling files from: $FE_SRC_DIR"

ls "$FE_SRC_DIR" > /dev/null 2>&1
ls "$FE_OUT_DIR" > /dev/null 2>&1
ls "$BE_OUT_DIR" > /dev/null 2>&1

ls "$SQLITE_JAR" > /dev/null 2>&1
ls "$JSON_JAR" > /dev/null 2>&1

ls "$CALENDAR_JAR" > /dev/null 2>&1
ls "$FLATLAF_JAR" > /dev/null 2>&1

CLASSPATH="$SQLITE_JAR:$JSON_JAR:$CALENDAR_JAR:$FLATLAF_JAR:$FE_OUT_DIR:$BE_OUT_DIR:$FE_MAIN_DIR"
JAVAFILES=$(find "$FE_SRC_DIR" -type f -name "*.java")

javac -cp $CLASSPATH -d $FE_OUT_DIR $JAVAFILES

echo "done."
echo ""
