# Native Image Build Script (English only to avoid encoding issues)

Write-Host "========================================"
Write-Host "  GraalVM Native Image Builder"
Write-Host "========================================"
Write-Host ""

$GRAALVM_HOME = "C:\graalvm\graalvm-community-openjdk-17.0.9+9.1"

Write-Host "GraalVM: $GRAALVM_HOME"
Write-Host ""

Write-Host "Checking Native Image..."
& "$GRAALVM_HOME\bin\native-image.exe" --version

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Native Image not found"
    exit 1
}

Write-Host "OK: Native Image found"
Write-Host ""

Write-Host "[1/3] Compiling Java code..."

if (Test-Path "target\classes") {
    Remove-Item -Recurse -Force "target\classes"
}
New-Item -ItemType Directory -Path "target\classes" -Force | Out-Null

& "$GRAALVM_HOME\bin\javac.exe" -d target/classes src/main/java/com/ocr/jpeg2pdf/SimpleCli.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Compilation failed"
    exit 1
}

Write-Host "OK: Compilation successful"
Write-Host ""

Write-Host "[2/3] Copying resources..."

New-Item -ItemType Directory -Path "target\classes\META-INF\native-image" -Force | Out-Null

Copy-Item "src\main\resources\META-INF\MANIFEST.MF" "target\classes\META-INF\" -Force
Copy-Item "src\main\resources\META-INF\native-image\native-image.properties" "target\classes\META-INF\native-image\" -Force
Copy-Item "src\main\resources\META-INF\native-image\reflection-config.json" "target\classes\META-INF\native-image\" -Force

Write-Host "OK: Resources copied"
Write-Host ""

Write-Host "[3/3] Creating JAR..."

Push-Location target\classes
& "$GRAALVM_HOME\bin\jar.exe" cfm ..\simple-cli.jar ..\..\src\main\resources\META-INF\MANIFEST.MF *
Pop-Location

if (-not (Test-Path "target\simple-cli.jar")) {
    Write-Host "ERROR: JAR creation failed"
    exit 1
}

Write-Host "OK: JAR created"
Write-Host ""

Write-Host "========================================"
Write-Host "  Building Native Image"
Write-Host "========================================"
Write-Host ""
Write-Host "This may take 5-10 minutes, please wait..."
Write-Host ""

& "$GRAALVM_HOME\bin\native-image.exe" --no-fallback --enable-http --enable-https -H:IncludeResources=".*\.json$" -H:IncludeResources=".*\.properties$" -H:Name=jpeg2pdf-ofd -jar target/simple-cli.jar

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERROR: Build failed"
    exit 1
}

Write-Host ""
Write-Host "========================================"
Write-Host "  Build Successful!"
Write-Host "========================================"
Write-Host ""

if (Test-Path "jpeg2pdf-ofd.exe") {
    $exe = Get-Item "jpeg2pdf-ofd.exe"
    Write-Host "Output file: $($exe.Name)"
    Write-Host "Size: $([math]::Round($exe.Length/1MB,2)) MB"
    Write-Host "Location: $($exe.FullName)"
    Write-Host ""
    Write-Host "Test run:"
    Write-Host "  .\jpeg2pdf-ofd.exe --help"
} else {
    Write-Host "ERROR: EXE file not generated"
}

Write-Host ""
