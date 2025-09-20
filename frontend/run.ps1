
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
$BE_SRC_DIR = "backend/src/main/java/"
$BE_OUT_DIR = "backend/bin/"
$SQLITE_JAR = "backend/lib/sqlite-jdbc-3.50.3.0.jar"
$JSON_JAR = "backend/lib/json-20250517.jar"

Write-Host "Compiling files from: $BE_SRC_DIR"

# Make sure output dir exists
New-Item -ItemType Directory -Force -Path $BE_OUT_DIR | Out-Null

# Compile all Java files
$classpath = "$SQLITE_JAR;$JSON_JAR"
$javaFiles = Get-ChildItem -Recurse -Filter *.java -Path $BE_SRC_DIR | ForEach-Object { $_.FullName }
javac -cp $classpath -d $BE_OUT_DIR $javaFiles

Write-Host "Compilation successful!"
Write-Host ""

# Directories and dependencies
$FE_SRC_DIR = "frontend/src/main/java/"
$FE_OUT_DIR = "frontend/bin/"
$CALENDAR_JAR = "frontend/lib/jcalendar-1.4.jar"

Write-Host "Compiling files from: $FE_SRC_DIR"

# Make sure output dir exists
New-Item -ItemType Directory -Force -Path $FE_OUT_DIR | Out-Null

# Compile all Java files
$classpath = "$BE_OUT_DIR;$SQLITE_JAR;$JSON_JAR;$CALENDAR_JAR"
$javaFiles = Get-ChildItem -Recurse -Filter *.java -Path $FE_SRC_DIR | ForEach-Object { $_.FullName }
javac -cp $classpath -d $FE_OUT_DIR $javaFiles

Write-Host "Compilation successful!"
Write-Host ""

# Run program (use `;` for Windows classpath, `:` for Linux/macOS)
$classpath = "$BE_OUT_DIR;$SQLITE_JAR;$JSON_JAR;$CALENDAR_JAR;$FE_OUT_DIR"
java -cp $classpath MainApp
