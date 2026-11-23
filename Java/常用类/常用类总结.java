//常用类的练习，String、StringBuilder、java.time都过了一遍
//2026.11.19

public class 常用类总结 {
	public static void main(String[] args) {
		//String不可变：拼接是造新对象
		String s="abc";
		String s2=s+"d";
		System.out.println(s==s2);	//false，s2是新对象

		//常量池共享，new出来的在堆里，==比地址equals比内容
		String a="abc";
		String b=new String("abc");
		System.out.println(a==b);		//false
		System.out.println(a.equals(b));	//true，比内容用这个

		//常用方法
		String str="hello,java,world";
		System.out.println(str.length());			//17
		System.out.println(str.charAt(0));			//h
		System.out.println(str.indexOf("java"));		//6
		System.out.println(str.substring(6,10));		//java，包头不包尾
		System.out.println(str.replace("java","python"));	//hello,python,world
		System.out.println(str.toUpperCase());			//HELLO,JAVA,WORLD

		//split的坑：.是正则的任意字符，必须转义
		String ip="192.168.1.1";
		System.out.println(ip.split("\\.").length);	//4，写成split(".")就完蛋
		String list="a,b,c";
		String[] parts=list.split(",");
		System.out.println(parts[1]);			//b

		//循环拼接用StringBuilder，String每圈都造新对象
		StringBuilder sb=new StringBuilder();
		sb.append("编号").append("-").append(1001).append("-").append(true);
		System.out.println(sb.toString());		//编号-1001-true
		sb.reverse();
		System.out.println(sb);				//eurt-1001-号编

		//java.time：LocalDate只管日期
		java.time.LocalDate d=java.time.LocalDate.now();
		java.time.LocalDate 生日=java.time.LocalDate.of(2005,2,8);
		System.out.println(d.isAfter(生日));		//true
		System.out.println(生日.getYear());		//2005
		System.out.println(d.plusDays(7));		//一周后，返回新对象

		//LocalDateTime带时间，格式化用DateTimeFormatter
		java.time.LocalDateTime t=java.time.LocalDateTime.now();
		java.time.format.DateTimeFormatter f=java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String 时间串=t.format(f);
		System.out.println(时间串);
		//字符串转回来格式必须完全对上，MM是月mm是分钟别写混
		java.time.LocalDateTime 回去=java.time.LocalDateTime.parse(时间串,f);
		System.out.println(回去.getHour());
	}
}
