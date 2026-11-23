//集合的练习，List、Set、Map都过了一遍
//2026.11.20

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class 集合总结 {
	public static void main(String[] args) {
		//List：有序可重复，ArrayList查询快
		List<String> list=new ArrayList<>();
		list.add("a");
		list.add("b");
		list.add(0,"头");			//指定位置插
		list.add("b");			//List允许重复
		System.out.println(list);		//[头, a, b, b]
		System.out.println(list.get(1));	//a，下标取
		list.set(1,"替换");
		System.out.println(list.get(1));	//替换
		System.out.println(list.contains("b"));	//true

		//Integer列表remove的坑：remove(1)删下标不是删数字1
		List<Integer> nums=new ArrayList<>();
		nums.add(10);nums.add(20);nums.add(30);
		nums.remove(1);				//删的是下标1的20
		System.out.println(nums);		//[10, 30]
		nums.remove(Integer.valueOf(30));	//按对象删才写这个
		System.out.println(nums);		//[10]

		//Set：不可重复，无序
		Set<String> set=new HashSet<>();
		set.add("苹果");
		set.add("香蕉");
		set.add("苹果");			//加不进去
		System.out.println(set.size());	//2
		for(String s:set) {
			System.out.println(s);	//顺序不保证，跟加的顺序可能不一样
		}

		//自定义类去重必须hashCode和equals一起重写
		Set<学生> 班级=new HashSet<>();
		班级.add(new 学生("张三",18));
		班级.add(new 学生("张三",18));	//内容一样，被去重
		System.out.println("班级人数:"+班级.size());	//1

		//Map：键值对
		Map<String,Integer> 成绩=new HashMap<>();
		成绩.put("语文",95);
		成绩.put("数学",88);
		成绩.put("语文",92);			//键重复就覆盖，92顶掉95
		System.out.println(成绩.get("语文"));		//92
		System.out.println(成绩.get("英语"));		//null，不报错但小心空指针
		System.out.println(成绩.getOrDefault("英语",0));	//0，安全写法

		//遍历推荐entrySet，键值一次拿全
		for(Map.Entry<String,Integer> e:成绩.entrySet()) {
			System.out.println(e.getKey()+"="+e.getValue());
		}
	}
}

//只重写equals不去重会失败，两个都要
class 学生 {
	String 名字;
	int 年龄;

	public 学生(String 名字,int 年龄) {
		this.名字=名字;
		this.年龄=年龄;
	}

	@Override
	public boolean equals(Object o) {
		if(this==o) return true;
		if(!(o instanceof 学生)) return false;
		学生 s=(学生)o;
		return 年龄==s.年龄 && 名字.equals(s.名字);
	}

	@Override
	public int hashCode() {
		return 名字.hashCode()*31+年龄;
	}
}
