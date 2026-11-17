USE dbtest19;

CREATE TABLE IF NOT EXISTS emp(
id INT PRIMARY KEY AUTO_INCREMENT,
ename VARCHAR(15),
salary DOUBLE(10,2),
dept_id INT
);

INSERT INTO emp(ename,salary,dept_id) VALUES('张三',5000,10);
INSERT INTO emp(ename,salary,dept_id) VALUES('李四',8000,10);
INSERT INTO emp(ename,salary,dept_id) VALUES('王五',12000,20);

SHOW GLOBAL VARIABLES;
SHOW SESSION VARIABLES LIKE 'autocommit';
SELECT @@global.autocommit;
SELECT @@session.autocommit;
SELECT @@global.max_connections;

SET @@global.time_zone='+08:00';
SET @@session.time_zone='+08:00';
SELECT @@global.time_zone,@@session.time_zone;

SET @name='张三';
SELECT @name;

SET @count:=(SELECT COUNT(*) FROM emp);
SELECT @count;

SELECT AVG(salary) INTO @avg_sal FROM emp;
SELECT @avg_sal;

SELECT MAX(salary) INTO @max_sal FROM emp;
SELECT CONCAT('最高工资是',@max_sal);

DELIMITER //

CREATE PROCEDURE 局部变量演示()
BEGIN
DECLARE a INT DEFAULT 0;
DECLARE b INT DEFAULT 0;

SELECT COUNT(*) INTO a FROM emp;
SET b=a*10;
SELECT a,b;
END //

CALL 局部变量演示() //

CREATE PROCEDURE 局部变量比较()
BEGIN
DECLARE 块内变量 INT DEFAULT 100;
SET @用户变量=999;

SELECT 块内变量,@用户变量;
END //

CALL 局部变量比较() //

SELECT 块内变量 //


DELIMITER ;
