-- 执行计划 EXPLAIN 练习：type/key/rows/Extra 各字段实测
-- dbtest29
USE dbtest29;

-- 准备数据
CREATE TABLE emp(
	id INT PRIMARY KEY AUTO_INCREMENT,
	ename VARCHAR(15) NOT NULL,
	deptno INT,
	salary DOUBLE(10,2),
	hiredate DATE
);
CREATE TABLE dept(
	deptno INT PRIMARY KEY,
	dname VARCHAR(15)
);

-- 批量造2万行，数据量小优化器会直接全表扫
DELIMITER $$
CREATE PROCEDURE fill_emp()
BEGIN
	DECLARE i INT DEFAULT 1;
	START TRANSACTION;
	WHILE i <= 20000 DO
		INSERT INTO emp(ename,deptno,salary,hiredate)
		VALUES(CONCAT('员工',i), FLOOR(1+RAND()*10), 3000+RAND()*12000,
			DATE_ADD('2020-01-01', INTERVAL FLOOR(RAND()*2000) DAY));
		SET i = i+1;
	END WHILE;
	COMMIT;
END$$
DELIMITER ;

CALL fill_emp();
INSERT INTO dept VALUES(1,'研发'),(2,'测试'),(3,'运维');
DROP PROCEDURE fill_emp;

-- 1. 全表扫：没有任何索引
EXPLAIN SELECT * FROM emp WHERE deptno = 3;
-- 预期：type=ALL, key=NULL

-- 2. 建索引后：ref
CREATE INDEX idx_deptno ON emp(deptno);
EXPLAIN SELECT * FROM emp WHERE deptno = 3;
-- 预期：type=ref, key=idx_deptno

-- 3. 主键等值：const
EXPLAIN SELECT * FROM emp WHERE id = 100;
-- 预期：type=const, key=PRIMARY

-- 4. JOIN 用主键关联：eq_ref
EXPLAIN SELECT e.ename, d.dname FROM emp e JOIN dept d ON e.deptno = d.deptno WHERE e.id = 100;
-- 预期：dept 是 eq_ref（deptno 是主键）

-- 5. 范围查询：range
EXPLAIN SELECT * FROM emp WHERE deptno > 5 AND deptno < 9;
-- 预期：type=range

-- 6. key_len 判断联合索引命中列数
CREATE INDEX idx_dept_sal ON emp(deptno, salary);
EXPLAIN SELECT * FROM emp WHERE deptno = 3 AND salary > 5000;
-- key_len=4+4+1（deptno 可空加1）说明两列都命中
EXPLAIN SELECT * FROM emp WHERE deptno = 3;
-- key_len 变小，只用了 deptno 一列

-- 7. Extra：Using index 覆盖索引
EXPLAIN SELECT deptno, salary FROM emp WHERE deptno = 3 AND salary > 5000;
-- 查询列全在 idx_dept_sal 里，不回表

-- 8. Extra：Using filesort 排序没走索引
EXPLAIN SELECT * FROM emp WHERE deptno = 3 ORDER BY hiredate;
-- 排序列 hiredate 不在索引里

-- 9. 排序列加进联合索引后 filesort 消失
CREATE INDEX idx_dept_date ON emp(deptno, hiredate);
EXPLAIN SELECT * FROM emp WHERE deptno = 3 ORDER BY hiredate;
-- 预期：type=ref，filesort 消失

-- 10. GROUP BY 不走索引出现 Using temporary，走了就没有
EXPLAIN SELECT deptno, COUNT(*) FROM emp GROUP BY deptno;
-- idx_dept_sal 最左列是 deptno，GROUP BY 能沿索引分组

-- 11. rows 估算与统计信息
ANALYZE TABLE emp;
EXPLAIN SELECT * FROM emp WHERE deptno = 1;

-- 12. 子查询的 id 不同，id 大的先执行
EXPLAIN SELECT * FROM emp WHERE deptno = (SELECT deptno FROM dept WHERE dname = '研发');

-- 清理
DROP INDEX idx_deptno ON emp;
DROP INDEX idx_dept_sal ON emp;
DROP INDEX idx_dept_date ON emp;
