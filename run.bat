@echo off
REM Launcher for LibManagement — adjusts classpath so connector in libs/ is used.
SETLOCAL
REM Set the project root to the directory where this script is located.
set "PROJECT_ROOT=%~dp0"

REM Determine java executable
if defined JAVA_HOME (
  set "JAVACMD=%JAVA_HOME%\bin\java.exe"
) else (
  echo WARNING: JAVA_HOME is not set. Assuming 'java' is in the system PATH.
  set "JAVACMD=java"
)

REM Default classpath entries: all jars in libs, target\classes, and current dir
set "CP=%PROJECT_ROOT%libs\*;%PROJECT_ROOT%target\classes;."

REM Optional: include VS Code jdt compiled classes path if present — update if necessary
REM This is a more generic way to find the VSCode JDT path, but it's complex.
REM For now, we rely on `target/classes` which is the standard.

echo Running with classpath: %CP%
"%JAVACMD%" -cp "%CP%" abservices.libmanagement.LibManagement
ENDLOCAL
pause
