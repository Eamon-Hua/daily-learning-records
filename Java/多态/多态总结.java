//多态的练习，向上转型、动态绑定、instanceof都过了一遍
//2026.11.18

public class 多态总结 {
	public static void main(String[] args) {
		动物 a=new 狗();		//向上转型，自动的
		a.吃();				//狗吃骨头——运行看右边，跑的是狗的版本
		//a.看家();			//编译报错——编译看左边，动物类里没有看家

		喂(new 狗());			//统一处理，喂的参数是动物，啥都能传
		喂(new 猫());			//猫吃鱼

		System.out.println(a instanceof 狗);	//true
		System.out.println(a instanceof 猫);	//false，兄弟类之间不算

		动物 b=new 猫();
		if(b instanceof 猫 m){	//Java16+新写法，判断和转型一步到位
			m.抓老鼠();			//猫抓老鼠
		}

		//属性没有多态！这条输出跟直觉不一样
		System.out.println("a.腿数="+a.腿数);	//输出4——引用是动物类型，访问到的是动物类的腿数，狗里写的6不算
	}

	//参数写父类类型，统一处理各种子类，加新动物这个方法一行不用改
	public static void 喂(动物 a){
		a.吃();
	}
}

class 动物 {
	int 腿数=4;

	public void 吃(){
		System.out.println("吃东西");
	}
}

class 狗 extends 动物 {
	int 腿数=6;				//狗自己的腿数是6，但多态引用访问到的是父类的4，属性不参与多态

	@Override
	public void 吃() {
		System.out.println("狗吃骨头");
	}

	public void 看家() {		//子类特有方法，动物引用调不了，编译报错
		System.out.println("狗看家");
	}
}

class 猫 extends 动物 {
	@Override
	public void 吃() {
		System.out.println("猫吃鱼");
	}

	public void 抓老鼠() {
		System.out.println("猫抓老鼠");
	}
}
