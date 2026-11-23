//异常的练习，try-catch-finally、throw/throws、自定义异常都过了一遍
//2026.11.19

public class 异常总结 {
	public static void main(String[] args) {
		//基础接法：谁出问题catch谁
		try {
			int r=10/0;
			System.out.println("这行永远执行不到，异常一抛后面直接跳catch");
		}catch(ArithmeticException e) {
			System.out.println("算错了："+e.getMessage());	// / by zero
		}finally {
			System.out.println("无论如何都执行");	//关资源放这里
		}

		//finally就算try里return了也照样跑，返回值是finally执行完才带出去
		System.out.println("有return的方法："+试试return());

		//多个catch：子类在前父类在后，写反编译报错
		try {
			String s=null;
			s.length();
		}catch(NullPointerException e) {
			System.out.println("空指针："+e.getMessage());
		}catch(Exception e) {	//兜底的父类只能放最后
			System.out.println("其他异常");
		}

		//自定义异常：业务上余额不足，抛自己的
		账户 a=new 账户(100);
		try {
			a.取钱(500);
		}catch(取款异常 e) {
			System.out.println("取款失败："+e.getMessage());	//余额不足
		}
		System.out.println("余额还剩："+a.看余额());	//100.0，钱没扣

		//不catch就会往上抛，这里直接演示崩溃前的样子
		账户 b=new 账户(10);
		try {
			b.取钱(20);
		}catch(取款异常 e) {
			e.printStackTrace();	//排查问题用这个，完整调用栈都打出来
		}
	}

	//验证finally与return的执行顺序
	public static String 试试return() {
		try {
			return "try里的返回值";
		}finally {
			System.out.println("finally比return先执行完");	//这行先打出来
		}
	}
}

//自定义异常：继承RuntimeException就是不受检的，想强制别人处理就继承Exception
class 取款异常 extends RuntimeException {
	public 取款异常(String message) {
		super(message);		//传给父类，getMessage()才有内容
	}
}

class 账户 {
	private double 余额;

	public 账户(double 余额) {
		this.余额=余额;
	}

	//throws声明出去，谁调谁处理；里面真出错用throw抛
	public void 取钱(double money) throws 取款异常 {
		if(money>余额) {
			throw new 取款异常("余额不足");	//抛出去后面代码不执行
		}
		余额-=money;
	}

	public double 看余额() {
		return 余额;
	}
}
