# Define Paths
$javaSourcePath = ".\backend\src\"  # The folder containing your Java files
$compiledClassesPath = ".\backend\bin\"  # The folder where .class files will be placed
$dependenciesPath = ".\backend\lib\sqlite-jdbc-3.47.1.0.jar"  # The folder containing additional JAR dependencies (e.g., database JAR)
$mainClass = ".\backend\src\Main.java"  # Fully qualified name of the Main class (package and class name)

# Step 1: Compile the Java files
Write-Host "Compiling Java files from $javaSourcePath..."
javac -d $compiledClassesPath -cp $dependenciesPath $(Get-ChildItem -Path . -Recurse -Filter "*.java" | ForEach-Object { $_.FullName })

# Step 2: Check for compilation success
if ($?) {
    Write-Host "Compilation successful."
} else {
    Write-Host "Compilation failed."
    exit 1  # Exit if compilation fails
}

# Step 3: Run the Main class with dependencies
Write-Host "Running the Main class..."
Write-Host ""
$classpath = "$compiledClassesPath;$dependenciesPath"  # Include both compiled classes and dependencies
java -cp $classpath $mainClass

rm .\backend\ressources\databases\AutoRent.db
