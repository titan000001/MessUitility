$ErrorActionPreference = "Stop"

$libDir = "lib"
if (-not (Test-Path $libDir)) {
    New-Item -ItemType Directory -Force -Path $libDir | Out-Null
}

Write-Host "Downloading FlatLaf (UI Framework)..."
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/com/formdev/flatlaf/3.4/flatlaf-3.4.jar" -OutFile "$libDir/flatlaf.jar"

Write-Host "Downloading SQLite JDBC..."
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.1.0/sqlite-jdbc-3.45.1.0.jar" -OutFile "$libDir/sqlite-jdbc.jar"

Write-Host "Downloading SLF4J API (required by new SQLite JDBC)..."
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar" -OutFile "$libDir/slf4j-api.jar"
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar" -OutFile "$libDir/slf4j-simple.jar"

Write-Host "Downloading OpenPDF (for PDF export)..."
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/com/github/librepdf/openpdf/1.3.30/openpdf-1.3.30.jar" -OutFile "$libDir/openpdf.jar"

Write-Host "All libraries downloaded successfully to /lib folder!"
