CREATE DATABASE library;

USE library;

/* 1 - book_record */
CREATE TABLE book_record (
    book_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    summary TEXT NOT NULL,
    rating INT
);
