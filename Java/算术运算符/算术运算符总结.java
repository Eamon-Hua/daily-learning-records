//算术运算符的练习，坑都写在注释里了
//2026.11.15

class 算术运算符总结 {

	public static void main(String[] args) {

		//先来最基础的，注意 7/2 出来是 3，不是 3.5
		int a=7;
		int b=2;
		System.out.println(a+b);   //9
		System.out.println(a-b);   //5
		System.out.println(a*b);   //14
		System.out.println(a/b);   //3   小数部分直接砍掉，不四舍五入
		System.out.println(a%b);   //1

		//想要小数，得让其中一边先变成小数
		System.out.println(a/(double)b);   //3.5
		System.out.println((double)a/b);   //3.5
		System.out.println(a/b*1.0);       //3.0  这个坑我踩过，先算完除法了

		//取余的正负跟被除数（左边）一致，跟数学里不一样
		System.out.println(5%2);     //1
		System.out.println(-5%2);    //-1
		System.out.println(5%-2);    //1
		System.out.println(-5%-2);   //-1

		//判断奇偶、能不能整除就靠这个
		System.out.println(10%2==0);   //true
		System.out.println(15%5==0);   //true

		//小类型跟大类型一起算，结果变成大的
		int i=10;
		double d=3;
		System.out.println(i+d);   //13.0

		//char 参与运算会先变成 int，'a' 就是 97
		char c='a';
		System.out.println(c+1);          //98
		System.out.println((char)(c+1));  //b   想要字符得自己强转回来

		//byte 相加会自动升成 int，直接赋给 byte 编译不过
		byte x=1;
		byte y=2;
		//byte z=x+y;
		byte z=(byte)(x+y);
		System.out.println(z);   //3

		//浮点数算不准，这不是 Java 的问题
		System.out.println(0.1+0.2);        //0.30000000000000004
		System.out.println(0.1+0.2==0.3);   //false  所以别用 == 比小数

		//加号只要碰上一个字符串，就变拼接
		System.out.println("10"+20);      //1020
		System.out.println(10+20+"30");   //3030    从左往右，前面是数字先加
		System.out.println("10"+20+30);   //102030  一旦成字符串，后面全拼
		System.out.println('a'+1+"b");    //98b
		System.out.println(""+'a'+1);     //a1

		//自增，单独用没区别，参与运算才要注意
		int n=5;
		n++;
		System.out.println(n);   //6
		--n;
		System.out.println(n);   //5

		//后缀：先把原值交出去，自己再加
		int m=5;
		int r1=m++;
		System.out.println(r1+" "+m);   //5 6

		//前缀：先加，再把新值交出去
		int k=5;
		int r2=++k;
		System.out.println(r2+" "+k);   //6 6

		//面试题 i=i++，结果还是 10
		//右边先把 10 存起来，i 自己变 11，然后又把 10 赋回去，白加了
		int t=10;
		t=t++;
		System.out.println(t);   //10

		//换成前置就是 11
		int u=10;
		u=++u;
		System.out.println(u);   //11

		//+= 内部自带强制类型转换，所以 short 用 += 不报错
		short s=1;
		//s=s+1;   这行编译不过
		s+=1;
		System.out.println(s);   //2

		//byte 乘个小数也行，会先算完再强转回去
		byte s2=10;
		s2*=2.5;
		System.out.println(s2);   //25

		//溢出不会报错，直接绕回去
		int max=Integer.MAX_VALUE;
		System.out.println(max);           //2147483647
		System.out.println(max+1);         //-2147483648
		System.out.println((long)max+1);   //2147483648  先转 long 就好了

		//整数除 0 抛异常，浮点除 0 是 Infinity，别混
		//System.out.println(1/0);
		System.out.println(1.0/0);    //Infinity
		System.out.println(-1.0/0);   //-Infinity
		System.out.println(0.0/0);    //NaN

		//NaN 跟谁都不相等，自己也不等，判断要用 isNaN
		System.out.println(0.0/0==0.0/0);         //false
		System.out.println(Double.isNaN(0.0/0));  //true

		//优先级：括号最大，先乘除后加减，同级从左往右
		System.out.println(2+3*4);     //14
		System.out.println((2+3)*4);   //20
		System.out.println(10-3-2);    //5   不是 10-(3-2)
		System.out.println(-7/2);      //-3  往零取整

	}

}
