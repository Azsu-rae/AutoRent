$javaSourcePath = ".\backend\src\"
$compiledClassesPath = ".\backend\bin\"
$dependenciesPath = ".\backend\lib\sqlite-jdbc-3.47.1.0.jar"
$mainClass = ".\backend\src\Main.java"

Write-Host "Compiling Java files from $javaSourcePath..."
javac -d $compiledClassesPath -cp $dependenciesPath $(Get-ChildItem -Path . -Recurse -Filter "*.java" | ForEach-Object { $_.FullName })

if ($?) {
    Write-Host "Compilation successful."
} else {
    Write-Host "Compilation failed."
    exit 1
}

Write-Host "Running the Main class..."
Write-Host ""
$classpath = "$compiledClassesPath;$dependenciesPath"  # Include both compiled classes and dependencies
java -cp $classpath $mainClass

rm .\backend\ressources\databases\AutoRent.db
