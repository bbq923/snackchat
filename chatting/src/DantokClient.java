import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.rmi.ConnectException;
import java.util.Scanner;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class DantokClient {
	public static void main (String args[]) {
		if (args.length != 3) {
			System.out.println("USAGE: java DantokClient <host> <port> <id>");
			System.exit(0);
		}
		
		try {
			String serverIp = args[0];
			SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket sslSocket =  (SSLSocket)sslSocketFactory.createSocket(serverIp, new Integer(args[1]));
			System.out.println("서버에 연결되었습니다.");
			Thread sender = new Thread(new ClientSender(sslSocket, args[2]));
			Thread receiver = new Thread(new ClientReceiver(sslSocket));
			
			sender.start();
			receiver.start();
		} catch (ConnectException ce) {
			ce.printStackTrace();
		} catch (Exception e) {}
	}
	
	static class ClientSender extends Thread {
		SSLSocket sslSocket;
		DataOutputStream out;
		String name;
		
		ClientSender(SSLSocket sslSocket, String name) {
			this.sslSocket = sslSocket;
			try {
				out = new DataOutputStream(sslSocket.getOutputStream());
				this.name = name;
			} catch (Exception e) {}
		}
		
		public void run() {
			Scanner scanner = new Scanner(System.in);
			try {
				if (out != null) {
					out.writeUTF(name);
				}
				
				while (out != null) {
					out.writeUTF("[" + name + "]" + scanner.nextLine());
				}
			} catch (IOException e) {}
		} // run()
	} // ClientSender
	
	static class ClientReceiver extends Thread {
		SSLSocket sslSocket;
		DataInputStream in;
		
		ClientReceiver(SSLSocket sslSocket) {
			this.sslSocket = sslSocket;
			try {
				in = new DataInputStream(sslSocket.getInputStream());
			} catch (IOException e) {}
		}
		
		public void run() {
			while (in != null) {
				try {
					System.out.println(in.readUTF());
				} catch (IOException e) {}
			}
		}
	}
	
	
}
