$javaSourcePath = ".\frontend\src\bex\" 
$dependenciesSourcePath = ".\backend\src\aex\"  
$dependenciesPath = ".\backend\lib\sqlite-jdbc-3.47.1.0.jar"  
$compiledDependenciesClassesPath = ".\backend\bin\"  
$compiledClassesPath = ".\frontend\bin\"  
$mainClass = ".\frontend\src\MainApp.java" 

if (-Not (Test-Path $compiledClassesPath)) {
    New-Item -Path $compiledClassesPath -ItemType Directory
}

Write-Host "Compiling Java files from $dependenciesSourcePath..."
javac -d $compiledDependenciesClassesPath -cp $dependenciesPath $dependenciesSourcePath*.java

if ($?) {
    Write-Host "Compilation successful."
} else {
    Write-Host "Compilation failed."
    exit 1  
}

Write-Host ""
Write-Host "Compiling Java files from $javaSourcePath..."
javac -d $compiledClassesPath -cp "$compiledDependenciesClassesPath;$dependenciesPath" $javaSourcePath*.java

if ($?) {
    Write-Host "Compilation successful."
} else {
    Write-Host "Compilation failed."
    exit 1  
}

Write-Host "Running the Main class..."
Write-Host ""
$classpath = "$compiledClassesPath;$compiledDependenciesClassesPath;$dependenciesPath"
java -cp $classpath $mainClass
