//Object类的练习，toString、equals、hashCode都过了一遍
//2026.11.18

import java.util.HashSet;
import java.util.Set;

public class Object类总结 {
	public static void main(String[] args) {
		狗 d1=new 狗("旺财",3);
		狗 d2=new 狗("旺财",3);

		System.out.println(d1);			//狗{名字=旺财, 年龄=3}——重写过toString，不重写输出的是狗@xxxx
		System.out.println("拼接也自动调："+d1);

		System.out.println(d1==d2);			//false，地址不同
		System.out.println(d1.equals(d2));	//true，重写过equals按内容比

		Set<狗> set=new HashSet<>();
		set.add(d1);
		set.add(d2);
		System.out.println("set大小："+set.size());	//1——equals和hashCode都重写了，内容相同进不去第二个
		//只重写equals不重写hashCode的话这里是2，去重直接失效

		String s1=new String("hello");
		String s2=new String("hello");
		System.out.println(s1==s2);			//false
		System.out.println(s1.equals(s2));	//true——String自己重写了equals
	}
}

class 狗 {
	String 名字;
	int 年龄;

	public 狗(String 名字,int 年龄) {
		this.名字=名字;
		this.年龄=年龄;
	}

	@Override
	public String toString() {
		return "狗{名字="+名字+", 年龄="+年龄+"}";
	}

	@Override
	public boolean equals(Object obj) {
		if(this==obj) return true;				//自己比自己
		if(obj==null) return false;
		if(getClass()!=obj.getClass()) return false;	//类不同直接否
		狗 other=(狗)obj;
		return 名字.equals(other.名字) && 年龄==other.年龄;
	}

	@Override
	public int hashCode() {
		//跟equals绑定的：名字和年龄参与equals，hashCode也得由它们算
		int result=名字.hashCode();
		result=31*result+年龄;
		return result;
	}
}
