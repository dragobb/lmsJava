Run & packaging options — make the app run without typing long java commands

Quick launcher (recommended)
- I added `run.bat` and `run.ps1` to the project root. Double-click `run.bat` or run `.
un.ps1` from PowerShell.
- They set a classpath including `libs\*` and `target\classes` and will include the VS Code jdt compiled folder if present.

Make a permanent runnable JAR (recommended long-term)
1. Install a JDK (required for Maven builds). Verify:
   ```powershell
   java -version
   javac -version
   ```
2. Build a fat JAR with Maven Shade (adds dependencies into one jar):
   - Add the Maven Shade plugin to `pom.xml` (example below).
   - Run:
     ```powershell
     mvn clean package
     ```
   - Then run:
     ```powershell
     java -jar target/LibManagement-1.0-SNAPSHOT-shaded.jar
     ```

Add connector permanently to project classpath (IDE)
- In VS Code Java Projects view, right-click `Referenced Libraries` and `Add Jar` -> select `libs\mysql-connector-j-8.0.32.jar`.
- In other IDEs (IntelliJ/Eclipse), add the jar to the project's Libraries/Classpath.

Notes and troubleshooting
- The launcher expects the connector JAR in `libs\`. If your connector is elsewhere, either move it or update `run.bat`/`run.ps1`.
- If you still get driver errors, run the app from a console so you can copy any error text and send it to me.

If you want, I can add the Maven Shade plugin to `pom.xml` for you (you'll still need a JDK to build).