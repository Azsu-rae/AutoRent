#!/usr/bin/env pwsh

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Write-Host ""
$DB_PATH = "Backend/ressources/databases/AutoRent.db"
if (Test-Path $DB_PATH) {
    Remove-Item $DB_PATH
    Write-Host "Deleted database"
} else {
    Write-Host "No database found"
}
Write-Host ""

# Directories and dependencies
$SRC_DIR = "Backend/src/main/java/"
$OUT_DIR = "Backend/bin/"
$SQLITE_JAR = "Backend/lib/sqlite-jdbc-3.50.3.0.jar"
$JSON_JAR = "Backend/lib/json-20250517.jar"

Write-Host "Compiling files from: $SRC_DIR"

# Make sure output dir exists
New-Item -ItemType Directory -Force -Path $OUT_DIR | Out-Null

# Compile all Java files
$classpath = "$SQLITE_JAR;$JSON_JAR"
$javaFiles = Get-ChildItem -Recurse -Filter *.java -Path $SRC_DIR | ForEach-Object { $_.FullName }
javac -cp $classpath -d $OUT_DIR $javaFiles

Write-Host "Compilation successful!"
Write-Host ""

# Run program (use `;` for Windows classpath, `:` for Linux/macOS)
$classpath = "$OUT_DIR;$SQLITE_JAR;$JSON_JAR"
java -cp $classpath Main
