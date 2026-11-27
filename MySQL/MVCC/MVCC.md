# MySQL MVCC 多版本并发控制

记于 2026.11.25

MVCC（多版本并发控制）让读不阻塞写、写不阻塞读：每行数据保留多个历史版本，不同事务根据自己的"时间点"决定看到哪个版本。InnoDB 在 READ COMMITTED 和 REPEATABLE READ 两个隔离级别下靠它实现快照读。

## 版本字段

每行记录背后有三个隐藏列：

- **DB_TRX_ID**（6 字节）：最后修改这行的事务 ID
- **DB_ROLL_PTR**（7 字节）：回滚指针，指向 undo log 里的上一个版本
- **DB_ROW_ID**（6 字节）：表没有主键时的隐藏行号

修改一行时，旧版本写入 undo log，新版本在原位置，回滚指针串起来形成**版本链**：最新 → 旧一版 → 更旧，一直到这条数据的初始版本。

## ReadView：一致性视图

事务做快照读时生成 ReadView，记录生成时刻"活跃（未提交）的事务列表"。拿到版本链后从最新版本开始，按规则判断每个版本的事务 ID 是否可见：

- 版本的 trx_id == 自己的事务 ID → 自己改的，可见
- trx_id < ReadView 中最小的活跃事务 ID → 在视图创建前已提交，可见
- trx_id > ReadView 中最大的事务 ID → 视图创建后才开始的事务，不可见
- 介于两者之间：在活跃列表里 → 未提交，不可见；不在 → 已提交，可见

不可见就顺着回滚指针找上一个版本，直到找到可见的为止。

## 两种隔离级别的差别

差别只在 ReadView 的生成时机：

- **READ COMMITTED**：**每次快照读都生成新的 ReadView**——别的事务提交后，下一次读就能看到，所以叫"读已提交"
- **REPEATABLE READ**：**事务内第一次快照读时生成，之后复用**——事务期间看到的数据始终一致，即使别人提交了新数据

## 快照读与当前读

- **快照读**：普通 SELECT，读 MVCC 版本链，不加锁
- **当前读**：`SELECT ... FOR UPDATE / FOR SHARE`、`UPDATE`、`DELETE`、`INSERT`，读最新提交的版本，并加锁

同一事务里混用两种读可能看到"不一致"：REPEATABLE READ 下快照读读到旧值，跟一条 UPDATE（当前读）后再快照读，值变了——因为 UPDATE 改的是最新版本。这是 MVCC 的正常行为，不是 bug。

RR 级别的幻读问题：快照读靠 MVCC 避免，当前读靠间隙锁（Next-Key Lock）避免，两种机制各管一半。

## 查看相关状态

```sql
SELECT @@transaction_isolation;                  -- 当前隔离级别
SELECT * FROM information_schema.innodb_trx;     -- 当前活跃事务
SELECT NAME FROM information_schema.innodb_trx;  -- trx_id 就藏在其中
```

undo log 回滚段大小与数量：`SHOW VARIABLES LIKE 'innodb_undo%'`。长事务持有旧版本不释放，undo 会持续增长——线上要监控长事务。
