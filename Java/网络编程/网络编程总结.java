//网络编程的练习，TCP Socket、UDP DatagramSocket 走 127.0.0.1 回环
//2026.11.25

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class 网络编程总结 {
	public static void main(String[] args) throws IOException, InterruptedException {
		//TCP：服务端在子线程监听18080，客户端连接发消息收回复
		Thread 服务端 = new Thread(() -> {
			try (ServerSocket server = new ServerSocket(18080)) {
				Socket socket = server.accept();	//阻塞等连接
				BufferedReader in = new BufferedReader(
					new InputStreamReader(socket.getInputStream(), "UTF-8"));
				PrintWriter out = new PrintWriter(
					new java.io.OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
				String line;
				while((line = in.readLine()) != null) {	//按行读，约定换行符为边界
					if("退出".equals(line)) break;
					out.println("回复:" + line);
				}
				socket.close();
			} catch(IOException e) {
				System.out.println("服务端异常:" + e.getMessage());
			}
		});
		服务端.start();
		Thread.sleep(300);	//等服务端监听起来

		try (Socket socket = new Socket("127.0.0.1", 18080)) {
			BufferedReader in = new BufferedReader(
				new InputStreamReader(socket.getInputStream(), "UTF-8"));
			PrintWriter out = new PrintWriter(
				new java.io.OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
			out.println("你好服务器");
			System.out.println("TCP收到:" + in.readLine());
			out.println("退出");	//约定结束标记
		}
		服务端.join();

		//UDP：发送方打一包到18081，接收方收到并回显来源地址
		Thread 接收方 = new Thread(() -> {
			try (DatagramSocket ds = new DatagramSocket(18081)) {
				byte[] buf = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				ds.receive(packet);	//阻塞等包
				String msg = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
				System.out.println("UDP收到:" + msg + " 来自:"
					+ packet.getAddress() + ":" + packet.getPort());
				//回一包
				byte[] resp = "收到".getBytes("UTF-8");
				ds.send(new DatagramPacket(resp, resp.length,
					packet.getAddress(), packet.getPort()));
			} catch(IOException e) {
				System.out.println("接收方异常:" + e.getMessage());
			}
		});
		接收方.start();
		Thread.sleep(300);

		try (DatagramSocket ds = new DatagramSocket()) {
			byte[] data = "UDP你好".getBytes("UTF-8");
			ds.send(new DatagramPacket(data, data.length,
				InetAddress.getByName("127.0.0.1"), 18081));
			byte[] rbuf = new byte[1024];
			DatagramPacket resp = new DatagramPacket(rbuf, rbuf.length);
			ds.receive(resp);	//等回包
			System.out.println("UDP回包:" + new String(resp.getData(), 0, resp.getLength(), "UTF-8"));
		}
		接收方.join();
	}
}
