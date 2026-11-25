//多线程基础的练习，Thread创建、状态、优先级、常用方法都过了一遍
//2026.11.24

public class 多线程基础总结 {
	public static void main(String[] args) throws InterruptedException, java.util.concurrent.ExecutionException {
		//方式1：继承Thread
		Thread t1=new Thread(){
			@Override
			public void run() {
				System.out.println(getName()+" run中，优先级:"+getPriority());
			}
		};
		t1.setName("继承方式");
		t1.start();	//必须start，直接调run()是普通方法调用，不会有第二条线程

		//方式2：实现Runnable（lambda），不占继承名额
		Thread t2=new Thread(()->{
			System.out.println(Thread.currentThread().getName()+" run中");
		},"Runnable方式");
		t2.start();
		t2.join();	//main等到t2跑完才往下走

		//方式3：Callable，有返回值
		java.util.concurrent.FutureTask<Integer> ft=
			new java.util.concurrent.FutureTask<>(()->1+1);
		Thread t3=new Thread(ft,"Callable方式");
		t3.start();
		System.out.println("Callable结果:"+ft.get());

		//生命周期：NEW -> RUNNABLE -> TIMED_WAITING -> TERMINATED
		Thread t4=new Thread(()->{
			try {
				Thread.sleep(100);	//睡100ms
			}catch(InterruptedException e) {
				System.out.println("被中断");
			}
		});
		System.out.println("刚new:"+t4.getState());		//NEW
		t4.start();
		System.out.println("start后:"+t4.getState());	//RUNNABLE
		Thread.sleep(30);
		System.out.println("sleep中:"+t4.getState());	//TIMED_WAITING
		t4.join();
		System.out.println("跑完:"+t4.getState());		//TERMINATED

		//再start一次抛IllegalThreadStateException
		try {
			t4.start();
		}catch(IllegalThreadStateException e) {
			System.out.println("重复start确实抛异常");
		}

		//守护线程：必须在start之前set，用户线程全结束JVM就退出
		Thread 守护=new Thread(()->{
			try {
				Thread.sleep(10_000);	//睡10秒，main不会等它
			}catch(InterruptedException e) { }
			System.out.println("守护线程跑完了——这行不会输出，main退出时JVM直接终止了它");
		});
		守护.setDaemon(true);
		守护.start();

		//优先级：1~10，默认5，高优先级只是概率高不是保证
		System.out.println("默认优先级:"+Thread.currentThread().getPriority());
		Thread 高=new Thread(()->{ });
		高.setPriority(Thread.MAX_PRIORITY);
		System.out.println("设置后:"+高.getPriority());

		//多线程抢CPU：执行顺序不固定，跑几次结果不一样
		for(int i=1;i<=3;i++) {
			String 名="并发"+i;
			new Thread(()->System.out.println(Thread.currentThread().getName())).start();
		}
		Thread.sleep(200);	//给上面线程一点时间输出
		System.out.println("main结束");
	}
}
