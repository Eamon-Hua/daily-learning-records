# MySQL8 新特性：窗口函数与 CTE

记于 2026.11.20

窗口函数和 CTE 是 MySQL 8.0 新增的，8.0 以前的版本不支持。

## 窗口函数：分组统计但不合并行

老 GROUP BY 的问题是**聚合后行数合并**：按部门算平均工资，就看不到每个人了。想"既显示每个人，又带上他所在组的统计值"，旧写法要 JOIN 子查询，窗口函数直接多一列：

```sql
SELECT ename, salary, dept_id,
       AVG(salary) OVER (PARTITION BY dept_id) AS 部门平均
FROM emp;
```

`OVER` 里的 PARTITION BY 分组但不合并行，每一行都保留，只是多了一列组内统计值。同样可用的还有 SUM、COUNT、MAX、MIN。

### 排名函数

```sql
ROW_NUMBER() OVER (ORDER BY salary DESC) AS 排名,
RANK()      OVER (ORDER BY salary DESC)  AS 并列排名,
DENSE_RANK() OVER (ORDER BY salary DESC) AS 紧凑排名
```

工资并列时三者的差别：ROW_NUMBER 强行分 1、2、3；RANK 并列同名次、跳号（1、1、3）；DENSE_RANK 并列同名次、不跳号（1、1、2）。**问"第几名"用 RANK，要序号用 ROW_NUMBER**。

### 移动统计

`SUM(salary) OVER (ORDER BY id)` 是累计和；`LAG(列, n)` 取前面第 n 行的值、`LEAD` 取后面，环比上一行特别好用：

```sql
SELECT 月份, 销量, LAG(销量,1) OVER (ORDER BY 月份) AS 上月,
       销量 - LAG(销量,1) OVER (ORDER BY 月份) AS 环比
FROM sales;
```

## CTE：WITH 把子查询提个名字

复杂查询套多层子查询可读性差。CTE（公用表表达式）给子查询起个名字，先声明后使用，从上往下读：

```sql
WITH 高薪 AS (
    SELECT * FROM emp WHERE salary > 8000
)
SELECT ename, dept_id FROM 高薪 WHERE dept_id = 20;
```

多个 CTE 用逗号并列，后面的可以引用前面的，逐层组合：

```sql
WITH 高薪 AS (SELECT * FROM emp WHERE salary > 8000),
     部门统计 AS (SELECT dept_id, COUNT(*) c FROM 高薪 GROUP BY dept_id)
SELECT * FROM 部门统计 WHERE c > 2;
```

## 递归 CTE

WITH RECURSIVE 让查询自己调用自己，实现以前要存储过程才能做的活——生成序列、查树形层级：

```sql
WITH RECURSIVE 数字 AS (
    SELECT 1 AS n              -- 起点
    UNION ALL
    SELECT n + 1 FROM 数字 WHERE n < 10   -- 终止条件写在WHERE里
)
SELECT * FROM 数字;            -- 1到10
```

结构固定：**锚点（起点）+ UNION ALL + 递归部分（引用自己 + 终止条件）**。没有终止条件就是无限循环，跑之前先想清楚什么时候停。

老写法对照：递归 CTE 之前查组织架构树要写函数循环，8.0 以后一条 SQL 出整个树。
