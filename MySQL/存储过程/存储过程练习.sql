USE dbtest20;

CREATE TABLE IF NOT EXISTS emp(
id INT PRIMARY KEY AUTO_INCREMENT,
ename VARCHAR(15),
salary DOUBLE(10,2),
dept_id INT
);

INSERT INTO emp(ename,salary,dept_id) VALUES('张三',5000,10);
INSERT INTO emp(ename,salary,dept_id) VALUES('李四',8000,10);
INSERT INTO emp(ename,salary,dept_id) VALUES('王五',12000,20);
INSERT INTO emp(ename,salary,dept_id) VALUES('赵六',9000,20);

DELIMITER //

CREATE PROCEDURE 无参演示()
BEGIN
SELECT * FROM emp;
END //

CALL 无参演示() //

CREATE PROCEDURE 按部门查人数(IN d INT)
BEGIN
SELECT COUNT(*) FROM emp WHERE dept_id=d;
END //

CALL 按部门查人数(10) //

CREATE PROCEDURE 工资统计(IN 部门 INT,OUT 总数 INT,OUT 平均工资 DOUBLE,OUT 最高 DOUBLE)
BEGIN
SELECT COUNT(*),AVG(salary),MAX(salary) INTO 总数,平均工资,最高 FROM emp WHERE dept_id=部门;
END //

CALL 工资统计(10,@总数,@均薪,@最高) //
SELECT @总数,@均薪,@最高 //
CALL 工资统计(20,@总数,@均薪,@最高) //
SELECT @总数,@均薪,@最高 //

CREATE PROCEDURE 涨薪(INOUT 工资 DOUBLE)
BEGIN
SET 工资=工资*1.1;
END //

SET @薪资=8000 //
CALL 涨薪(@薪资) //
SELECT @薪资 //

CREATE PROCEDURE 试改IN参数(IN x INT)
BEGIN
SET x=x+100;
SELECT x;
END //

SET @原值=1 //
CALL 试改IN参数(@原值) //
SELECT @原值 //
-- @原值还是1，IN改了不回传

CREATE PROCEDURE 带判断的过程(IN d INT)
BEGIN
IF d=10 THEN
SELECT '这是10号部门';
ELSE
SELECT '不是10号部门';
END IF;
END //

CALL 带判断的过程(10) //
CALL 带判断的过程(99) //

CREATE PROCEDURE 带循环的过程(IN n INT)
BEGIN
DECLARE i INT DEFAULT 1;
DECLARE 总和 INT DEFAULT 0;

WHILE i<=n DO
SET 总和=总和+i;
SET i=i+1;
END WHILE;

SELECT 总和;
END //

CALL 带循环的过程(100) //
-- 输出5050

DELIMITER ;

SHOW CREATE PROCEDURE 工资统计;

DROP PROCEDURE 带判断的过程;
DROP PROCEDURE 带循环的过程;
