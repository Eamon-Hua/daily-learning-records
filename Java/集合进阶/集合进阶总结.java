//集合进阶的练习，Collections工具类、Comparable与Comparator排序都过了一遍
//2026.11.23

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class 集合进阶总结 {
	public static void main(String[] args) {
		List<Integer> nums=new ArrayList<>();
		Collections.addAll(nums,5,1,8,3,9);	//批量加
		System.out.println(nums);

		Collections.sort(nums);			//Integer自己实现了Comparable，直接排
		System.out.println("升序:"+nums);	//[1, 3, 5, 8, 9]

		Collections.reverse(nums);
		System.out.println("反转:"+nums);	//[9, 8, 5, 3, 1]

		Collections.shuffle(nums);		//洗牌，每次结果都不一样
		System.out.println("打乱:"+nums);

		System.out.println("最大:"+Collections.max(nums));
		System.out.println("最小:"+Collections.min(nums));

		//Comparable：规矩写在类里，compareTo返回负数=我小=排前面
		List<学生> 班=new ArrayList<>();
		Collections.addAll(班,new 学生("张三",85),new 学生("李四",92),new 学生("王五",78));
		Collections.sort(班);			//自动按分数升序
		System.out.println("Comparable按分数升序:"+班);

		//Comparator：规矩写在外面，同一个类想怎么比就怎么比
		Collections.sort(班,new Comparator<学生>() {
			@Override
			public int compare(学生 a,学生 b) {
				return b.分数-a.分数;	//b减a，降序，分高的在前
			}
		});
		System.out.println("Comparator按分数降序:"+班);

		//lambda写法，一句顶匿名内部类
		班.sort((a,b)->a.名字.compareTo(b.名字));
		System.out.println("lambda按名字:"+班);

		//unmodifiableList：包出来的只读视图，add就炸
		List<Integer> 只读=Collections.unmodifiableList(nums);
		try {
			只读.add(100);
		}catch(UnsupportedOperationException e) {
			System.out.println("只读列表确实不让加");
		}
	}
}

//Comparable：自然排序，sort、TreeSet、TreeMap都认这套规矩
class 学生 implements Comparable<学生> {
	String 名字;
	int 分数;

	public 学生(String 名字,int 分数) {
		this.名字=名字;
		this.分数=分数;
	}

	@Override
	public int compareTo(学生 o) {
		//this减o是升序；分数差距会溢出的场景用Integer.compare更稳
		return Integer.compare(this.分数,o.分数);
	}

	@Override
	public String toString() {
		return 名字+分数;
	}
}
