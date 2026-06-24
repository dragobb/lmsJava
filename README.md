# 📚 Library Management System

> A professional Java Swing desktop application for managing books, borrowers, and user authentication in a school or campus library.

---

## 🎯 Project Overview

**Library Management System (LMS)** is a feature-rich desktop application designed to streamline library operations with an intuitive graphical interface and robust database backend.

### Tech Stack
- **Frontend**: Java Swing
- **Backend**: MySQL Database
- **Build Tool**: Maven
- **Java Version**: 21
- **Database Driver**: MySQL Connector/J 8.0.32

### Core Modules
| Module | Purpose |
|--------|---------|
| `LogIn.java` | Secure login with SHA-256 password hashing |
| `RegisterForm.java` | User registration interface |
| `LibManagement.java` | Main dashboard & book management |
| `DriverLoader.java` | Dynamic MySQL driver loader |
| `DriverShim.java` | JDBC driver wrapper & registration |

---

## ✨ Key Features

- ✅ **Secure Authentication** — SHA-256 encrypted login system
- ✅ **User Registration** — Easy user account creation
- ✅ **Book Management** — Add, view, and manage books with cover images
- ✅ **Advanced Search** — Filter books by title, author, or ISBN
- ✅ **Borrower Tracking** — Complete borrower database with borrow/return history
- ✅ **Dynamic UI** — Custom styling with backgrounds, icons, and modern components
- ✅ **Database Integration** — Persistent storage with MySQL

---

## 📂 Project Structure

```
lmsJava/
├── src/main/java/abservices/libmanagement/
│   ├── LogIn.java
│   ├── RegisterForm.java
│   ├── LibManagement.java
│   ├── DriverLoader.java
│   ├── DriverShim.java
│   ├── borrowersTable.java
│   ├── WrapLayout.java
│   ├── DropShadowBorder.java
│   └── *.form (NetBeans GUI files)
├── libs/
│   └── mysql-connector-j-8.0.32.jar
├── images/
│   └── [UI assets and icons]
├── pom.xml (Maven configuration)
├── library_db.sql (Database schema)
├── run.bat (Windows batch launcher)
├── run.ps1 (PowerShell launcher)
└── README.md (This file)
```

---

## 🔧 Requirements

| Component | Minimum | Recommended |
|-----------|---------|-------------|
| **Java** | JDK 8 | JDK 21 (for building) |
| **MySQL** | 5.7 | 8.0+ |
| **MySQL Driver** | 8.0.32 | Latest |
| **OS** | Windows/Linux/Mac | Windows 10+ |

> **Note**: A JDK is required for Maven builds. For running compiled code, Java Runtime Environment (JRE) is sufficient.

---

## 🗄️ Database Setup

### Option A: Import SQL Script
Run the provided `library_db.sql` file in your MySQL client:

```sql
-- Create database
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(64) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Books table
CREATE TABLE IF NOT EXISTS books (
  title VARCHAR(255) NOT NULL PRIMARY KEY,
  author VARCHAR(255) NOT NULL,
  isbn VARCHAR(100) NOT NULL,
  available TINYINT(1) NOT NULL DEFAULT 1,
  image_path VARCHAR(1024),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Borrowers table
CREATE TABLE IF NOT EXISTS borrowers (
  id INT AUTO_INCREMENT PRIMARY KEY,
  member_name VARCHAR(255) NOT NULL,
  school_id VARCHAR(100) NOT NULL,
  member_image VARCHAR(1024),
  book_title VARCHAR(255) NOT NULL,
  borrow_date DATE NOT NULL,
  returned_date DATE DEFAULT NULL,
  FOREIGN KEY (book_title) REFERENCES books(title) ON DELETE CASCADE
);
```

### Option B: Default Test Account
After database import, you can use:
```
Username: admin
Password: admin123
```

---

## 🚀 Running the Application

### Option 1: Quick Launcher (⭐ Recommended)

#### Windows Command Prompt / PowerShell:
```powershell
# From project root directory
.\run.ps1
```

#### Or double-click:
```
run.bat
```

This automatically sets the classpath and launches the application.

### Option 2: Maven Build & Run

1. **Verify JDK installation**:
   ```powershell
   java -version
   javac -version
   ```

