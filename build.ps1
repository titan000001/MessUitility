# PowerShell script to build the Java application and package it as an .exe using jpackage

$appName = "MessUtility"
$version = "1.0.0"

Write-Host "Cleaning up previous builds..."
if (Test-Path "out") { Remove-Item -Recurse -Force "out" }
if (Test-Path "dist") { Remove-Item -Recurse -Force "dist" }

Write-Host "Creating output directories..."
New-Item -ItemType Directory -Force -Path "out/classes" | Out-Null
New-Item -ItemType Directory -Force -Path "dist" | Out-Null

Write-Host "Compiling Java source files..."
$classpath = "lib/*"
javac -cp $classpath -d out/classes src/com/messutility/models/users/*.java src/com/messutility/models/expenses/*.java src/com/messutility/models/tracker/*.java src/com/messutility/core/*.java src/com/messutility/db/*.java src/com/messutility/gui/*.java src/com/messutility/*.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "Copying dependencies to dist folder..."
Copy-Item -Path "lib/*.jar" -Destination "dist/" -Force

Write-Host "Packaging into JAR..."
jar cvfm dist/${appName}.jar manifest.txt -C out/classes .

if ($LASTEXITCODE -ne 0) {
    Write-Host "JAR packaging failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "Running jpackage to create a native Windows app image (.exe)..."
# We use 'app-image' because building an installer (.exe or .msi) usually requires the WiX toolset.
# This creates a standalone folder containing the executable.
$jpackageArgs = @(
    "--type", "app-image",
    "--name", $appName,
    "--input", "dist",
    "--main-jar", "${appName}.jar",
    "--dest", "out",
    "--app-version", $version
)
& jpackage @jpackageArgs

if ($LASTEXITCODE -eq 0) {
    Write-Host "Build successful! You can find your .exe inside the out/${appName} directory." -ForegroundColor Green
} else {
    Write-Host "jpackage failed!" -ForegroundColor Red
}
