# MySQL SQL 优化

记于 2026.11.24

优化围绕两个目标：**少扫行**（走索引、只取需要的列）和**少做额外工作**（消 filesort、消临时表、消回表）。手段都以 EXPLAIN 结果验证。

## 覆盖索引：不回表

回表：二级索引里只存索引列和主键，`SELECT *` 拿到主键还得回聚簇索引再查一遍整行。如果**查询的列全在索引里**，直接从索引返回，这就是覆盖索引，EXPLAIN 的 Extra 显示 **Using index**：

```sql
-- idx_dept_sal(deptno, salary) 已存在
EXPLAIN SELECT deptno, salary FROM emp WHERE deptno = 3 AND salary > 5000;
-- Extra: Using index
```

两个推论：

- **别写 SELECT ***，按需列名。列一多覆盖索引就失效，回表次数等于行数
- 联合索引覆盖面比单列索引大：idx(a,b) 能覆盖 SELECT a,b，也能覆盖 WHERE a=? 的 b 取值

## 前缀索引：长字符串省钱

EMAIL、URL 这类长字符串整列建索引占空间大、比较慢。只对前 n 个字符建索引：

```sql
CREATE INDEX idx_email_pre ON emp(email(10));
```

n 怎么选——**选择性**要接近整列：`COUNT(DISTINCT 前缀) / COUNT(*)`。逐个试：

```sql
SELECT COUNT(DISTINCT LEFT(email,10)) / COUNT(*) FROM emp;   -- 例如 0.92
SELECT COUNT(DISTINCT email) / COUNT(*) FROM emp;            -- 例如 0.94
```

选择性差距不大就选小的。两个限制：**前缀索引不能做覆盖索引**（索引里只有前缀，拿不到完整值，必须回表），也不能用于 ORDER BY。

## 深分页：LIMIT 1000000,10 的优化

`LIMIT 900000, 10` 要扫过前 90 万行再丢掉，页码越深越慢。改成先用索引定位再关联：

```sql
-- 慢：扫90万+10
SELECT * FROM emp ORDER BY id LIMIT 900000, 10;

-- 快：子查询只扫索引拿主键，回表10次
SELECT * FROM emp
JOIN (SELECT id FROM emp ORDER BY id LIMIT 900000, 10) t ON emp.id = t.id;
```

子查询走覆盖索引（只有主键），外层只对 10 行回表。EXPLAIN 对比：子查询那段 type=index、rows 大但 Extra 是 Using index，外层 eq_ref/const 回表 10 行。

## JOIN 优化：小表驱动大表

JOIN 的原理是外层每行去内层查一次，所以**内层必须有索引，外层行数尽量少**——小表当驱动表。MySQL 优化器一般自己会选，`STRAIGHT_JOIN` 可以手动指定驱动顺序。JOIN 键的类型和编码两边要对齐，不然用不上索引。

## 排序与分组：沿索引读，消 filesort

ORDER BY / GROUP BY 慢的根源都是没走索引，排序在内存/临时文件里做（Using filesort / Using temporary）。把排序列组合进联合索引，且**等值条件列放最左**：

```sql
CREATE INDEX idx_dept_sal ON emp(deptno, salary);
-- WHERE deptno=? ORDER BY salary：先等值锁定再沿索引读，天然有序
SELECT * FROM emp WHERE deptno = 3 ORDER BY salary;
```

规则：ORDER BY 多列方向一致（都升或都降）才走得通；等值列放最左后，其余列天然有序；ORDER BY 里套函数同样失效。

## count 的真相

`count(*)`、`count(1)`、`count(id)` 官方口径性能无实质差别，`count(*)` 是标准写法。`count(列)` 统计的是**该列不为 NULL 的行数**，语义不同，别混用。

## 失效写法复习

六种索引失效场景（左模糊、列上运算、类型转换、最左前缀断裂、OR 半边无索引、NOT IN）在优化语境下同样适用，写完 SQL 用 EXPLAIN 确认。
