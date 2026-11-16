//封装的练习，2026.11.16
//private 藏属性，getter/setter 开口子，this 区分成员和参数

class 封装总结 {

	public static void main(String[] args) {

		//想存负数？setter 里拦住了
		银行账户 a=new 银行账户();
		a.设余额(1000);
		System.out.println(a.取余额());   //1000.0
		a.设余额(-500);
		System.out.println(a.取余额());   //还是1000.0，-500没进去

		//标准的 getter/setter 套路
		员工 e=new 员工("张三",5000);
		System.out.println(e.getName()+" "+e.getSalary());
		e.setSalary(6000);
		System.out.println(e.getSalary());
		e.setSalary(-100);   //非法值不收
		System.out.println(e.getSalary());   //6000

		//参数和成员变量同名，构造器里必须用 this 区分
		学生 s=new 学生("李四",20);
		s.自我介绍();   //我是李四，今年20岁

		//this(...) 调另一个构造器，默认 18 岁
		学生 s2=new 学生("王五");
		s2.自我介绍();   //我是王五，今年18岁

		//无参构造器也能转发
		学生 s3=new 学生();
		s3.自我介绍();   //我是无名，今年18岁
	}

}

class 银行账户 {
	private double 余额;

	public void 设余额(double money) {
		if(money>0) {
			余额=money;
		}
	}

	public double 取余额() {
		return 余额;
	}
}

class 员工 {
	private String name;
	private double salary;

	public 员工(String name,double salary) {
		this.name=name;      //不用 this 就是自己赋给自己，编译能过但没生效
		this.salary=salary;
	}

	public String getName() {
		return name;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		if(salary>=0) {
			this.salary=salary;
		}
	}
}

class 学生 {
	private String name;
	private int age;

	public 学生(String name,int age) {
		this.name=name;
		this.age=age;
	}

	//this(...) 必须放第一行
	public 学生(String name) {
		this(name,18);
	}

	public 学生() {
		this("无名",18);
	}

	public void 自我介绍() {
		System.out.println("我是"+name+"，今年"+age+"岁");
	}
}
