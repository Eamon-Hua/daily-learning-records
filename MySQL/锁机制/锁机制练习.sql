-- 锁机制练习：全局锁语法、表锁、行锁、data_locks 查看
-- 并发的锁等待需要两个会话，本脚本单连接演示语法与锁记录查看
-- dbtest31
USE dbtest31;

CREATE TABLE emp(
	id INT PRIMARY KEY AUTO_INCREMENT,
	ename VARCHAR(15) NOT NULL,
	salary DOUBLE(10,2),
	version INT DEFAULT 0
);
INSERT INTO emp(ename,salary,version) VALUES
	('张三',5000,1),('李四',8000,1),('王五',6000,1);

-- 1. 全局锁：加锁后全库只读（会话内立即 UNLOCK，否则后续语句全被阻塞）
FLUSH TABLES WITH READ LOCK;
UNLOCK TABLES;

-- 2. 表级锁：READ 锁下自己只能读
LOCK TABLES emp READ;
SELECT * FROM emp;
UNLOCK TABLES;

-- 3. 行级锁：排他锁，锁 id=1 这一行（索引等值，Record Lock）
START TRANSACTION;
SELECT * FROM emp WHERE id = 1 FOR UPDATE;
-- 此时本会话持有 id=1 的 X 记录锁，查看锁记录：
SELECT index_name, lock_type, lock_mode, lock_data
FROM performance_schema.data_locks
WHERE object_name = 'emp';
COMMIT;

-- 4. 共享锁：S 锁
START TRANSACTION;
SELECT * FROM emp WHERE id = 2 FOR SHARE;
SELECT index_name, lock_type, lock_mode, lock_data
FROM performance_schema.data_locks
WHERE object_name = 'emp' AND lock_type = 'RECORD';
COMMIT;

-- 5. 范围查询的 Next-Key Lock（记录+间隙）
START TRANSACTION;
SELECT * FROM emp WHERE id BETWEEN 1 AND 2 FOR UPDATE;
SELECT index_name, lock_mode, lock_data
FROM performance_schema.data_locks
WHERE object_name = 'emp' AND lock_type = 'RECORD';
COMMIT;

-- 6. 无索引条件：行锁波及全表（surrogate：ename 无索引）
START TRANSACTION;
SELECT * FROM emp WHERE ename = '张三' FOR UPDATE;
SELECT COUNT(*) AS 加锁记录数
FROM performance_schema.data_locks
WHERE object_name = 'emp' AND lock_type = 'RECORD';
COMMIT;

-- 7. 悲观锁扣减流程（单事务内演示语法）
START TRANSACTION;
SELECT salary FROM emp WHERE id = 1 FOR UPDATE;
UPDATE emp SET salary = salary - 100 WHERE id = 1;
COMMIT;

-- 8. 乐观锁：版本号校验
UPDATE emp SET salary = 5500, version = version + 1
WHERE id = 1 AND version = 1;
SELECT ROW_COUNT() AS 影响行数;	-- 1 表示成功
-- 再用旧版本号更新，影响行数 0，需要读新数据重试
UPDATE emp SET salary = 5800, version = version + 1
WHERE id = 1 AND version = 1;
SELECT ROW_COUNT() AS 影响行数;

-- 9. 锁等待超时参数
SHOW VARIABLES LIKE 'innodb_lock_wait_timeout';
