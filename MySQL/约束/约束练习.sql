-- 约束的练习，2026.11.16
-- 建的是 dbtest17，整份跑没冲突

DROP DATABASE IF EXISTS dbtest17;

CREATE DATABASE dbtest17 CHARACTER SET utf8mb4;

USE dbtest17;

-- 主键+自增、非空、唯一，一次建全
CREATE TABLE t1 (
id INT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(15) NOT NULL,
phone VARCHAR(20) UNIQUE
);

INSERT INTO t1(name,phone) VALUES('张三','110');
INSERT INTO t1(name,phone) VALUES('李四',NULL);    -- unique 允许多个 NULL，有点反直觉

-- 唯一约束生效：phone 重复会报 Duplicate entry，这行故意留着注释
-- INSERT INTO t1(name,phone) VALUES('王五','110');

SELECT * FROM t1;

-- 检查约束+默认值
CREATE TABLE t2 (
age INT CHECK(age>0 AND age<150),   -- 8.0.16 之前 CHECK 不生效
gender CHAR(1) DEFAULT '男'
);

INSERT INTO t2(age) VALUES(20);    -- gender 没填，用默认的'男'

SELECT * FROM t2;

-- CHECK 生效：age=200 违反约束报 3819，这行故意留着注释
-- INSERT INTO t2(age) VALUES(200);

-- 外键：从表的 deptno 必须在主表里存在
CREATE TABLE dept (
deptno INT PRIMARY KEY,
dname VARCHAR(15)
);

INSERT INTO dept VALUES(10,'研发部'),(20,'市场部');

CREATE TABLE emp (
empno INT PRIMARY KEY AUTO_INCREMENT,
ename VARCHAR(15),
deptno INT,
CONSTRAINT fk_emp_dept FOREIGN KEY(deptno) REFERENCES dept(deptno)
);

INSERT INTO emp(ename,deptno) VALUES('张三',10),('李四',20);

-- 外键生效：主表没有 30，插入报 1452，这行故意留着注释
-- INSERT INTO emp(ename,deptno) VALUES('王五',30);

-- 外键限制删除：emp 里还有人挂在部门 10，删除报 1452，这行故意留着注释
-- DELETE FROM dept WHERE deptno=10;

-- 级联删除：删部门连带删员工
CREATE TABLE emp2 (
empno INT PRIMARY KEY AUTO_INCREMENT,
ename VARCHAR(15),
deptno INT,
CONSTRAINT fk_emp2_dept FOREIGN KEY(deptno) REFERENCES dept(deptno) ON DELETE CASCADE
);

INSERT INTO dept VALUES(30,'测试部');
INSERT INTO emp2(ename,deptno) VALUES('张三',30);

DELETE FROM dept WHERE deptno=30;

SELECT * FROM emp2;   -- 空了，跟着删了

-- 事后补约束、删约束都用 ALTER
CREATE TABLE t3 (
id INT,
email VARCHAR(30)
);

ALTER TABLE t3 MODIFY id INT PRIMARY KEY;

ALTER TABLE t3 ADD CONSTRAINT uk_email UNIQUE(email);

SHOW INDEX FROM t3;

ALTER TABLE t3 DROP INDEX uk_email;

ALTER TABLE t3 DROP PRIMARY KEY;
