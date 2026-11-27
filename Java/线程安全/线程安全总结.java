//线程安全的练习，synchronized、Lock、wait/notify、tryLock防死锁都过了一遍
//2026.11.25

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class 线程安全总结 {
	static int 计数 = 0;
	static final Object 锁 = new Object();
	static final ReentrantLock 显式锁 = new ReentrantLock();

	public static void main(String[] args) throws InterruptedException {
		//不加锁：10个线程各加1万次，结果小于10万
		Thread[] 无锁线程 = new Thread[10];
		for (int i = 0; i < 10; i++) {
			无锁线程[i] = new Thread(() -> {
				for (int j = 0; j < 10000; j++) 计数++;
			});
			无锁线程[i].start();
		}
		for (Thread t : 无锁线程) t.join();
		System.out.println("无锁结果(丢更新):" + 计数);

		//synchronized 同步代码块，锁同一个对象
		计数 = 0;
		Thread[] 同步线程 = new Thread[10];
		for (int i = 0; i < 10; i++) {
			同步线程[i] = new Thread(() -> {
				for (int j = 0; j < 10000; j++) {
					synchronized (锁) {
						计数++;
					}
				}
			});
			同步线程[i].start();
		}
		for (Thread t : 同步线程) t.join();
		System.out.println("synchronized结果:" + 计数);

		//Lock：lock/unlock，unlock必须写finally
		计数 = 0;
		Thread[] lock线程 = new Thread[10];
		for (int i = 0; i < 10; i++) {
			lock线程[i] = new Thread(() -> {
				for (int j = 0; j < 10000; j++) {
					显式锁.lock();
					try {
						计数++;
					} finally {
						显式锁.unlock();
					}
				}
			});
			lock线程[i].start();
		}
		for (Thread t : lock线程) t.join();
		System.out.println("Lock结果:" + 计数);

		//tryLock：拿不到锁不阻塞，避免死锁的写法
		ReentrantLock 锁A = new ReentrantLock();
		锁A.lock();	//main线程先占住锁A
		Thread 抢锁线程 = new Thread(() -> {
			if (锁A.tryLock()) {
				System.out.println("拿到锁A");
				锁A.unlock();
			} else {
				System.out.println("tryLock失败，不阻塞，直接走else");
			}
		});
		抢锁线程.start();
		抢锁线程.join();
		锁A.unlock();

		//死锁场景说明：线程1先锁A再锁B，线程2先锁B再锁A，互相等待
		//避免方法：所有线程按相同顺序获取锁，或都用tryLock带超时

		//wait/notify：单个缓冲区，生产者存1条，消费者取1条
		Queue<Integer> 缓冲 = new LinkedList<>();
		Object 缓冲锁 = new Object();

		Thread 消费者 = new Thread(() -> {
			synchronized (缓冲锁) {
				while (缓冲.isEmpty()) {	//必须while，唤醒时条件可能又变了
					try {
						System.out.println("消费者:队列空，wait");
						缓冲锁.wait();	//释放锁等待
					} catch (InterruptedException e) { }
				}
				System.out.println("消费者:取出" + 缓冲.poll());	//poll取头元素
			}
		});
		消费者.start();
		Thread.sleep(100);	//让消费者先进wait

		Thread 生产者 = new Thread(() -> {
			synchronized (缓冲锁) {	//同一把锁才能互相通信
				缓冲.add(42);
				System.out.println("生产者:存入42，notify");
				缓冲锁.notifyAll();
			}
		});
		生产者.start();
		生产者.join();
		消费者.join();

		//wait在synchronized外调用抛IllegalMonitorStateException
		try {
			缓冲锁.wait();
		} catch (IllegalMonitorStateException e) {
			System.out.println("锁外调wait抛IllegalMonitorStateException");
		} catch (InterruptedException e) { }
	}
}
