# Java Object 类

记于 2026.11.18

Object 是所有类的根父类——不写 extends，编译器也默认给你 `extends Object`。所以 Object 里的方法每个对象都有，最常用的是三个：**toString、equals、hashCode**。

## toString

打印对象时自动调用的方法。Object 给的默认实现是一串"类名@十六进制地址"，比如 `狗@1b6d3586`——地址对人没用，所以打印对象前一般都重写它：

```java
@Override
public String toString() {
    return "狗{名字=" + 名字 + ", 年龄=" + 年龄 + "}";
}

System.out.println(d);          // 打印时自动调 toString，不用写 d.toString()
System.out.println("狗是" + d); // 字符串拼接时也自动调
```

这个方法是 IDEA 右键 Generate 里一键生成的，手写两遍知道格式就行。

## equals

Object 给的默认实现就是 `==`，**比的是地址**：

```java
狗 d1 = new 狗("旺财");
狗 d2 = new 狗("旺财");

System.out.println(d1 == d2);        // false，两个 new 出来地址不同
System.out.println(d1.equals(d2));   // 也是 false！默认 equals 就是 ==
```

两个"旺财"内容一样却返回 false——想让"内容相同就算相等"，就得重写 equals：

```java
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;              // 自己和自己比，直接返回 true
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;   // 类都不同，不可能相等
    狗 other = (狗) obj;
    return 名字.equals(other.名字) && 年龄 == other.年龄;
}
```

坑：**String 之所以能用 equals 比内容，是因为 String 自己重写了 equals**。自己写的类不重写，equals 和 == 就是一个东西。重写时参数类型必须是 Object，写成 `equals(狗 d)` 那是重载不是重写，比较行为不会变，这个坑编译器不报错，`@Override` 注解能帮你拦住。

## hashCode

返回对象的哈希值（int）。默认实现跟地址挂钩。**hashCode 和 equals 是绑定的，规矩是：equals 相等的两个对象，hashCode 必须相等**。

为什么绑定？HashSet / HashMap 存东西先看 hashCode 分桶，桶里再用 equals 逐个比。只重写 equals 不重写 hashCode，两个"内容相等"的对象 hashCode 不同，被分到不同桶里——HashSet 里就存进去两个"相等的旺财"，去重直接失效：

```java
Set<狗> set = new HashSet<>();
set.add(new 狗("旺财"));
set.add(new 狗("旺财"));
System.out.println(set.size());   // 不重写 hashCode 时输出 2！重写了输出 1
```

结论：**重写 equals 就必须一起重写 hashCode**，IDEA Generate 也是两个一起生成的。

## == 和 equals 怎么选

- 基本类型（int、double 等）只能用 ==，比的就是值
- 引用类型：== 永远比地址；想比内容用 equals（前提是这个类重写过，String 包装类都重写了；自己写的类得自己重写）

---

代码在同目录 `Object类总结.java`，能直接跑。
