# PowerShell launcher for LibManagement
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $projectRoot

#$javaPath = "$env:JAVA_HOME\bin\java.exe"
$javaPath = "C:\Program Files\Java\jre-1.8\bin\java.exe"
$libs = Join-Path $projectRoot 'libs\*'
$classes = Join-Path $projectRoot 'target\classes'
# Optional: VS Code jdt compiled path (update if different)
$jdt = 'C:\Users\ariel\AppData\Roaming\Code\User\workspaceStorage\e0454cc7e17a631588d2e9f6bfca623d\redhat.java\jdt_ws\jdt.ls-java-project\bin'
$cp = "$libs;$classes;."
if (Test-Path $jdt) { $cp = "$cp;$jdt" }
& $javaPath -cp $cp abservices.libmanagement.LibManagement
Read-Host -Prompt "Press Enter to exit"
