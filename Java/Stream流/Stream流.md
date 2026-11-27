# Java Stream 流

记于 2026.11.26

Stream 是对集合做批量声明式处理的 API：描述"做什么"，不写"怎么循环"。注意它是数据处理流水线，与 IO 流（InputStream）无关。

## 三段结构

创建 → 中间操作（0 到多个，返回新 Stream，惰性执行）→ 终结操作（只能一个，触发执行并产生结果）。

Stream 一次性使用：终结操作之后这个 Stream 不能再用，再调操作抛 IllegalStateException。

## 创建方式

```java
Stream<Integer> s1 = list.stream();              // 集合
Stream<String> s2 = Stream.of("a", "b", "c");    // 直接给值
Stream<Integer> s3 = Stream.iterate(0, n -> n + 2).limit(5);   // 无限流，配 limit
Stream<String[]> s4 = Files.lines(Path.of("a.txt"));           // 按行读文件
```

数组用 `Arrays.stream(arr)`；Map 没有 stream()，转成 entrySet().stream()。

## 中间操作（返回新 Stream，惰性）

```java
List<String> r = list.stream()
        .filter(s -> s.length() > 3)     // 过滤
        .map(String::toUpperCase)        // 一对一转换
        .sorted()                        // 排序，可传 Comparator
        .distinct()                      // 去重（按 equals/hashCode）
        .limit(10)                       // 取前10个
        .skip(5)                         // 跳过前5个
        .peek(System.out::println)       // 调试：经过时看一眼
        .collect(Collectors.toList());   // 终结操作触发执行
```

**惰性执行**：没有终结操作，中间操作一行都不跑。短路操作（`findFirst()`、`anyMatch()`）命中后后面的元素不再处理。

`flatMap` 把每个元素变成流再摊平：`flatMap(s -> Arrays.stream(s.split(",")))`，用于"一对多转换后合并"。

## 终结操作

- **forEach** / **forEachOrdered**：逐个处理
- **collect**：汇总成集合或字符串（下节收集器）
- **reduce**：聚合成一个值
- **count / min / max / sum**（IntStream）
- **anyMatch / allMatch / noneMatch**：布尔判断
- **findFirst / findAny**：取一个元素

```java
int 总和 = list.stream().mapToInt(Integer::intValue).sum();
Optional<String> 最长 = list.stream().max(Comparator.comparingInt(String::length));
Optional<String> 第一个 = list.stream().filter(s -> s.startsWith("a")).findFirst();
```

返回值是 `Optional<T>` 而不是 null：`ifPresent(值 -> ...)`、`orElse(默认值)` 取值，不要 get()（空时抛异常）。

## 收集器 Collectors

```java
Map<Integer, List<String>> 按长度分组 =
        list.stream().collect(Collectors.groupingBy(String::length));

Map<Boolean, List<String>> 分区 =
        list.stream().collect(Collectors.partitioningBy(s -> s.length() > 3));

String 拼接 = list.stream().collect(Collectors.joining(", ", "[", "]"));

Map<String, Integer> 名字到长度 =
        list.stream().collect(Collectors.toMap(s -> s, String::length));
```

- `groupingBy`：按 key 分组，value 是列表；`groupingBy(key, counting())` 组内计数
- `partitioningBy`：按条件二分
- `joining`：字符串拼接，可带分隔符和前后缀
- `toMap`：key 重复时抛 IllegalStateException，第三个参数传合并函数 `(旧, 新) -> 新`

## IntStream 数值流

`mapToInt` / `mapToDouble` 转成原生类型流，免装箱，且多了 sum、average、summaryStatistics：

```java
IntSummaryStatistics st = list.stream().mapToInt(String::length).summaryStatistics();
// st.getCount() / getSum() / getMin() / getMax() / getAverage()
```

parallelStream() 走并行流（ForkJoin 池），大集合纯计算时有收益，操作有状态或 IO 时不用。
