# PowerShell launcher for LibManagement
# Set the current location to the script's directory to ensure relative paths work.
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $projectRoot

# Check for Maven and compile the project
Write-Host "[INFO] Checking for Maven to compile the project..."
if (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Host "[INFO] Maven found. Compiling project..."
    mvn clean compile
    if ($LASTEXITCODE -ne 0) {
        Write-Error "[ERROR] Maven compilation failed. Please check the errors above and try again."
        Read-Host -Prompt "Press Enter to exit"
        exit
    }
} else {
    Write-Warning "[WARN] Maven (mvn) not found in PATH. Assuming project is already compiled."
}

# Determine the java executable path
if ($env:JAVA_HOME) {
    $javaPath = Join-Path $env:JAVA_HOME 'bin\java.exe'
} else {
    Write-Warning "JAVA_HOME environment variable not set. Assuming 'java' is in the system PATH."
    $javaPath = "java"
}

# Define classpath components relative to the project root
$libs = Join-Path $projectRoot 'libs\*'
$classes = Join-Path $projectRoot 'target\classes'

# Construct the classpath string (uses semicolon for Windows)
$cp = "$libs;$classes;."

& $javaPath -cp $cp abservices.libmanagement.LibManagement
Read-Host -Prompt "Press Enter to exit"
