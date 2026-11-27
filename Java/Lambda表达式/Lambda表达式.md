# Java Lambda 表达式与函数式接口

记于 2026.11.26

Lambda 是函数式接口的实例的简写：接口只有一个抽象方法时，new 匿名内部类那一套可以压成一行。语法：`(参数) -> 表达式` 或 `(参数) -> { 语句 }`。

## @FunctionalInterface

只有一个抽象方法的接口就是函数式接口，加不加注解都是。注解的作用是编译期检查：再往接口里加第二个抽象方法直接编译报错。接口里的 default 方法和 static 方法不占名额（Object 类的 public 方法如 toString 也不算）。

```java
@FunctionalInterface
public interface 计算 {
    int apply(int a, int b);        // 唯一的抽象方法
}

计算 加法 = (a, b) -> a + b;
计算 乘法 = (a, b) -> a * b;
System.out.println(加法.apply(3, 4));   // 7
```

参数类型可省略（编译器从接口签名推断）；单参数时括号可省；方法体只有一句时大括号和 return 可省。

## 四大内置接口

java.util.function 包，日常够用不必自建：

| 接口 | 抽象方法 | 输入 → 输出 | 用途 |
|---|---|---|---|
| `Consumer<T>` | `accept(T)` | T → 无 | 消费一个值，forEach |
| `Supplier<T>` | `get()` | 无 → T | 生产一个值，工厂 |
| `Function<T,R>` | `apply(T)` | T → R | 转换，map |
| `Predicate<T>` | `test(T)` | T → boolean | 判断，filter |

各有一个变体：`BiFunction<T,U,R>` 双输入、`IntPredicate` 原生类型免装箱、`UnaryOperator<T>` 输入输出同类型。

```java
Consumer<String> 打印 = s -> System.out.println(s);
Supplier<Double> 随机 = () -> Math.random();
Function<String, Integer> 长度 = String::length;
Predicate<String> 非空 = s -> !s.isEmpty();
```

## 方法引用

Lambda 体只是调用一个已有方法时，可以再简写成 `类::方法` 或 `对象::方法`：

```java
Consumer<String> 打印 = System.out::println;        // s -> System.out.println(s)
Function<String, Integer> 长度 = String::length;    // s -> s.length()
Supplier<ArrayList<String>> 新列表 = ArrayList::new; // 构造器引用
BiFunction<String, String, Boolean> 相等 = String::equals;
```

四种形式：静态方法 `类::静态方法`、实例方法（参数作调用者）`类::实例方法`、实例方法（特定对象）`对象::方法`、构造器 `类::new`。

## this 与变量捕获

Lambda 里 `this` 指向外围类的实例，不是匿名内部类语义。Lambda 能使用外部的局部变量，但该变量必须是事实最终变量（effectively final，赋值一次后不再改）——匿名内部类的同一规则。
