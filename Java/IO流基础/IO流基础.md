# Java IO 流基础

记于 2026.11.23

IO 流用于在程序和文件之间传输数据。先分清三组概念，名字就都看得懂了：

- 输入/输出：以**程序**为参照，读进来是输入流，写出去是输出流
- 字节/字符：字节流管一切（图片、视频、文本都能搬），字符流只管文本（内部帮你处理了编码）
- 节点/处理流：节点流直接接数据源（文件），处理流包在别的流外面加功能（缓冲）

## File：先认识文件

File 代表的是**路径**，不是文件内容。存在不存在、是不是目录，得查：

```java
File f = new File("测试目录/测试.txt");
f.exists();          // 存在吗
f.isFile();          // 是文件吗
f.isDirectory();     // 是目录吗
f.length();          // 字节数
f.delete();          // 删
f.mkdirs();          // 建目录（多级）
f.createNewFile();   // 建文件
```

列目录：`f.list()` 拿文件名数组，`f.listFiles()` 拿 File 对象数组。递归遍历目录就是自己调自己：是目录就进去接着 listFiles，不是就处理，注意加个判断防 null（没权限的目录返回 null）。

## 字节流

InputStream / OutputStream 是抽象父类，文件操作用 FileInputStream / FileOutputStream。read() 一次读一个字节，返回 -1 表示读完：

```java
try (FileInputStream in = new FileInputStream("源.txt");
     FileOutputStream out = new FileOutputStream("目标.txt")) {
    int b;
    while ((b = in.read()) != -1) {
        out.write(b);
    }
}
```

**try-with-resources 必须用**：流是资源，用完必须 close。写在 try 的括号里，语句结束时自动调用 close（流都实现了 AutoCloseable），不会遗漏。

一个字节一个字节读太慢，实际都配缓冲流（下面说）。另一种提速方式是读进 byte 数组：

```java
byte[] buf = new byte[1024];
int len;
while ((len = in.read(buf)) != -1) {
    out.write(buf, 0, len);    // 读多少写多少，别写整个buf
}
```

## 字符流：文本专用

Reader / Writer 按字符传输，能指定编码，中文不会乱码：

```java
try (FileReader r = new FileReader("a.txt");
     FileWriter w = new FileWriter("b.txt", true)) {   // true=追加模式
    int c;
    while ((c = r.read()) != -1) {
        w.write(c);
    }
}
```

Writer 的坑：它自带一个小缓冲区，数据小的时候**先攒着不落盘**，close 之前可能什么都没写进去。所以用完必须 close 或 flush。

## 缓冲流：套在外面提速

BufferedInputStream / BufferedReader 包在节点流外面，内部一个 8K 的数组，攒一批再读写，IO 次数少了自然快：

```java
try (BufferedReader br = new BufferedReader(new FileReader("a.txt"))) {
    String line;
    while ((line = br.readLine()) != null) {   // 一行一行读，最常用
        System.out.println(line);
    }
}
```

BufferedReader.readLine() 一次读一行（不含换行符，返回 null 是读完了），BufferedWriter.newLine() 写换行——文本处理基本就靠这两个。

包装的方向别搞反：**处理流包节点流，节点流不包处理流**。

---

代码在同目录 `IO流基础总结.java`，能直接跑（会在当前目录建几个测试文件，跑完自动删）。
