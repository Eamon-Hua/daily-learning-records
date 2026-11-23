//IO流基础的练习，File、字节流、字符流、缓冲流都过了一遍
//2026.11.23

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class IO流基础总结 {
	public static void main(String[] args) throws IOException {
		//File只是个路径，建文件先建目录
		File 目录=new File("io_练习");
		if(!目录.exists()) {
			目录.mkdirs();
		}
		File 文件=new File(目录,"测试.txt");
		文件.createNewFile();
		System.out.println("存在:"+文件.exists()+"，是文件:"+文件.isFile());

		//字节流：一个字节一个字节搬，万能但慢
		try(FileOutputStream out=new FileOutputStream(文件)) {
			out.write("hello IO 你好".getBytes());
		}
		try(FileInputStream in=new FileInputStream(文件)) {
			int b;
			System.out.print("按字节读:");
			while((b=in.read())!=-1) {
				System.out.print((char)b);	//中文会花，字节流不认编码
			}
			System.out.println();
		}

		//字符流：按字符搬，中文不乱码，Writer有缓冲要close才落盘
		File 文本=new File(目录,"中文.txt");
		try(FileWriter w=new FileWriter(文本)) {
			w.write("第一行");
			w.write("第二行");
		}

		//缓冲流包节点流，readLine一次一行最常用
		try(BufferedReader br=new BufferedReader(new FileReader(文本))) {
			String line;
			int 行号=1;
			while((line=br.readLine())!=null) {	//null就是读完了
				System.out.println("第"+(行号++)+"行:"+line);
			}
		}

		//BufferedWriter+newLine写多行，追加模式构造器第二个参数传true
		File 追加=new File(目录,"中文.txt");
		try(BufferedWriter bw=new BufferedWriter(new FileWriter(追加,true))) {
			bw.write("追加的一行");
			bw.newLine();			//跨平台的换行
			bw.write("再来一行");
		}

		//字节流配缓冲，复制文件的标准写法
		File 备份=new File(目录,"中文备份.txt");
		try(FileInputStream in=new FileInputStream(追加);
			FileOutputStream out=new FileOutputStream(备份)) {
			byte[] buf=new byte[1024];
			int len;
			while((len=in.read(buf))!=-1) {
				out.write(buf,0,len);	//读多少写多少，别写整个buf
			}
		}
		System.out.println("备份大小:"+备份.length()+"字节");

		//递归遍历目录：是目录就进去，不是就处理
		System.out.println("目录里有什么:");
		列出来(目录,1);

		//收尾：把练习文件全删了，跑几遍都不留垃圾
		删干净(目录);
	}

	//递归遍历，listFiles对没权限目录返回null，判一下防炸
	public static void 列出来(File 目录,int 层) {
		File[] files=目录.listFiles();
		if(files==null) return;
		for(File f:files) {
			System.out.println("  ".repeat(层)+f.getName());
			if(f.isDirectory()) {
				列出来(f,层+1);
			}
		}
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
