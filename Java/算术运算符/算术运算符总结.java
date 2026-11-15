/*
 * 知识点：算术运算符
 *
 * 一共就这几个：+ - * / % ++ --
 * 看着简单，坑不少：
 *   1. 两个整数相除会丢小数
 *   2. 取模的正负跟被除数走
 *   3. 类型会自动提升
 *   4. 浮点数算不准
 *   5. + 碰上字符串就变成拼接
 *   6. ++ 在前在后结果不一样
 *   7. i=i++ 这种面试题
 *   8. += 自带强制类型转换
 *   9. 溢出会绕回去
 *  10. 整数除零抛异常，浮点除零是 Infinity
 *
 * 下面每一行都标了实际输出，可以直接对着看
 */

class 算术运算符总结 {

	public static void main(String[] args) {

		//=========== 一、加减乘除取模：最基本的五个 ===========

		int a=7;
		int b=2;

		System.out.println(a+b);   //9    加法
		System.out.println(a-b);   //5    减法
		System.out.println(a*b);   //14   乘法
		System.out.println(a/b);   //3    除法：两个整数相除，小数部分直接砍掉，不是四舍五入
		System.out.println(a%b);   //1    取模，就是求余数

		//想得到小数结果，必须有一边是浮点型，否则先按整数算完了再赋值也晚了
		System.out.println(a/(double)b);   //3.5
		System.out.println((double)a/b);   //3.5
		System.out.println(a/b*1.0);       //3.0  坑：先算了 7/2=3，再乘 1.0

		//=========== 二、取模的正负：只看被除数 ===========

		//记住一句话：结果的符号跟左边（被除数）一致，跟右边没关系
		System.out.println(5%2);     //1
		System.out.println(-5%2);    //-1   左边是负的，结果就是负的
		System.out.println(5%-2);    //1    右边是负的，不影响，结果还是正的
		System.out.println(-5%-2);   //-1

		//常用场景：判断奇偶、判断能不能整除
		System.out.println(10%2==0);   //true  偶数
		System.out.println(15%5==0);   //true  能整除

		//=========== 三、类型提升：小的跟大的算，结果变成大的 ===========

		int i=10;
		double d=3;
		System.out.println(i+d);   //13.0   int 先自动转成 double 再相加

		char c='a';
		System.out.println(c+1);          //98   char 会先提升成 int，'a' 就是 97
		System.out.println((char)(c+1));  //b    想变回字符得自己强转

		byte x=1;
		byte y=2;
		//byte z=x+y;        这行编译不过：byte 相加会自动升成 int
		byte z=(byte)(x+y);  //所以要强转回来
		System.out.println(z);   //3

		//=========== 四、浮点数算不准：这是常识，不是 bug ===========

		System.out.println(0.1+0.2);         //0.30000000000000004
		System.out.println(0.1+0.2==0.3);    //false  所以千万别用 == 比浮点数
		//真要算钱用 BigDecimal，讲精度那块会细说

		//=========== 五、加号的第二职业：碰上字符串就变成拼接 ===========

		//规则：只要 + 的两边有一个是字符串，这个 + 就是拼接，不是加法
		System.out.println("10"+20);      //1020    数字被转成字符串接上去
		System.out.println(10+20+"30");   //3030    从左往右：先算 10+20=30，再拼 "30"
		System.out.println("10"+20+30);   //102030  先拼成 "1020"，再拼 "30"
		System.out.println('a'+1+"b");    //98b     char+int 先算加法得 98，再拼 "b"
		System.out.println(""+'a'+1);     //a1      前面已经是字符串了，后面全拼

		//=========== 六、自增自减：单独用没区别，参与运算就有讲究 ===========

		int n=5;
		n++;                   //自己加 1
		System.out.println(n); //6
		++n;                   //还是加 1，位置无所谓
		System.out.println(n); //7
		n--;                   //自己减 1
		System.out.println(n); //6

		//后缀 m++：先把原来的值交出去，然后自己再加 1
		int m=5;
		int r1=m++;
		System.out.println(r1+" "+m);   //5 6

		//前缀 ++k：先自己加 1，再把新值交出去
		int k=5;
		int r2=++k;
		System.out.println(r2+" "+k);   //6 6

		int p=5;
		System.out.println(p++);   //5   打印的是加之前的值
		System.out.println(p);     //6   这时才看到加完的结果

		int q=5;
		System.out.println(++q);   //6   先加，再打印

		//=========== 七、经典面试题：i=i++ 等于几 ===========

		int t=10;
		t=t++;
		System.out.println(t);   //10   不是 11

		//拆开看就明白了：
		//第一步 右边的 t++ 把 10 这个值先存起来，t 自己变成 11
		//第二步 把存起来的 10 赋回给 t，t 又变回 10
		//所以自增白干了一场

		int u=10;
		u=++u;
		System.out.println(u);   //11   前缀是先加再赋值，这次没白干

		//=========== 八、复合赋值：+= -= *= /= %= 自带强转 ===========

		int s1=10;
		s1+=5;                  //等价于 s1=s1+5
		System.out.println(s1); //15

		short s2=1;
		//s2=s2+1;   编译不过：short+int 结果是 int，不能直接塞回 short
		s2+=1;        //编译能过：+= 内部偷偷做了强制类型转换
		System.out.println(s2);  //2

		byte s3=10;
		s3*=2.5;               //先算 25.0，再强转成 byte
		System.out.println(s3);  //25

		//=========== 九、溢出：装不下就绕回去 ===========

		int max=Integer.MAX_VALUE;
		System.out.println(max);     //2147483647
		System.out.println(max+1);   //-2147483648  最大值加 1 直接变最小值，不会报错

		int min=Integer.MIN_VALUE;
		System.out.println(min-1);   //2147483647  最小值减 1 变最大值

		//想防溢出用 long，或者 Math.addExact 这种会抛异常的方法
		System.out.println((long)max+1);   //2147483648  先转 long 就不会溢出

		//=========== 十、除零：整数和浮点完全两码事 ===========

		//System.out.println(1/0);   整数除零直接抛异常 ArithmeticException: / by zero
		System.out.println(1.0/0);    //Infinity    浮点除零是无穷大，不报错
		System.out.println(-1.0/0);   //-Infinity
		System.out.println(0.0/0);    //NaN         零除零是"不是数"

		//判断要用 Double.isNaN，不能用 == 比，因为 NaN 连自己都不等于
		System.out.println(0.0/0==0.0/0);            //false
		System.out.println(Double.isNaN(0.0/0));     //true

		//=========== 十一、优先级：括号最大，先乘除后加减 ===========

		System.out.println(2+3*4);     //14   先算乘法
		System.out.println((2+3)*4);   //20   括号优先
		System.out.println(10-3-2);    //5    同级从左往右算，不是 10-(3-2)
		System.out.println(2+3%2);     //3    % 和 * / 同级，比 + 高

		//负数参与取模容易看晕，写的时候加括号最保险
		System.out.println(-7/2);      //-3   向零取整，不是 -4
		System.out.println(-7%2);      //-1

	}

}
