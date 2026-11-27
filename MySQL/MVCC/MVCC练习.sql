-- MVCC 练习：版本字段、隔离级别与 ReadView 时机、快照读/当前读
-- ReadView 的可见性对比需要两个会话交错执行，本脚本单连接演示语法与可观察状态
-- dbtest32
USE dbtest32;

CREATE TABLE emp(
	id INT PRIMARY KEY AUTO_INCREMENT,
	ename VARCHAR(15) NOT NULL,
	salary DOUBLE(10,2)
);
INSERT INTO emp(ename,salary) VALUES('张三',5000),('李四',8000);

-- 1. 隐藏列：information_schema 里看不到，但 ibd2sdi 或 SELECT 可确认数据在
-- 版本链藏在 InnoDB 存储层，SQL 层用以下方式间接观察
SELECT id, ename, salary FROM emp;

-- 2. 当前隔离级别（InnoDB 默认 REPEATABLE READ，MVCC 在 RC/RR 下生效）
SELECT @@transaction_isolation;
SELECT @@global.transaction_isolation;

-- 3. RR 下的快照读一致性：单事务内重复读结果一致
START TRANSACTION;
SELECT salary FROM emp WHERE ename = '张三';	-- 第一次快照读，生成 ReadView
-- （如果是双会话：此时另一会话 UPDATE 并提交，这里再读仍是旧值）
SELECT salary FROM emp WHERE ename = '张三';	-- 复用 ReadView，结果一致
COMMIT;

-- 4. RC 下的行为差异：每次快照读生成新 ReadView
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;
SELECT salary FROM emp WHERE ename = '张三';
-- （双会话下另一会话提交新值后，这里的下一次 SELECT 能看到新值）
COMMIT;
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;	-- 改回默认

-- 5. 当前读：读最新提交版本并加锁
START TRANSACTION;
SELECT * FROM emp WHERE id = 1 FOR UPDATE;	-- 当前读，X 锁
UPDATE emp SET salary = 5200 WHERE id = 1;	-- UPDATE 本身也是当前读
SELECT salary FROM emp WHERE id = 1;		-- 快照读（自己改的版本，自己可见）
COMMIT;

-- 6. 混用的表现：快照读旧值，当前读新值（RR 下）
START TRANSACTION;
SELECT salary FROM emp WHERE id = 1;		-- 快照读：5200（本事务建立视图时的版本）
UPDATE emp SET salary = 5400 WHERE id = 1;	-- 当前读，改的是最新版本
SELECT salary FROM emp WHERE id = 1;		-- 快照读：自己改过的版本自己可见，5400
COMMIT;

-- 7. 活跃事务查看（ReadView 的活跃列表来源）
SELECT trx_id, trx_state, trx_isolation_level, trx_started
FROM information_schema.innodb_trx;

-- 8. undo 相关参数
SHOW VARIABLES LIKE 'innodb_undo%';
SHOW VARIABLES LIKE 'innodb_max_undo_log_size';