2. **Build with Maven**:
   ```powershell
   mvn clean package
   ```

3. **Run the compiled JAR**:
   ```powershell
   java -cp "target/classes;libs/*" abservices.libmanagement.LibManagement
   ```

### Option 3: Create a Standalone JAR (Advanced)

Add Maven Shade plugin to `pom.xml` for a fat JAR:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.2.4</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>abservices.libmanagement.LibManagement</mainClass>
                    </transformer>
                </transformers>
                <finalName>LibManagement-1.0-SNAPSHOT-shaded</finalName>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Then run:
```powershell
mvn clean package
java -jar target/LibManagement-1.0-SNAPSHOT-shaded.jar
```

---

## 🛠️ Setup Instructions for VS Code

1. **Add Referenced Libraries**:
   - Right-click `Referenced Libraries` in the Java Projects view
   - Select `Add Jar` → `libs\mysql-connector-j-8.0.32.jar`

2. **Compile**:
   - VS Code Java Extension will compile automatically on save
   - Or run: `mvn clean compile`

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| "Driver not found" error | Ensure `mysql-connector-j-8.0.32.jar` is in `libs/` folder |
| Cannot connect to database | Verify MySQL is running and `library_db` exists |
| Classpath errors | Run from project root and use provided launchers |
| Java version mismatch | Update Java to version 21 or check `pom.xml` compiler settings |
| Application won't start | Check console output for detailed error messages |

### Getting Help
- Run the application from a console window to capture full error messages
- Verify all dependencies are properly installed
- Check database connection credentials in the code

---

## 📝 Notes

- The MySQL driver jar is bundled locally in `libs/` for portability
- The application uses dynamic class loading to support multiple driver locations
- All passwords are securely hashed using SHA-256
- The launcher expects `libs\*` and `target\classes` in the classpath

---

## 👤 Author

**Developed by**: ariel

---

## 📄 License

This project is provided as-is for educational purposes.
```powershell
run.bat
```

### Option 2: Run from VS Code

1. Install the Java extension pack.
2. Open the project folder.
3. Add `libs/mysql-connector-j-8.0.32.jar` to Referenced Libraries if needed.
4. Run the `.vscode/launch.json` configuration: `Launch LibManagement (LogIn)`.

### Option 3: Run directly with `java`

From project root:
```powershell
& "C:\Program Files\Java\jre-1.8\bin\java.exe" -cp "libs\mysql-connector-j-8.0.32.jar;target\classes;." abservices.libmanagement.LogIn
```

### Option 4: Run with Maven

If you have a JDK installed:
```powershell
mvn clean compile exec:java -Dexec.mainClass="abservices.libmanagement.LogIn"
```

## VS Code / Classpath Notes

If VS Code does not detect the MySQL driver automatically:
- Add the jar from `libs/mysql-connector-j-8.0.32.jar` to Referenced Libraries.
- Refresh the project and restart the Java language server.
- The app also supports `LIBS_DIR` for IDE launch configs.

## Dependencies

- `com.mysql:mysql-connector-j:8.0.32`
- `com.formdev:flatlaf:3.2` (UI styling)
- `com.aeontronix.enhanced-mule:enhanced-mule-tools-cli:1.3.0-beta23` (project dependency present in POM)

## Known files to check

- `README-FIX.md` — extra launch instructions and troubleshooting notes.
- `.vscode/launch.json` — VS Code debug/run launch settings.
- `library_db.sql` — database schema to import into MySQL.

## Troubleshooting

### MySQL driver error
- Ensure `libs/mysql-connector-j-8.0.32.jar` exists.
- Ensure the jar is registered in your IDE or included in runtime classpath.
- Use the `run.bat` / `run.ps1` launchers to include the jar automatically.

### Login fails
- Confirm the database is created and `users` table exists.
- Confirm the database credentials in code match your MySQL setup.
- If using a new user, register first through the app or insert into the `users` table.

### Build issues
- If Maven fails with `No compiler is provided in this environment`, install a JDK and run from that JDK.
- If the project is compiled with a different Java version than the runtime, align the JDK/JRE versions.

## Contact

For fixes or improvements:
- Edit the source in `src/main/java/abservices/libmanagement/`
- Keep assets in `images/` and external jars in `libs/`
- Use the `library_db.sql` file to manage the database schema
