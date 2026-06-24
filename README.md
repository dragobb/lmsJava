# Library Management System

A Java Swing desktop application for managing books, borrowers, and user login for a school or campus library.

## Project Overview

This project is a Library Management System built with:
- Java Swing for the user interface
- MySQL for the backend database
- JDBC for database connectivity
- Local driver loading support from `libs/mysql-connector-j-8.0.32.jar`

The main application classes are:
- `bonifacio_bscs1a.libmanagement.LogIn` — login screen and database authentication
- `bonifacio_bscs1a.libmanagement.RegisterForm` — user registration form
- `bonifacio_bscs1a.libmanagement.LibManagement` — main library dashboard
- `bonifacio_bscs1a.libmanagement.DriverLoader` — dynamic MySQL driver loader
- `bonifacio_bscs1a.libmanagement.DriverShim` — wrapper to register the JDBC driver

## Key Features

- Secure login with SHA-256 hashed passwords
- User registration screen
- Add new books with images
- View and refresh book list
- Search and filter books by title, author, or ISBN
- Borrower list view and borrowed book details
- Mark books borrowed/returned in the database
- Custom UI styling with image backgrounds, icons, and modern buttons

## Project Structure

- `pom.xml` — Maven project file and dependency management
- `src/main/java/bonifacio_bscs1a/libmanagement/` — main source code
- `libs/` — external libraries like MySQL Connector/J
- `library_db.sql` — database schema for MySQL
- `.vscode/launch.json` — VS Code launch configuration for the project
- `images/` — UI image assets used by the application

## Requirements

- Java 8 or newer runtime
- MySQL server (or XAMPP MySQL)
- MySQL Connector/J driver jar in `libs/mysql-connector-j-8.0.32.jar`
- A working database named `library_db`

> Note: The `pom.xml` currently targets Java 21, so a JDK is recommended for Maven builds. For running the app only, a Java runtime is enough if the code is compiled successfully.

## Database Setup

Use `library_db.sql` to create the required database and tables.

Example:
```sql
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(64) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS books (
  title VARCHAR(255) NOT NULL PRIMARY KEY,
  author VARCHAR(255) NOT NULL,
  isbn VARCHAR(100) NOT NULL,
  available TINYINT(1) NOT NULL DEFAULT 1,
  image_path VARCHAR(1024),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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

A sample account can be created manually after import. Example:
- Username: `admin`
- Password: `admin123`

## Running the Application

### Option 1: Use the included launcher

From Windows PowerShell in project root:
```powershell
.
un.ps1
```

Or double-click:
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
& "C:\Program Files\Java\jre-1.8\bin\java.exe" -cp "libs\mysql-connector-j-8.0.32.jar;target\classes;." bonifacio_bscs1a.libmanagement.LogIn
```

### Option 4: Run with Maven

If you have a JDK installed:
```powershell
mvn clean compile exec:java -Dexec.mainClass="bonifacio_bscs1a.libmanagement.LogIn"
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
- Edit the source in `src/main/java/bonifacio_bscs1a/libmanagement/`
- Keep assets in `images/` and external jars in `libs/`
- Use the `library_db.sql` file to manage the database schema
