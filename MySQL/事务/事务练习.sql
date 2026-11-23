USE dbtest25;

CREATE TABLE IF NOT EXISTS account(
id INT PRIMARY KEY AUTO_INCREMENT,
NAME VARCHAR(15),
money DOUBLE(10,2)
);

INSERT INTO account(NAME,money) VALUES('张三',5000);
INSERT INTO account(NAME,money) VALUES('李四',100);

-- 看当前隔离级别（InnoDB默认REPEATABLE READ）
SELECT @@transaction_isolation;

-- 正常事务：转账要么全成要么全不算
START TRANSACTION;
UPDATE account SET money=money-500 WHERE NAME='张三';
UPDATE account SET money=money+500 WHERE NAME='李四';
COMMIT;

SELECT * FROM account;

-- 回滚：钱扣了但转账失败，ROLLBACK全退
START TRANSACTION;
UPDATE account SET money=money-500 WHERE NAME='张三';
SELECT * FROM account;	-- 事务内能看到未提交的改动
ROLLBACK;
SELECT * FROM account;	-- 外面看，一分没少

-- 隐式提交的坑：事务中间跑DDL，前面全被自动提交
START TRANSACTION;
UPDATE account SET money=money-100 WHERE NAME='张三';
CREATE TABLE tmp_测试(id INT);	-- DDL！前面的UPDATE自动提交了
ROLLBACK;
SELECT * FROM account;	-- 那笔-100回不去了
DROP TABLE tmp_测试;

-- SAVEPOINT：大事务里的保存点，出事只退一段
START TRANSACTION;
UPDATE account SET money=money-100 WHERE NAME='张三';
SAVEPOINT 保存点1;
UPDATE account SET money=money-100 WHERE NAME='张三';	-- 这步假装出错了
ROLLBACK TO 保存点1;	-- 只退到保存点，第一个UPDATE保留
COMMIT;
SELECT * FROM account;

-- 换隔离级别（本会话）
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
SELECT @@transaction_isolation;
-- 并发问题（脏读/不可重复读/幻读）要开两个连接对比演示：
-- 会话A开事务后读，会话B改数据并COMMIT，会话A再读看差异
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;
