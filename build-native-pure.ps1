# Native Image Build Script - Pure Java SE (No dependencies)

Write-Host "========================================"
Write-Host "  Building Native Image (No dependencies)"
Write-Host "========================================"
Write-Host ""

$GRAALVM_HOME = "C:\graalvm\graalvm-community-openjdk-17.0.9+9.1"
$NATIVE_IMAGE = "$GRAALVM_HOME\lib\svm\bin\native-image.exe"

Write-Host "GraalVM: $GRAALVM_HOME"
Write-Host ""

Write-Host "Checking Native Image..."
& $NATIVE_IMAGE --version

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Native Image not found"
    exit 1
}

Write-Host "OK: Native Image found"
Write-Host ""

Write-Host "[1/3] Compiling NativeImageCli.java..."

# Create output directory
if (Test-Path "target\native") {
    Remove-Item -Recurse -Force "target\native"
}
New-Item -ItemType Directory -Path "target\native" -Force | Out-Null

# Compile (pure Java SE, no dependencies)
& "$GRAALVM_HOME\bin\javac.exe" -d target/native src/main/java/com/ocr/jpeg2pdf/NativeImageCli.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Compilation failed"
    exit 1
}

Write-Host "OK: Compilation successful"
Write-Host ""

Write-Host "[2/3] Creating JAR..."

# Create JAR
Push-Location target\native
& "$GRAALVM_HOME\bin\jar.exe" cfe ../native-cli.jar com.ocr.jpeg2pdf.NativeImageCli com/ocr/jpeg2pdf/*.class
Pop-Location

if (-not (Test-Path "target\native-cli.jar")) {
    Write-Host "ERROR: JAR creation failed"
    exit 1
}

Write-Host "OK: JAR created"
Write-Host ""

Write-Host "[3/3] Building Native Image..."
Write-Host ""
Write-Host "This will take 5-10 minutes..."
Write-Host ""

# Build Native Image with minimal options
& $NATIVE_IMAGE `
  --no-fallback `
  -H:Name=jpeg2pdf-ofd `
  -jar target/native-cli.jar

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERROR: Build failed"
    Write-Host ""
    Write-Host "Check error messages above"
    exit 1
}

Write-Host ""
Write-Host "========================================"
Write-Host "  Build Successful!"
Write-Host "========================================"
Write-Host ""

if (Test-Path "jpeg2pdf-ofd.exe") {
    $exe = Get-Item "jpeg2pdf-ofd.exe"
    Write-Host "Output: $($exe.Name)"
    Write-Host "Size: $([math]::Round($exe.Length/1MB,2)) MB"
    Write-Host "Path: $($exe.FullName)"
    Write-Host ""
    Write-Host "========================================"
    Write-Host "  Test"
    Write-Host "========================================"
    Write-Host ""
    Write-Host "Show help:"
    Write-Host "  .\jpeg2pdf-ofd.exe --help"
    Write-Host ""
    Write-Host "Convert image:"
    Write-Host "  .\jpeg2pdf-ofd.exe image.jpg output/"
    Write-Host ""
} else {
    Write-Host "ERROR: EXE not generated"
}

Write-Host ""
