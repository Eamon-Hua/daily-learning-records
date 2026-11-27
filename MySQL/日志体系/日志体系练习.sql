-- 日志体系练习：redo/undo/binlog 的状态查看与 binlog 事件读取
-- dbtest33
USE dbtest33;

CREATE TABLE emp(
	id INT PRIMARY KEY AUTO_INCREMENT,
	ename VARCHAR(15) NOT NULL,
	salary DOUBLE(10,2)
);
INSERT INTO emp(ename,salary) VALUES('张三',5000),('李四',8000);

-- 1. binlog 开关与格式（8.0 默认 ON / ROW）
SHOW VARIABLES LIKE 'log_bin';
SHOW VARIABLES LIKE 'binlog_format';
SHOW VARIABLES LIKE 'binlog_expire_logs_seconds';

-- 2. redo 配置
SHOW VARIABLES LIKE 'innodb_redo_log_capacity';
SHOW VARIABLES LIKE 'innodb_flush_log_at_trx_commit';

-- 3. undo 配置
SHOW VARIABLES LIKE 'innodb_undo%';

-- 4. binlog 文件列表
SHOW BINARY LOGS;

-- 5. 产生一些变更再看 binlog 事件
UPDATE emp SET salary = 5200 WHERE id = 1;
INSERT INTO emp(ename,salary) VALUES('王五',6000);
DELETE FROM emp WHERE id = 2;
SHOW BINARY LOGS;
-- 取当前 binlog 文件里的事件（不带 IN 默认显示当前文件；Query/Write_rows/Delete_rows 等）
SHOW BINLOG EVENTS;

-- 6. ROW 格式下看到的是行级变更事件；切 STATEMENT 看到的是 SQL 原文（演示用，生产用 ROW）
SET SESSION sql_log_bin = 0;	-- 本会话不写 binlog（需SUPER/权限，失败则跳过）
SET SESSION sql_log_bin = 1;

-- 7. purge：清理已过期的 binlog（演示语法，不真删保留期内的）
SHOW VARIABLES LIKE 'binlog_expire_logs_seconds';
-- PURGE BINARY LOGS BEFORE NOW() - INTERVAL 7 DAY;

-- 8. 两阶段提交相关状态
SHOW STATUS LIKE 'Innodb_redo_log_%';

-- 9. 事务层观察：当前活跃事务
SELECT trx_id, trx_state, trx_started FROM information_schema.innodb_trx;
