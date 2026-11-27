//Stream流的练习，创建、中间操作、终结操作、收集器
//2026.11.26

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Stream流总结 {
	public static void main(String[] args) {
		List<String> 名单 = List.of("张三", "李四", "王五麻子", "赵六", "张三", "孙七", "周八很长很长");

		//创建：集合/of/iterate无限流
		Stream<String> s1 = 名单.stream();
		Stream<String> s2 = Stream.of("a", "b");
		Stream<Integer> s3 = Stream.iterate(0, n -> n + 2).limit(5);
		System.out.println("iterate:" + s3.collect(Collectors.toList()));

		//中间操作链：惰性，没有终结操作什么都不执行
		List<String> 处理后 = 名单.stream()
			.filter(s -> s.length() > 2)
			.map(String::toUpperCase)
			.distinct()
			.sorted()
			.collect(Collectors.toList());
		System.out.println("filter+map+distinct+sorted:" + 处理后);

		//limit/skip 分页效果
		System.out.println("skip(1).limit(2):" + 名单.stream().skip(1).limit(2).collect(Collectors.toList()));

		//flatMap：一对多摊平
		List<String> 词 = List.of("java,mysql", "docker,k8s");
		List<String> 拆开 = 词.stream()
			.flatMap(s -> Stream.of(s.split(",")))
			.collect(Collectors.toList());
		System.out.println("flatMap:" + 拆开);

		//终结操作：reduce 聚合
		int 总长度 = 名单.stream().mapToInt(String::length).sum();
		System.out.println("总长度:" + 总长度);
		Optional<String> 最长 = 名单.stream().max(Comparator.comparingInt(String::length));
		System.out.println("最长:" + 最长.orElse("无"));

		//短路：findFirst / anyMatch
		Optional<String> 第一个三字 = 名单.stream().filter(s -> s.length() == 2).findFirst();
		System.out.println("第一个两字名:" + 第一个三字.orElse("无"));
		boolean 有张 = 名单.stream().anyMatch(s -> s.startsWith("张"));
		System.out.println("有姓张的:" + 有张);

		//Optional 取值
		System.out.println("orElse用法:" + 名单.stream().filter(s -> s.length() > 100).findFirst().orElse("没有匹配"));

		//收集器：groupingBy / partitioningBy / joining / toMap
		Map<Integer, List<String>> 按长度分组 = 名单.stream()
			.distinct()
			.collect(Collectors.groupingBy(String::length));
		System.out.println("groupingBy按长度:" + 按长度分组);

		Map<Integer, Long> 组内计数 = 名单.stream()
			.collect(Collectors.groupingBy(String::length, Collectors.counting()));
		System.out.println("groupingBy+counting:" + 组内计数);

		Map<Boolean, List<String>> 分区 = 名单.stream()
			.collect(Collectors.partitioningBy(s -> s.length() > 2));
		System.out.println("partitioningBy:" + 分区);

		String 拼接 = 名单.stream().distinct().collect(Collectors.joining(", ", "[", "]"));
		System.out.println("joining:" + 拼接);

		Map<String, Integer> 名字到长度 = 名单.stream()
			.distinct()
			.collect(Collectors.toMap(s -> s, String::length, (旧, 新) -> 新));
		System.out.println("toMap:" + 名字到长度);

		//IntStream 数值流
		IntSummaryPrint();
		//parallelStream 并行
		long 计数 = 名单.parallelStream().filter(s -> s.length() > 1).count();
		System.out.println("parallelStream计数:" + 计数);
	}

	static void IntSummaryPrint() {
		IntStream 原生流 = IntStream.rangeClosed(1, 10);
		System.out.println("rangeClosed求和:" + 原生流.sum());
		//rangeClosed用完就关了，再造一个做统计
		java.util.IntSummaryStatistics st = List.of(1, 2, 3, 4, 5).stream()
			.mapToInt(Integer::intValue).summaryStatistics();
		System.out.println("summaryStatistics: count=" + st.getCount() + " sum=" + st.getSum()
			+ " avg=" + st.getAverage());
	}
}
