USE dbtest26;

CREATE TABLE IF NOT EXISTS emp(
id INT PRIMARY KEY AUTO_INCREMENT,
ename VARCHAR(15),
salary DOUBLE(10,2),
dept_id INT
);

INSERT INTO emp(ename,salary,dept_id) VALUES('张三',5000,10);
INSERT INTO emp(ename,salary,dept_id) VALUES('李四',8000,10);
INSERT INTO emp(ename,salary,dept_id) VALUES('王五',8000,10);
INSERT INTO emp(ename,salary,dept_id) VALUES('赵六',12000,20);
INSERT INTO emp(ename,salary,dept_id) VALUES('钱七',6000,20);
INSERT INTO emp(ename,salary,dept_id) VALUES('孙八',9000,30);

-- 窗口函数：分组统计但不合并行，人还在，多一列组内平均
SELECT ename,salary,dept_id,
AVG(salary) OVER(PARTITION BY dept_id) AS 部门平均
FROM emp;

-- 排名函数对比：并列时ROW_NUMBER强行分序号，RANK跳号，DENSE_RANK不跳号
-- 10部门两个8000并列，看三列的差别
SELECT ename,salary,
ROW_NUMBER() OVER(ORDER BY salary DESC) AS 序号,
RANK() OVER(ORDER BY salary DESC) AS 跳号排名,
DENSE_RANK() OVER(ORDER BY salary DESC) AS 紧凑排名
FROM emp;

-- 每个部门内排工资名次：PARTITION和ORDER可以同时用
SELECT ename,dept_id,salary,
RANK() OVER(PARTITION BY dept_id ORDER BY salary DESC) AS 组内名次
FROM emp;

-- 窗口函数只能出现在SELECT列表和ORDER BY里，不能进WHERE
-- 想按"组内名次"过滤，套一层：
SELECT * FROM(
SELECT ename,dept_id,salary,
RANK() OVER(PARTITION BY dept_id ORDER BY salary DESC) AS 组内名次
FROM emp
) t
WHERE 组内名次=1;

-- 累计和：OVER里只ORDER BY，从第一行累到当前行
SELECT ename,salary,
SUM(salary) OVER(ORDER BY id) AS 累计工资
FROM emp;

-- CTE：子查询起个名字，从上往下读
WITH 高薪 AS(
SELECT * FROM emp WHERE salary>8000
)
SELECT ename,dept_id,salary FROM 高薪 WHERE dept_id=10;

-- 多个CTE用逗号并列，后面的能引用前面的
WITH 高薪 AS(
SELECT * FROM emp WHERE salary>8000
),
部门统计 AS(
SELECT dept_id,COUNT(*) AS 人数 FROM 高薪 GROUP BY dept_id
)
SELECT * FROM 部门统计 WHERE 人数>=1;

-- 递归CTE：锚点+UNION ALL+递归部分，终止条件写在WHERE
WITH RECURSIVE 数字 AS(
SELECT 1 AS n
UNION ALL
SELECT n+1 FROM 数字 WHERE n<10
)
SELECT GROUP_CONCAT(n ORDER BY n) AS 一到十 FROM 数字;

-- 递归CTE查层级：员工表自引用，从老板往下捋
CREATE TABLE IF NOT EXISTS 员工(
id INT PRIMARY KEY,
NAME VARCHAR(15),
boss_id INT
);
INSERT INTO 员工 VALUES(1,'大老板',NULL),(2,'经理A',1),(3,'经理B',1),(4,'小张',2),(5,'小李',3);

WITH RECURSIVE 层级 AS(
SELECT id,NAME,0 AS 层 FROM 员工 WHERE boss_id IS NULL	-- 锚点：没有老板的那个人
UNION ALL
SELECT e.id,e.NAME,层级.层+1 FROM 员工 e JOIN 层级 ON e.boss_id=层级.id
)
SELECT id,NAME,层 FROM 层级;
