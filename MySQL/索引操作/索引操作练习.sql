USE dbtest28;

CREATE TABLE IF NOT EXISTS emp(
id INT PRIMARY KEY AUTO_INCREMENT,
ename VARCHAR(15) NOT NULL,
phone CHAR(11),
salary DOUBLE(10,2),
created DATETIME,
INDEX idx_ename(ename),
INDEX idx_salary(salary),
INDEX idx_phone_salary(phone,salary)	-- 联合索引
);

INSERT INTO emp(ename,phone,salary,created) VALUES('张三','13800000001',5000,'2026-01-10 09:00:00');
INSERT INTO emp(ename,phone,salary,created) VALUES('李四','13800000002',8000,'2026-03-15 10:00:00');
INSERT INTO emp(ename,phone,salary,created) VALUES('王五','13800000003',12000,'2026-06-20 11:00:00');
INSERT INTO emp(ename,phone,salary,created) VALUES('赵六',NULL,6000,'2026-09-01 12:00:00');

-- 三板斧
SHOW INDEX FROM emp;
CREATE INDEX idx_created ON emp(created);
ALTER TABLE emp ADD INDEX idx_salary_ename(salary,ename);
DROP INDEX idx_created ON emp;
SHOW INDEX FROM emp;

-- 基准：右模糊走索引，key=idx_ename，type=range
EXPLAIN SELECT * FROM emp WHERE ename LIKE '张%';

-- 失效1：左模糊，key=NULL，type=ALL全表扫
EXPLAIN SELECT * FROM emp WHERE ename LIKE '%三';

-- 失效2：列上套函数，key=NULL
EXPLAIN SELECT * FROM emp WHERE YEAR(created)='2026';
-- 改写：函数挪到常量那边，能走索引
CREATE INDEX idx_created ON emp(created);
EXPLAIN SELECT * FROM emp WHERE created>='2026-01-01' AND created<'2027-01-01';

-- 失效3：列上做运算，key=NULL
EXPLAIN SELECT * FROM emp WHERE salary+1000=6000;
EXPLAIN SELECT * FROM emp WHERE salary=5000;	-- 运算挪到常量侧，走索引

-- 失效4：类型转换，phone是CHAR，不帶引号被偷偷转数字，key=NULL
EXPLAIN SELECT * FROM emp WHERE phone=13800000001;
EXPLAIN SELECT * FROM emp WHERE phone='13800000001';	-- 带引号，走联合索引最左列

-- 失效5：联合索引(salary,ename)只查ename，最左前缀不满足，key=NULL
EXPLAIN SELECT * FROM emp WHERE ename='李四';
EXPLAIN SELECT * FROM emp WHERE salary=8000 AND ename='李四';	-- 两列连续命中，走idx_salary_ename

-- 失效6：OR一边没索引，全表扫
EXPLAIN SELECT * FROM emp WHERE ename='张三' OR salary=8000;

-- 不等于：范围太大优化器算下来不如全扫，通常key=NULL
EXPLAIN SELECT * FROM emp WHERE salary<>8000;

DROP INDEX idx_salary_ename ON emp;
DROP INDEX idx_created ON emp;
DROP INDEX idx_ename ON emp;
DROP INDEX idx_salary ON emp;
DROP INDEX idx_phone_salary ON emp;
