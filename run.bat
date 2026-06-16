@echo off
REM Launcher for LibManagement — adjusts classpath so connector in libs/ is used.
SETLOCAL

REM Determine java executable
if defined JAVA_HOME (
  set "JAVACMD=%JAVA_HOME%\bin\java.exe"
) else (
  set "JAVACMD=C:\Program Files\Java\jre-1.8\bin\java.exe"
)

REM Default classpath entries: all jars in libs, target\classes, and current dir
set "CP=libs\*;target\classes;."

REM Optional: include VS Code jdt compiled classes path if present — update if necessary
set "JDT_PATH=C:\Users\ariel\AppData\Roaming\Code\User\workspaceStorage\e0454cc7e17a631588d2e9f6bfca623d\redhat.java\jdt_ws\jdt.ls-java-project\bin"
if exist "%JDT_PATH%" (
  set "CP=%CP%;%JDT_PATH%"
)

"%JAVACMD%" -cp "%CP%" bonifacio_bscs1a.libmanagement.LibManagement
ENDLOCAL
pause
