-- SQL 优化练习：覆盖索引、前缀索引、深分页、小表驱动大表、消filesort
-- dbtest30
USE dbtest30;

-- 准备数据
CREATE TABLE emp(
	id INT PRIMARY KEY AUTO_INCREMENT,
	ename VARCHAR(15) NOT NULL,
	deptno INT,
	salary DOUBLE(10,2),
	email VARCHAR(100),
	hiredate DATE
);
DELIMITER $$
CREATE PROCEDURE fill_emp()
BEGIN
	DECLARE i INT DEFAULT 1;
	START TRANSACTION;
	WHILE i <= 20000 DO
		INSERT INTO emp(ename,deptno,salary,email,hiredate)
		VALUES(CONCAT('员工',i), FLOOR(1+RAND()*10), 3000+RAND()*12000,
			CONCAT('user',i,'@company.com'),
			DATE_ADD('2020-01-01', INTERVAL FLOOR(RAND()*2000) DAY));
		SET i = i+1;
	END WHILE;
	COMMIT;
END$$
DELIMITER ;
CALL fill_emp();
DROP PROCEDURE fill_emp;

-- 1. 覆盖索引：查询列全在索引里，不回表（Extra 显示 Using index；库里候选索引多时可能显示 Using index condition）
CREATE INDEX idx_dept_sal ON emp(deptno, salary);
EXPLAIN SELECT deptno, salary FROM emp WHERE deptno = 3 AND salary > 5000;

-- 对照：多取一个不在索引里的列就回表，Using index 消失
EXPLAIN SELECT deptno, salary, ename FROM emp WHERE deptno = 3 AND salary > 5000;

-- 2. 前缀索引：选择性对比
SELECT COUNT(DISTINCT email) / COUNT(*) AS 整列选择性 FROM emp;
SELECT COUNT(DISTINCT LEFT(email,10)) / COUNT(*) AS 前10位选择性 FROM emp;
SELECT COUNT(DISTINCT LEFT(email,6)) / COUNT(*) AS 前6位选择性 FROM emp;
CREATE INDEX idx_email_pre ON emp(email(10));
EXPLAIN SELECT * FROM emp WHERE email = 'user100@company.com';
-- type=ref；注意前缀索引拿不到完整email值，必然回表，做不了覆盖索引

-- 3. 深分页：LIMIT 大偏移 vs 先取主键再关联
EXPLAIN SELECT * FROM emp ORDER BY id LIMIT 9000, 10;
CREATE INDEX idx_id_sal ON emp(salary);		-- 准备一个可覆盖的场景
-- 慢写法：扫过前9000行全行
SELECT id, salary FROM emp ORDER BY id LIMIT 9000, 10;
-- 快写法：子查询走覆盖索引只碰主键，外层只对10行回表
SELECT e.id, e.salary FROM emp e
JOIN (SELECT id FROM emp ORDER BY id LIMIT 9000, 10) t ON e.id = t.id;
DROP INDEX idx_id_sal ON emp;

-- 4. JOIN 小表驱动大表：内层(被驱动表)必须有索引
CREATE TABLE dept(deptno INT PRIMARY KEY, dname VARCHAR(15));
INSERT INTO dept VALUES(1,'研发'),(2,'测试'),(3,'运维');
EXPLAIN SELECT e.ename, d.dname FROM dept d JOIN emp e ON e.deptno = d.deptno;
-- 2万行的emp当被驱动表走PRIMARY/idx索引，3行的dept当驱动表

-- 5. 排序走索引：等值列放最左，排序列放后面，消filesort
EXPLAIN SELECT * FROM emp WHERE deptno = 3 ORDER BY salary;
-- 有 idx_dept_sal(deptno,salary)：type=ref，无 Using filesort

-- 排序列不在索引里：filesort
EXPLAIN SELECT * FROM emp WHERE deptno = 3 ORDER BY hiredate;
CREATE INDEX idx_dept_date ON emp(deptno, hiredate);
EXPLAIN SELECT * FROM emp WHERE deptno = 3 ORDER BY hiredate;
-- 建了 idx_dept_date 后 filesort 消失

-- 6. GROUP BY 沿索引分组，消 Using temporary
EXPLAIN SELECT deptno, COUNT(*), AVG(salary) FROM emp GROUP BY deptno;
-- idx_dept_sal 最左列 deptno，分组沿索引进行

-- 7. count 语义对比：count(列)不含NULL
SELECT COUNT(*) AS 全部行 FROM emp;
SELECT COUNT(NULL) AS 恒为0 FROM emp;
INSERT INTO emp(ename,deptno,salary,email,hiredate)
	VALUES('无邮箱',1,5000,NULL,'2024-01-01');
SELECT COUNT(email) AS 非NULL行 FROM emp;	-- 比 COUNT(*) 少1，NULL不算

-- 8. 验证失效写法（细节在索引操作篇）
EXPLAIN SELECT * FROM emp WHERE LEFT(email,5) = 'user1';	-- 列上套函数
EXPLAIN SELECT * FROM emp WHERE ename LIKE '%工100';		-- 左模糊

-- 清理
DROP INDEX idx_dept_sal ON emp;
DROP INDEX idx_email_pre ON emp;
DROP INDEX idx_dept_date ON emp;
