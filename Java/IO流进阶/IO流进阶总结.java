//IO流进阶的练习，序列化流、转换流、打印流、File递归过滤都过了一遍
//2026.11.24

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class IO流进阶总结 {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		File 目录=new File("io进阶_练习");
		if(!目录.exists()) {
			目录.mkdirs();
		}

		//序列化：类要实现Serializable，显式写serialVersionUID，类改了也能读
		File 对象文件=new File(目录,"student.obj");
		try(ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(对象文件))) {
			ArrayList<学生> 名单=new ArrayList<>();
			名单.add(new 学生("张三",20));
			名单.add(new 学生("李四",22));
			名单.add(new 学生("王五",19));
			out.writeObject(名单);	//直接序列化集合，元素也必须可序列化
		}
		try(ObjectInputStream in=new ObjectInputStream(new FileInputStream(对象文件))) {
			@SuppressWarnings("unchecked")
			ArrayList<学生> 名单=(ArrayList<学生>)in.readObject();
			for(学生 s:名单) {
				//transient的密码读出来是null，static的count是类加载时的值
				System.out.println(s.名字+" "+s.年龄+" 密码:"+s.密码+" 计数:"+学生.计数);
			}
		}

		//转换流：读GBK文件，FileReader默认编码读不了GBK
		File gbk文件=new File(目录,"gbk.txt");
		try(OutputStreamWriter w=new OutputStreamWriter(new FileOutputStream(gbk文件),"GBK")) {
			w.write("这是GBK编码的内容");
		}
		try(BufferedReader br=new BufferedReader(
				new InputStreamReader(new FileInputStream(gbk文件),"GBK"))) {
			System.out.println("GBK读出:"+br.readLine());
		}
		//再用UTF-8读同一个文件，验证乱码
		try(BufferedReader br=new BufferedReader(
				new InputStreamReader(new FileInputStream(gbk文件),StandardCharsets.UTF_8))) {
			System.out.println("UTF-8读GBK会乱:"+br.readLine());
		}
		//JDK11+的FileReader可以直接传Charset，注意参数是Charset不是字符串
		try(FileReader r=new FileReader(gbk文件,java.nio.charset.Charset.forName("GBK"))) {
			int c=r.read();
			System.out.println("FileReader带GBK参数,首字符:"+((char)c));
		}

		//打印流：println不抛IOException，autoFlush=true不用手动flush
		File 日志=new File(目录,"log.txt");
		try(PrintWriter pw=new PrintWriter(new FileWriter(日志),true)) {
			pw.println("2026-11-24 部署完成");
			pw.printf("进度：%d%%", 80);
		}
		try(BufferedReader br=new BufferedReader(new FileReader(日志))) {
			System.out.println("日志内容:");
			String line;
			while((line=br.readLine())!=null) {
				System.out.println("  "+line);
			}
		}

		//File递归过滤：FilenameFilter只留.java，统计行数
		File src=new File(目录,"src");
		src.mkdirs();
		try(OutputStreamWriter w=new OutputStreamWriter(new FileOutputStream(new File(src,"A.java")),"UTF-8")) {
			w.write("class A{\n}\nint x;\n");
		}
		new File(src,"B.txt").createNewFile();	//非java文件，不该被统计
		File[] javaFiles=src.listFiles((dir,name)->name.endsWith(".java"));
		System.out.println("过滤出"+javaFiles.length+"个java文件");
		System.out.println("java总行数:"+统计行数(src));

		//递归删除：File.delete()删不了非空目录，先删子项再删自己
		删干净(目录);
	}

	//递归统计.java行数，listFiles对没权限目录返回null，判一下
	public static long 统计行数(File 目录) {
		long 总数=0;
		File[] files=目录.listFiles();
		if(files==null) return 0;
		for(File f:files) {
			if(f.isDirectory()) {
				总数+=统计行数(f);
			}else if(f.getName().endsWith(".java")) {
				try(BufferedReader br=new BufferedReader(new FileReader(f))) {
					while(br.readLine()!=null) 总数++;
				}catch(IOException e) {
					System.out.println("跳过:"+f.getName());
				}
			}
		}
		return 总数;
	}

	public static void 删干净(File 目录) {
		File[] files=目录.listFiles();
		if(files!=null) {
			for(File f:files) {
				if(f.isDirectory()) {
					删干净(f);
				}
				f.delete();
			}
		}
		目录.delete();
	}
}

class 学生 implements Serializable {
	private static final long serialVersionUID = 1L;	//固定版本号，类改动后仍能反序列化
	static int 计数 = 100;			//static不序列化，读出来是当前类加载的值
	String 名字;
	int 年龄;
	transient String 密码 = null;	//transient不落盘，读出来是默认值null

	学生(String 名字, int 年龄) {
		this.名字 = 名字;
		this.年龄 = 年龄;
	}
}
