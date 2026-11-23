//泛型与迭代的练习，泛型类/方法、增强for、Iterator都过了一遍
//2026.11.20

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class 泛型与迭代总结 {
	public static void main(String[] args) {
		//泛型集合：塞错类型编译期就拦住
		List<String> list=new ArrayList<>();
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");
		//list.add(123);		//编译报错，泛型拦在运行前
		String s=list.get(0);		//取出来不用强转
		System.out.println(s);

		//泛型类：同一个类，装不同类型
		盒子<String> b1=new 盒子<>();
		b1.放("苹果");
		System.out.println(b1.拿());

		盒子<Integer> b2=new 盒子<>();
		b2.放(666);
		System.out.println(b2.拿());	//666，不用强转

		//带边界：T必须是Number及其子类
		数字盒子<Double> b3=new 数字盒子<>();
		b3.放(3.14);
		System.out.println("翻倍:"+b3.翻倍());	//6.28

		//泛型方法：调用时由实参推断类型
		打印("串");
		打印(123);
		打印(3.14);

		//增强for：底层就是Iterator，只读遍历用它
		for(String x:list) {
			System.out.println(x);
		}

		//边遍历边删：必须用it.remove()，用list.remove()直接炸
		Iterator<String> it=list.iterator();
		while(it.hasNext()) {
			String x=it.next();
			if("b".equals(x)) {
				it.remove();	//安全删除当前元素
			}
		}
		System.out.println(list);	//[a, c]

		//验证坑：边遍历边改集合会炸——JDK25起放宽了"删刚访问过的元素"，但删光标后面的照样炸
		try {
			for(String x:list) {
				if("a".equals(x)) {
					list.remove("d");	//d在光标后面，modCount对不上直接炸
				}
			}
		}catch(java.util.ConcurrentModificationException e) {
			System.out.println("边遍历边list.remove光标后面的元素确实会炸");
		}

		//偷懒的替代写法：removeIf一步到位，把c删掉
		list.removeIf(x->x.equals("c"));
		System.out.println(list);	//[a]
	}

	//<T>写在返回值前面，这是泛型方法的标志
	public static <T> void 打印(T x) {
		System.out.println(x);
	}
}

//T是占位符，用的时候才确定
class 盒子<T> {
	private T 东西;

	public void 放(T 东西) {
		this.东西=东西;
	}

	public T 拿() {
		return 东西;
	}
}

//extends限定上限：只收Number家族
class 数字盒子<T extends Number> {
	private T 数字;

	public void 放(T 数字) {
		this.数字=数字;
	}

	public double 翻倍() {
		return 数字.doubleValue()*2;	//有了Number边界才能调doubleValue
	}
}
