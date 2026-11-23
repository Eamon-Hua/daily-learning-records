USE dbtest27;

-- 建表时顺手建索引
CREATE TABLE IF NOT EXISTS emp(
id INT PRIMARY KEY AUTO_INCREMENT,	-- 主键索引，聚簇索引，叶子存整行
ename VARCHAR(15) NOT NULL,
phone CHAR(11) UNIQUE,	-- 唯一索引：值不许重复，NULL可以有多个
salary DOUBLE(10,2),
intro TEXT,
INDEX idx_salary(salary)	-- 普通索引，纯加速
);

INSERT INTO emp(ename,phone,salary,intro) VALUES('张三','13800000001',5000,'熟悉Java与MySQL');
INSERT INTO emp(ename,phone,salary,intro) VALUES('李四','13800000002',8000,'专注后端开发');
INSERT INTO emp(ename,salary,intro) VALUES('王五',9000,'熟悉Java');	-- phone没给，NULL不占唯一名额

-- 唯一索引的约束效果：插重复手机号会报Duplicate entry，这行故意留着注释
-- INSERT INTO emp(ename,phone,salary) VALUES('冒牌张三','13800000001',6000);

-- 建好表之后补索引的两种写法
CREATE INDEX idx_ename ON emp(ename);
ALTER TABLE emp ADD INDEX idx_salary_ename(salary,ename);	-- 联合索引，列顺序有意义

-- 全文索引：文本关键词搜索，中文要配ngram解析器
CREATE FULLTEXT INDEX idx_intro ON emp(intro) WITH PARSER ngram;
-- 查法：MATCH(列) AGAINST('关键词')，命中含该词的行
-- 注意：小表上刚建好的全文索引经常搜不出结果（缓存没同步完），多插几行真实数据再试
SELECT * FROM emp WHERE MATCH(intro) AGAINST('Java');
INSERT INTO emp(ename,phone,salary,intro) VALUES('孙九','13800000004',7000,'Java爱好者');
SELECT * FROM emp WHERE MATCH(intro) AGAINST('Java');

-- 查看一张表的所有索引：Key_name看名字，Column_name看列
SHOW INDEX FROM emp;

-- 联合索引与最左前缀：索引(salary,ename)按salary排好，salary相同再按ename排
-- 用得上（最左列在）：走索引
SELECT * FROM emp WHERE salary=8000;
SELECT * FROM emp WHERE salary=8000 AND ename='李四';
-- 这两条查出来结果一样，看不了区别，实际用EXPLAIN验证（下一篇细说）
SELECT * FROM emp WHERE ename='李四';	-- 最左列不在，用不上联合索引，全表扫

-- InnoDB的二级索引叶子存的是索引列+主键，查ename时主键顺手带回，不用回表
-- 这就是"能查少列就少列"的原因之一
SELECT id,ename FROM emp WHERE ename='李四';

DROP INDEX idx_intro ON emp;	-- 全文索引演示完就撤，保持表干净
DROP INDEX idx_salary_ename ON emp;
SHOW INDEX FROM emp;
