# Java IO 流进阶

记于 2026.11.24

基础篇（File、字节流、字符流、缓冲流）之外常用的四个：序列化流、转换流、打印流，加上 File 递归遍历的过滤写法。

## 序列化流

把对象直接写进文件、再从文件读回来，中间不用自己拼字符串再解析：

```java
// 写：对象 → 文件
try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("student.obj"))) {
    out.writeObject(new 学生("张三", 20));
}

// 读：文件 → 对象
try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("student.obj"))) {
    学生 s = (学生) in.readObject();
}
```

**被序列化的类必须实现 Serializable**。这个接口没有方法，只是个标记，JVM 看到它才允许 writeObject；不实现直接抛 NotSerializableException。

**必须写 serialVersionUID**：

```java
public class 学生 implements Serializable {
    private static final long serialVersionUID = 1L;
    ...
}
```

不写的话 JVM 按类的结构自动算一个。写入时的类和读取时的类只要改过任何一个成员，自动算出的值就对不上，readObject 直接抛 InvalidClassException——这个报错第一次遇到很难定位。显式声明成固定值，类小改（加个方法）也能继续读；成员改名/删掉，对应字段读出来是 null/默认值，不炸。

两个不参与序列化的成员：

- **transient**：被它修饰的成员写出去的是默认值（引用类型 null、数值 0）。密码这类不想落盘的字段用它
- **static**：属于类不属于对象，序列化的是对象状态，静态成员天然不参与。反序列化后读到的是当前 JVM 里类加载时的值

**序列化集合时，集合里的元素也必须可序列化**，漏一个整次 writeObject 都失败。

读取次数超过文件里对象个数会抛 EOFException，循环读文件里的多个对象要么先写入个数，要么序列化一个 `ArrayList<学生>`（ArrayList 本身实现了 Serializable）。

## 转换流

字节流转字符流的桥梁，核心用途是**指定编码**：

```java
// 读 GBK 编码的文件（FileReader 只认默认编码，读 GBK 全乱码）
try (BufferedReader br = new BufferedReader(
        new InputStreamReader(new FileInputStream("gbk文件.txt"), "GBK"))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
}

// 按 UTF-8 写文件
try (BufferedWriter bw = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream("out.txt"), "UTF-8"))) {
    bw.write("中文内容");
}
```

FileReader / FileWriter 用的是系统默认编码，Windows 上是 GBK——用它读 UTF-8 文件中文全乱。**涉及编码的地方一律显式指定**，要么转换流，要么 JDK 11 之后 FileReader 构造器直接传 Charset：`new FileReader("a.txt", StandardCharsets.UTF_8)`。

编码名写错（比如 "UTF-8" 写成 "UTF8" 部分平台能认，但 "GBK" 写错）抛 UnsupportedEncodingException。

## 打印流

PrintStream / PrintWriter，只有输出没有输入。`System.out` 就是 PrintStream，往控制台打；PrintWriter 往文件打：

```java
try (PrintWriter pw = new PrintWriter(new FileWriter("log.txt"), true)) {  // true=自动flush
    pw.println("2026-11-24 部署完成");
    pw.printf("进度：%d%%", 80);      // 支持 printf 格式化
}
```

特点：**println / printf 不抛 IOException**（内部吞了异常，可用 checkError() 查），所以写起来不用套 try-catch 每一行。println 底层是 `write(String) + newLine()`。

PrintWriter 带 autoFlush 参数（构造器第二个参数 true），每次 println 自动刷缓冲；FileWriter 是有缓冲的，不 flush/close 内容不落盘——上次基础篇记过这个坑，打印流这里用一个 autoFlush 参数解决。

## File 递归遍历：带过滤

基础篇写了纯递归列目录。实际需求通常是"只找 .java 文件""只算 .log 大小"，用 FilenameFilter 在遍历时过滤，不用自己判断后缀：

```java
File[] files = 目录.listFiles((dir, name) -> name.endsWith(".java"));
```

lambda 参数是 (File dir, String name)，返回 true 保留。另一个是 FileFilter，过滤的是 File 对象：`dir.listFiles(f -> f.isFile())`。

完整例子：递归统计某目录下所有 .java 文件的总行数：

```java
public static long 统计行数(File 目录) {
    long 总数 = 0;
    File[] files = 目录.listFiles();
    if (files == null) return 0;               // 没权限的目录返回null
    for (File f : files) {
        if (f.isDirectory()) {
            总数 += 统计行数(f);                // 目录递归进去
        } else if (f.getName().endsWith(".java")) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                while (br.readLine() != null) 总数++;
            } catch (IOException e) {
                System.out.println("跳过：" + f.getName());
            }
        }
    }
    return 总数;
}
```

递归删除目录也是同一结构：先递归删子项，再删自己（File.delete() 不能删非空目录）。

---

代码在同目录 `IO流进阶总结.java`，能直接跑（在当前目录建测试文件，跑完自动删）。
