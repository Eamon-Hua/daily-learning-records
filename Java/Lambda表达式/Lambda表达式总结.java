//Lambda表达式与函数式接口的练习，@FunctionalInterface、四大内置接口、方法引用
//2026.11.26

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Lambda表达式总结 {

	@FunctionalInterface
	interface 计算 {
		int apply(int a, int b);
		//再加一个抽象方法就编译报错，default方法不占名额
	}

	public static void main(String[] args) {
		//自定义函数式接口：参数类型推断，单参数省括号，单语句省return
		计算 加法 = (a, b) -> a + b;
		计算 乘法 = (a, b) -> a * b;
		System.out.println("加法.apply(3,4)=" + 加法.apply(3, 4));
		System.out.println("乘法.apply(3,4)=" + 乘法.apply(3, 4));

		//对比匿名内部类写法
		计算 除法 = new 计算() {
			@Override
			public int apply(int a, int b) {
				return a / b;
			}
		};
		System.out.println("匿名内部类写法:" + 除法.apply(10, 3));

		//四大内置接口
		Consumer<String> 打印 = s -> System.out.println("Consumer消费:" + s);
		打印.accept("一条消息");

		Supplier<List<String>> 新列表 = () -> new ArrayList<>();
		List<String> l = 新列表.get();
		l.add("Supplier造的列表");
		System.out.println(l);

		Function<String, Integer> 长度 = s -> s.length();
		System.out.println("Function转换:\"hello\"长度=" + 长度.apply("hello"));

		Predicate<String> 非空 = s -> !s.isEmpty();
		System.out.println("Predicate判断:\"\"非空=" + 非空.test("") + ",\"x\"非空=" + 非空.test("x"));

		//方法引用四种形式
		Consumer<String> 引用打印 = System.out::println;	//对象::实例方法
		Function<String, Integer> 引用长度 = String::length;	//类::实例方法，参数作调用者
		Supplier<ArrayList<String>> 引用构造 = ArrayList::new;	//类::new 构造器引用
		引用打印.accept("方法引用输出的这行");
		System.out.println("引用长度=" + 引用长度.apply("方法引用"));
		System.out.println("引用构造=" + 引用构造.get().getClass().getSimpleName());

		//组合：Predicate的and/or/negate，Function的andThen
		Predicate<String> 有数字 = s -> s.matches(".*\\d.*");
		Predicate<String> 长于5 = s -> s.length() > 5;
		Predicate<String> 组合 = 有数字.and(长于5);
		System.out.println("\"abc123\"有数字且长于5=" + 组合.test("abc123"));
		Function<String, String> 先trim = String::trim;
		System.out.println("trim后长度:" + 先trim.apply("  hi  ").length());

		//变量捕获：外部局部变量必须事实最终变量
		int 前缀 = 2026;
		Function<Integer, String> 编号 = n -> 前缀 + "-" + n;	//前缀不能再赋值
		System.out.println("变量捕获:" + 编号.apply(1));
	}
}
