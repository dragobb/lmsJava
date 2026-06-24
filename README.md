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

The project includes two launcher scripts that automatically handle classpath setup.

#### Method A: PowerShell Script
1. Open PowerShell
2. Navigate to the project root:
   ```powershell
   cd C:\Users\ariel\Documents\Java Projects\lmsJava
   ```
3. Run the launcher:
   ```powershell
   .\run.ps1
   ```

#### Method B: Batch File (Easiest)
Simply **double-click** `run.bat` in File Explorer, or run from Command Prompt:
```cmd
run.bat
```

#### Method C: PowerShell Terminal (Alternative)
If you get execution policy errors in PowerShell, try:
```powershell
powershell -ExecutionPolicy Bypass -File run.ps1
```

**What the launchers do:**
- ✅ Automatically detects Java installation
- ✅ Sets classpath to include `libs\*` and `target\classes`
- ✅ Launches the application with the correct package `abservices.libmanagement.LibManagement`
- ✅ Includes VS Code JDT compiled classes if available

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
   This command runs the application using the compiled classes and libraries.
    ```powershell
    # The classpath separator is ; on Windows and : on Linux/Mac
    java -cp "target/classes;libs/*" abservices.libmanagement.LibManagement
    ```

### Option 3: Create a Standalone JAR (Advanced)

To create a single, all-in-one executable JAR, add the `maven-shade-plugin` to your `pom.xml` inside the `<plugins>` section.

1.  **Add the plugin to `pom.xml`**:
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
                </configuration>
            </execution>
        </executions>
    </plugin>
    ```

2.  **Build the "fat" JAR**:
    ```powershell
    mvn clean package
    ```

3.  **Run the standalone JAR**:
    ```powershell
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

##  Author

**Developed by**: ariel

---

## 📄 License

This project is provided as-is for educational purposes.
