-- SQL schema for the Library Management System used by this project
-- Import this file into XAMPP/MySQL to create the required database and tables.

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

-- Sample user account
-- Password is SHA-256 hashed. Example password: admin123
INSERT INTO users (username, password) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');
