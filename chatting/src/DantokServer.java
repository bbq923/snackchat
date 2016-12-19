import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class DantokServer {
	HashMap clients;
	
	DantokServer() {
		clients = new HashMap();
		Collections.synchronizedMap(clients);
	}
	
	public void start(int port) {
		SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		SSLServerSocket sslServerSocket = null;
		SSLSocket sslSocket = null;
		
		try {
			sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
			System.out.println("서버가 시작되었습니다.");
			
			while(true) {
				sslSocket = (SSLSocket)sslServerSocket.accept();
				System.out.println("[" + sslSocket.getInetAddress() 
						+":"+sslSocket.getPort() + "]" + "에서 접속하였습니다.");
				ServerReceiver thread = new ServerReceiver(sslSocket);
				thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // start()
	
	void sendToAll(String msg) {
		Iterator it = clients.keySet().iterator(); 
		
		while(it.hasNext()) {
			try {
				DataOutputStream out = (DataOutputStream)clients.get(it.next());
				out.writeUTF(msg);
			} catch (IOException e) {}
		} // while
	} // sendToAll
	
	void sendToOne(SSLSocket sslSocket, String msg) {
		try {
			DataOutputStream out = new DataOutputStream(sslSocket.getOutputStream());
			out.writeUTF(msg);
		} catch (IOException e) {}
	}
	
	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("USAGE: java Dantokserver <port>");
			System.exit(0);
		}
		new DantokServer().start(new Integer(args[0]));
	}

	class ServerReceiver extends Thread {
		SSLSocket sslSocket;
		DataInputStream in;
		DataOutputStream out;
		
		ServerReceiver(SSLSocket sslSocket) {
			this.sslSocket = sslSocket;
			try {
				in = new DataInputStream(sslSocket.getInputStream());
				out = new DataOutputStream(sslSocket.getOutputStream());
			} catch (IOException e) {}
		}
		
		public void run() {
			String name = "";
			String notify = "이 방에는 "; // 현재 단톡방에 참가중인 사람들의 이름을 담은 문자열
			
			try {
				name = in.readUTF();
				sendToAll("#" + name + "님이 들어오셨습니다.");
				
				if (clients.size() > 0) { // 방에 누군가 있을 때 누가 있는지를 참가자에게만 알려준다
					for (Object userName : clients.keySet()) {
						notify = notify + (String)userName + ", ";
						
					}
					notify += "님이 있습니다.";
					sendToOne(sslSocket, notify);
				}
				clients.put(name, out);
				System.out.println("현재 서버접속자 수는 " + clients.size() + "입니다.");
				
				
				while (in != null) {
					sendToAll(in.readUTF());
				}
			} catch (IOException e) {
				// ignore
			} finally {
				sendToAll("#" + name + "님이 나가셨습니다.");
				clients.remove(name);
				System.out.println("[" + sslSocket.getInetAddress() + ":" + sslSocket.getPort() + "]" + "에서 접속을 종료하였습니다.");
				System.out.println("현재 서버접속자 수는 " + clients.size() + "입니다.");
			}
		}
	}
}
