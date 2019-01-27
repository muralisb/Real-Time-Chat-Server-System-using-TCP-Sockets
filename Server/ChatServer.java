//@author: Murali S Badiger


package Server;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatServer {

	public static int i = 0;
	private static final int port = 3333;
	public static Map<String, Socket> clients = new HashMap<String, Socket>();
	public Thread[] t = new Thread[10];
	public Socket soc = new Socket();
	public String id = "";

	public static ServerSocket socserver = null;

	public ChatServer() throws IOException {
		try {
			socserver = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Server Started");
	}

	/*
	 * Asks client for the id and calls newthread() function if client ID is not in
	 * the list of connected ids. else rejects the connection.
	 * 
	 * @param soc: Socket of client requesting to connect
	 */
	public void checkid(Socket s1) throws IOException {
		Socket a = s1;
		String id;
		try {
			BufferedReader is = new BufferedReader(new InputStreamReader(a.getInputStream()));
			PrintStream os = new PrintStream(a.getOutputStream());
			os.println("Type your id");
			while (true) {
				id = is.readLine().trim();
				boolean flag = clients.containsKey(id);
				if (flag == false) {
					os.println("OK");
					clients.put(id, a);
					new newthread(a, id).start();
					return;
				} else {
					os.println("Already connected");
					a.close();
					return;
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/*
	 * returns the list of clients connected to server
	 * 
	 * @param s: String of client ids connected to server.
	 */
	public static String connectedclients(String str) {
		StringBuilder cids = new StringBuilder();
		cids.append("Clients Connected: \n");
		Set<String> l = new HashSet<>();
		l = clients.keySet();
		for (String s : l) {
			if (!s.equals(str)) {
				cids.append(s + "\n");
			}

		}
		return cids.toString();
	}

	public static void main(String[] args) throws IOException {
		ChatServer def = new ChatServer();
		Socket m = null;
		while (true) {
			Socket mainsoc = socserver.accept();
			m = mainsoc;
			def.checkid(m);

		}

	}
}
