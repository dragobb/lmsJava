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

REM Classpath includes all JARs in libs, the compiled classes, and the current directory (.).
set "CP=%PROJECT_ROOT%libs\*;%PROJECT_ROOT%target\classes;."

echo Running with classpath: %CP%
REM The entire classpath string is wrapped in quotes to handle spaces in paths.
"%JAVACMD%" -cp "%CP%" abservices.libmanagement.LibManagement

:end
ENDLOCAL
pause
