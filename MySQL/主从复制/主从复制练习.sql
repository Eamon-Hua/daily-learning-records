-- 主从复制练习：配置项与状态查询（单实例环境，CHANGE/START 语句注释给出）
-- dbtest34
USE dbtest34;

CREATE TABLE emp(
	id INT PRIMARY KEY AUTO_INCREMENT,
	ename VARCHAR(15) NOT NULL,
	salary DOUBLE(10,2)
);
INSERT INTO emp(ename,salary) VALUES('张三',5000),('李四',8000);

-- 1. 复制相关的核心配置项
SHOW VARIABLES LIKE 'server_id';
SHOW VARIABLES LIKE 'log_bin';
SHOW VARIABLES LIKE 'binlog_format';
SHOW VARIABLES LIKE 'relay_log';
SHOW VARIABLES LIKE 'read_only';
SHOW VARIABLES LIKE 'super_read_only';

-- 2. 复制账号需要的权限
-- CREATE USER 'repl'@'%' IDENTIFIED BY '复制密码';
-- GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';

-- 3. 拿主库同步位点（开启复制前执行，记录 File 和 Position）
SHOW MASTER STATUS;

-- 4. 从库开启复制的语句（需要真实主库，注释形式）
-- CHANGE REPLICATION SOURCE TO
--     SOURCE_HOST = '主库IP',
--     SOURCE_PORT = 3306,
--     SOURCE_USER = 'repl',
--     SOURCE_PASSWORD = '复制密码',
--     SOURCE_LOG_FILE = 'mysql-bin.000003',
--     SOURCE_LOG_POS = 157;
-- START REPLICA;

-- GTID 方式：CHANGE REPLICATION SOURCE TO ... SOURCE_AUTO_POSITION = 1;

-- 5. 复制状态（未配置复制时结果为空集，配置后重点看
--    Replica_IO_Running / Replica_SQL_Running / Seconds_Behind_Source / Last_*_Error）
SHOW REPLICA STATUS;
SHOW REPLICAS;	-- 当前实例作为主库时，显示已连接的从库

-- 6. 并行复制参数（8.0.27+）
SHOW VARIABLES LIKE 'replica_parallel_workers';
SHOW VARIABLES LIKE 'replica_parallel_type';

-- 7. 半同步复制插件（按需安装，注释给出）
-- INSTALL PLUGIN rpl_semi_sync_master SONAME 'semisync_master.so';
-- SET GLOBAL rpl_semi_sync_master_enabled = 1;
SHOW PLUGINS;

-- 8. 数据同步验证方法：主从两边分别比对校验值
CHECKSUM TABLE emp;
SELECT COUNT(*) AS 行数 FROM emp;
