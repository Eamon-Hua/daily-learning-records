//static 的练习，2026.11.16
//静态变量一份共享，静态方法不用 new，静态代码块类加载时跑一次

class 静态总结 {

	public static void main(String[] args) {

		//静态变量：两个学员共用同一个"学校"
		学员 s1=new 学员("张三");
		学员 s2=new 学员("李四");
		System.out.println(s1.学校);   //北大
		System.out.println(s2.学校);   //北大

		//正式写法是 类名.静态变量，改一个全变
		学员.学校="清华";
		System.out.println(s1.学校);   //清华

		//人数是计数器，每 new 一个加 1
		System.out.println(学员.人数);   //2

		//静态方法直接类名调，不用 new
		System.out.println(工具.最大值(3,7));   //7

		//访问 配置.值 会触发类加载，先跑静态代码块再取值
		//所以输出顺序是：配置加载好了 -> 默认
		System.out.println(配置.值);

		//再 new 一次，静态代码块也不会再跑了
		学员 s3=new 学员("王五");
		System.out.println(学员.人数);   //3
	}

}

class 学员 {
	String name;
	static String 学校="北大";
	static int 人数=0;

	public 学员(String name) {
		this.name=name;
		人数++;   //每造一个学员加 1
	}
}

class 工具 {

	//静态方法里没有 this，不能直接用非静态成员
	//比如这里写 System.out.println(某对象的name) 编译不过
	public static int 最大值(int a,int b) {
		return a>=b?a:b;
	}
}

class 配置 {
	static String 值="默认";

	static {
		System.out.println("配置加载好了");   //只在类第一次被用到时执行一次
	}
}
