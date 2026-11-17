//抽象类和接口的练习，图形用抽象类，会飞用接口
//2026.11.18

public class 抽象类与接口总结 {
	public static void main(String[] args) {
		//图形 g=new 图形();	//抽象类不能new，这行报错，注释掉了
		图形 圆=new 圆(3);
		圆.描述();			//我是一个图形，面积28.274333882308138
		图形 方=new 方形(4);
		方.描述();

		飞机 波音=new 飞机();
		波音.飞();			//引擎推动起飞
		波音.降落();		//减速降落——接口给的默认实现，飞机没重写照样能用
		会飞.限高();		//飞行限高1000米，静态方法用接口名调

		超人类 sc=new 超人类();
		sc.飞();			//披风一甩上天
		sc.降落();			//超人自己重写了降落，用的是自己的版本
	}
}

abstract class 图形 {
	abstract double 面积();		//没法写实现，只能让子类自己算

	public void 描述() {		//抽象类里可以有普通方法
		System.out.println("我是一个图形，面积"+面积());
	}
}

class 圆 extends 图形 {
	double 半径;

	public 圆(double r) {
		this.半径=r;
	}

	@Override
	double 面积() {
		return 3.141592653589793*半径*半径;
	}
}

class 方形 extends 图形 {
	double 边长;

	public 方形(double a) {
		this.边长=a;
	}

	@Override
	double 面积() {
		return 边长*边长;
	}
}

interface 会飞 {
	void 飞();					//自动就是public abstract，写了修饰符反而不让加

	default void 降落() {		//默认方法，实现类可重写可不重写
		System.out.println("减速降落");
	}

	static void 限高() {		//接口的静态方法，只能接口名.调用，实现类继承不到
		System.out.println("飞行限高1000米");
	}
}

class 飞机 implements 会飞 {
	@Override
	public void 飞() {			//重写接口方法必须public，接口方法默认public，权限只能放大
		System.out.println("引擎推动起飞");
	}
}

class 超人类 implements 会飞 {
	@Override
	public void 飞() {
		System.out.println("披风一甩上天");
	}

	@Override
	public void 降落() {		//默认方法看不顺眼可以重写
		System.out.println("落到电话亭后面变身");
	}
}
