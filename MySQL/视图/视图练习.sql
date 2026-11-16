-- 视图的练习，2026.11.16
-- 建的是 dbtest18，整份跑没冲突

DROP DATABASE IF EXISTS dbtest18;

CREATE DATABASE dbtest18 CHARACTER SET utf8mb4;

USE dbtest18;

CREATE TABLE emp (
empno INT PRIMARY KEY,
ename VARCHAR(15),
salary DOUBLE,
deptno INT
);

INSERT INTO emp VALUES(1,'张三',8000,10),(2,'李四',5000,10),(3,'王五',12000,20);

CREATE TABLE dept (
deptno INT PRIMARY KEY,
dname VARCHAR(15)
);

INSERT INTO dept VALUES(10,'研发部'),(20,'市场部');

-- 创建视图，就是一段查询存起来当表用
CREATE VIEW v1 AS
SELECT empno,ename,deptno FROM emp;

SELECT * FROM v1;

-- 多表视图，复杂的关联查询存一份
CREATE VIEW v2 AS
SELECT e.ename,e.salary,d.dname
FROM emp e JOIN dept d ON e.deptno=d.deptno;

SELECT * FROM v2;

-- 查看：DESC 看结构，SHOW CREATE VIEW 看定义
DESC v1;

SHOW CREATE VIEW v1;

-- 改视图两种写法，效果一样
CREATE OR REPLACE VIEW v1 AS
SELECT empno,ename,salary FROM emp;

ALTER VIEW v1 AS
SELECT empno,ename FROM emp;

-- 视图也能增删改，动的是背后的表
UPDATE v1 SET ename='赵六' WHERE empno=1;

SELECT * FROM emp;   -- 原表跟着变了

INSERT INTO v1(empno,ename) VALUES(4,'孙七');

SELECT * FROM emp;

DELETE FROM v1 WHERE empno=4;

-- 带聚合、GROUP BY 的视图只读，改不了
CREATE VIEW v3 AS
SELECT deptno,AVG(salary) avg_sal FROM emp GROUP BY deptno;

SELECT * FROM v3;

-- UPDATE v3 SET avg_sal=1 WHERE deptno=10;   -- 报错，视图的行找不到原表对应的行

-- WITH CHECK OPTION：插的数据必须还能从视图查出来
CREATE VIEW v4 AS
SELECT empno,ename,deptno FROM emp WHERE deptno=10
WITH CHECK OPTION;

INSERT INTO v4(empno,ename,deptno) VALUES(5,'周八',10);   -- OK

INSERT INTO v4(empno,ename,deptno) VALUES(6,'吴九',20);   -- 报错，20 不满足条件

-- 删视图不影响原表
DROP VIEW v4;

SELECT * FROM emp;
