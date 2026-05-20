#! /usr/bin/env bash

set -e

BE_SRC_DIR="./backend/src/main/java/"
BE_OUT_DIR="./backend/bin/"
SQLITE_JAR="./backend/lib/sqlite-jdbc-3.50.3.0.jar"
JSON_JAR="./backend/lib/json-20250517.jar"

mkdir -p "$BE_OUT_DIR"

echo "Compiling files from: $BE_SRC_DIR"

ls "$BE_SRC_DIR" > /dev/null 2>&1
ls "$BE_OUT_DIR" > /dev/null 2>&1
ls "$SQLITE_JAR" > /dev/null 2>&1
ls "$JSON_JAR" > /dev/null 2>&1

CLASSPATH="$SQLITE_JAR:$JSON_JAR:$BE_OUT_DIR"
JAVAFILES=$(find "$BE_SRC_DIR" -type f -name "*.java")

javac -cp $CLASSPATH -d $BE_OUT_DIR $JAVAFILES

echo "done."
echo ""
