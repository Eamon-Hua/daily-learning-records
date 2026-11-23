USE dbtest24;

CREATE TABLE IF NOT EXISTS emp(
id INT PRIMARY KEY AUTO_INCREMENT,
ename VARCHAR(15),
salary DOUBLE(10,2)
);

CREATE TABLE IF NOT EXISTS emp_log(
id INT PRIMARY KEY AUTO_INCREMENT,
内容 VARCHAR(100),
时间 DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- BEFORE：拦下不合法数据并修正
DELIMITER //

CREATE TRIGGER 拦截低薪
BEFORE INSERT ON emp
FOR EACH ROW
BEGIN
IF NEW.salary < 0 THEN
SET NEW.salary=0;	-- BEFORE里能改NEW，还没落库来得及修正
END IF;
END //

-- AFTER：确认写进去了再记日志
CREATE TRIGGER 记入职日志
AFTER INSERT ON emp
FOR EACH ROW
BEGIN
INSERT INTO emp_log(内容) VALUES(CONCAT('新员工：',NEW.ename,'，薪资：',NEW.salary));
END //

-- UPDATE：OLD和NEW都有，前后一对比就知道改了什么
CREATE TRIGGER 记调薪日志
AFTER UPDATE ON emp
FOR EACH ROW
BEGIN
IF OLD.salary <> NEW.salary THEN
INSERT INTO emp_log(内容) VALUES(CONCAT(OLD.ename,'：',OLD.salary,' → ',NEW.salary));
END IF;
END //

-- DELETE：只有OLD，被删的那行还能捞到
CREATE TRIGGER 记离职日志
AFTER DELETE ON emp
FOR EACH ROW
BEGIN
INSERT INTO emp_log(内容) VALUES(CONCAT('离职：',OLD.ename));
END //

DELIMITER ;

INSERT INTO emp(ename,salary) VALUES('张三',5000);	-- 触发拦截器和入职日志
INSERT INTO emp(ename,salary) VALUES('李四',-500);	-- 负数被BEFORE改成0
INSERT INTO emp(ename,salary) VALUES('王五',8000);
UPDATE emp SET salary=6000 WHERE ename='张三';	-- 触发调薪日志
DELETE FROM emp WHERE ename='王五';	-- 触发离职日志

SELECT * FROM emp;
SELECT * FROM emp_log;

SHOW TRIGGERS;

DROP TRIGGER 拦截低薪;
DROP TRIGGER 记入职日志;
DROP TRIGGER 记调薪日志;
DROP TRIGGER 记离职日志;
