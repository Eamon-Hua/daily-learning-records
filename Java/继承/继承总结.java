//继承的练习，extends、super、重写都过了一遍
//2026.11.18

public class 继承总结 {
	public static void main(String[] args) {
		狗 d=new 狗();
		d.名字="旺财";
		d.吃();			//旺财在吃东西，方法继承来的

		猫 m=new 猫("咪咪");	//先输出 动物构造器，爹的构造器先跑
		m.吃();			//吃东西 / 咪咪还要吃鱼
		m.叫();

		鸟 b=new 鸟();
		b.名字="小黑";
		b.吃();			//小黑在吃东西（重写了但先调super.吃）
		System.out.println("飞100米，剩"+b.飞(100)+"点体力");	//鸟的体力是父类给的
	}
}

class 动物 {
	String 名字;
	int 体力=100;

	//父类只写了带参构造器，没写无参的
	//子类构造器第一行必须super(名字)，不写会编译报错
	public 动物(String 名字) {
		this.名字=名字;
		System.out.println("动物构造器");
	}

	public void 吃() {
		System.out.println("吃东西");
	}
}

class 狗 extends 动物 {
	//狗不写构造器的话，编译器默认塞super()，父类没有无参的就报错
	//所以必须自己写构造器并调super(名字)
	public 狗() {
		super("旺财");
	}
}

class 猫 extends 动物 {
	public 猫(String 名字) {
		super(名字);		//不写这行直接编译不过，父类没有无参构造器
	}

	@Override
	public void 吃() {
		super.吃();		//先调爹的版本，再补自己的
		System.out.println(名字+"还要吃鱼");
	}

	public void 叫() {
		System.out.println(名字+"喵喵叫");
	}
}

class 鸟 extends 动物 {
	public 鸟() {
		super("鸟");
	}

	@Override
	public void 吃() {
		System.out.println(名字+"在吃东西");
	}

	//飞是鸟自己的新方法
	public int 飞(int 米) {
		体力=体力-米/10;
		return 体力;
	}
}
