$javaSourcePath = ".\backend\src\"
$compiledClassesPath = ".\backend\bin\"
$dependenciesPath = ".\backend\lib\sqlite-jdbc-3.47.1.0.jar"
$mainClass = ".\backend\src\Main.java"

$BjavaSourcePath = ".\frontend\src\"
$BcompiledClassesPath = ".\frontend\bin\"
$BdependenciesPath = $compiledClassesPath
$BmainClass = ".\frontend\src\dashboard\MainApp.java"

Write-Host "Compiling Java files from $javaSourcePath..."
javac -d $compiledClassesPath -cp $dependenciesPath $(Get-ChildItem -Path $javaSourcePath -Recurse -Filter "*.java" | ForEach-Object { $_.FullName })

if ($?) {
    Write-Host "Compilation successful."
} else {
    Write-Host "Compilation failed."
    exit 1
}

Write-Host ""
Write-Host "Compiling Java files from $BjavaSourcePath..."
javac -d $BcompiledClassesPath -cp $BdependenciesPath $(Get-ChildItem -Path $BjavaSourcePath -Recurse -Filter "*.java" | ForEach-Object { $_.FullName })

if ($?) {
    Write-Host "Compilation successful."
} else {
    Write-Host "Compilation failed."
    exit 1
}

Write-Host ""
Write-Host "Running the MainApp..."
Write-Host ""
$classpath = "$BcompiledClassesPath;$compiledClassesPath;$dependenciesPath"  # Include both compiled classes and dependencies
java -cp $classpath $BmainClass
