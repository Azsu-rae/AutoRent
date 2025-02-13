$compiledClassesPath = ".\backend\bin\"
$dependenciesPath = ".\backend\lib\sqlite-jdbc-3.47.1.0.jar"  
$mainClass = ".\backend\bin\Main"  
$sourcePath = ".\backend\src\"
$classpath = "$compiledClassesPath;$dependenciesPath"  

Write-Host "Compiling Java files from $sourcePath..."
javac -d backend/bin/ -cp backend/lib/sqlite-jdbc-3.47.1.0.jar -sourcepath backend/src/ (Get-ChildItem -Path backend/src/ -Recurse -Filter *.java | ForEach-Object { $_.FullName })

if ($?) {
    Write-Host "Compilation successful."
} else {
    Write-Host "Compilation failed."
    exit 1  
}

Write-Host "Running the Main class..."
Write-Host ""
java -cp $classpath Main
rm .\backend\ressources\databases\AutoRent.db
