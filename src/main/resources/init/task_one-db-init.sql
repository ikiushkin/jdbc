DROP DATABASE IF EXISTS employees;
CREATE DATABASE employees;
USE employees;

DROP TABLE IF EXISTS employee;

CREATE TABLE EMPLOYEE (
    ID INT NOT NULL AUTO_INCREMENT,
    NAME VARCHAR(100) NOT NULL,
    SALARY DECIMAL(15, 2) NOT NULL,
    CREATED_DATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (ID)
);

INSERT INTO EMPLOYEE (name, salary) values ('Igor', 25000.00),
                                           ('Sergey', 23000.00),
                                           ('Alla', 27000.00),
                                           ('Andrey', 30000.00),
                                           ('Ekaterina', 35000.00);

