USE dbtest21;

CREATE TABLE IF NOT EXISTS emp(
id INT PRIMARY KEY AUTO_INCREMENT,
ename VARCHAR(15),
salary DOUBLE(10,2),
dept_id INT
);

INSERT INTO emp(ename,salary,dept_id) VALUES('张三',5000,10);
INSERT INTO emp(ename,salary,dept_id) VALUES('李四',8000,10);
INSERT INTO emp(ename,salary,dept_id) VALUES('王五',12000,20);

SET GLOBAL log_bin_trust_function_creators=1;

DELIMITER //

CREATE FUNCTION 涨后的工资(salary DOUBLE)
RETURNS DOUBLE
DETERMINISTIC
BEGIN
RETURN salary*1.1;
END //

SELECT 涨后的工资(5000) //
SELECT ename,salary,涨后的工资(salary) FROM emp //

CREATE FUNCTION 部门人数(d INT)
RETURNS INT
READS SQL DATA
BEGIN
DECLARE n INT DEFAULT 0;
SELECT COUNT(*) INTO n FROM emp WHERE dept_id=d;
RETURN n;
END //

SELECT 部门人数(10) //

SELECT ename,dept_id,部门人数(dept_id) FROM emp //

CREATE FUNCTION 年龄段(age INT)
RETURNS VARCHAR(10)
DETERMINISTIC
BEGIN
IF age<18 THEN RETURN '未成年';
ELSEIF age<60 THEN RETURN '成年人';
ELSE RETURN '老年';
END IF;
END //

SELECT 年龄段(15),年龄段(30),年龄段(70) //

DELIMITER ;

SHOW CREATE FUNCTION 部门人数;

DROP FUNCTION 年龄段;
