//@author: Murali S Badiger

package Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class newthread extends Thread {

	public Socket soc;
	public String cid;
	public BufferedReader is;
	public PrintStream os;

	public newthread(Socket soc, String id) {
		this.soc = soc;
		this.cid = id;

	}

	public void run() {
		try {
			is = new BufferedReader(new InputStreamReader(this.soc.getInputStream()));
			os = new PrintStream(this.soc.getOutputStream());
			String ids = ChatServer.connectedclients(this.cid);
			os.println(ids);
			menu();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void chatsession(Socket soc1, Socket soc2, String id1, String id2) {
		try {
			Socket a = soc1;
			Socket b = soc2;
			BufferedReader ai = new BufferedReader(new InputStreamReader(a.getInputStream()));
			PrintStream bo = new PrintStream(b.getOutputStream());
			PrintStream ao = new PrintStream(a.getOutputStream());
			bo.println(id1 + " Requested to enter chat session. type \"okay\" to enter chat session. type @close anywhere during the chat to leave the chat session");
			synchronized (a) {
				while (true) {
					String msg = ai.readLine();
					if (msg.equals("@close")||b.isClosed()) {
						bo.println(id1 + " has left the chat");
						break;
					}
					bo.println("From " + id1 + ": " + msg);
					Connect_db ent = new Connect_db(id1, msg, id2);
				}
			}
			ao.println("Chat Ended. Select an option and press enter");
			control(a);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void joinsession(Socket soc1, Socket soc2, String id1, String id2) {
		try {
			Socket a = soc1;
			Socket b = soc2;
			BufferedReader ai = new BufferedReader(new InputStreamReader(a.getInputStream()));
			PrintStream bo = new PrintStream(b.getOutputStream());
			PrintStream ao = new PrintStream(a.getOutputStream());
			bo.println("Client Session is started with: " + id1+" type @close anywhere during the chat to leave the chat session");
			synchronized (a) {
				while (true) {
					String msg = ai.readLine();
					if (msg.equals("@close")||b.isClosed()) {
						bo.println(id1 + " has left the chat");
						break;
					}
					bo.println("From " + id1 + ": " + msg);
					Connect_db ent = new Connect_db(id1, msg, id2);
				}
			}
			ao.println("Chat Ended. Select an option and press enter");
			control(a);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void menu() {
		try {
			is = new BufferedReader(new InputStreamReader(this.soc.getInputStream()));
			os = new PrintStream(this.soc.getOutputStream());
			String ids = ChatServer.connectedclients(this.cid);
			os.println(
					" 1. type \"@open session\" to chat with a client\n 2. type \"@clients\" to display list of connected clients\n 3. type \"@sendmess\" to send a message\n 4. type \"history\" to see the previous message sent by you\n 5. type \"@close\" to disconnect from the server ");
			while (true) {
				String inp = is.readLine();
				switch (inp) {

				case "@open session":
					os.println("Enter the Client id you want to chat with");
					String rid = is.readLine();
					boolean f = ChatServer.clients.containsKey(rid);
					Set<String> l = new HashSet<>();
					l = ChatServer.clients.keySet();
					if (f == true) {
						Socket rclient = ChatServer.clients.get(rid);
						chatsession(this.soc, rclient, cid, rid);
					} else {
						os.println(
								"The client you selected is disconnected from the Server. Please Select a different client or exit");
						menu();
					}

					break;
				case "@clients":
					ids = ChatServer.connectedclients(this.cid);
					os.println(ids);
					os.println("Select an option and press enter:");
					control(this.soc);
					break;

				case "@sendmess":
					os.println("Enter the receiver's id");
					String rid1 = is.readLine().trim();
					Socket rclient = ChatServer.clients.get(rid1);
					os.println("Enter the message");
					String msg = is.readLine().trim();
					sendmess(rclient, this.cid, msg, rid1);
					break;

				case "okay":
					os.println("Enter receivers id:");
					String a = is.readLine();
					Socket tclient = ChatServer.clients.get(a);
					joinsession(this.soc, tclient, this.cid, a);

				case "@close":
					os.println("Bye");
					os.close();
					is.close();
					this.soc.close();
					ChatServer.clients.remove(this.cid, this.soc);
					break;

				case "history":
					try {
						os.println("Select an option and press Enter");
						os.println("1. Messages Sent");
						os.println("2. Messages Received");
						String opt1 = is.readLine();
						switch (opt1) {
						case "1":
							Connect_db hist = new Connect_db(this.cid);
							ResultSet rs = hist.retrive();
							os.println("Message sent by you");
							while (rs.next()) {
								os.println(
										"Message: \"" + rs.getString("message") + "\" sent to: " + rs.getString("sent_to"));
							}
							control(this.soc);
							break;
						case "2":
							Connect_db recd = new Connect_db(this.cid);
							ResultSet rs1 = recd.received();
							os.println("Messages received: ");
							while (rs1.next()) {
								os.println("Message: \"" + rs1.getString("message") + "\" received from: "
										+ rs1.getString("clientid"));
							}
							control(this.soc);
							break;
						default:
							os.println("Invalid Option");
							menu();
							break;
						}

					} catch (SQLException e) {
						e.printStackTrace();
					}
					control(this.soc);
					break;
				default:
					os.println("From Server: Invalid Option");
					menu();
					break;

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendmess(Socket soc2, String s, String m, String rid) {
		try {
			DataInputStream ai = new DataInputStream(soc2.getInputStream());
			PrintStream ao = new PrintStream(soc2.getOutputStream());
			ao.println("Froms: " + s + ": " + m);
			os.println("Message is sent");
			Connect_db db = new Connect_db(s, m, rid);
			control(this.soc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void control(Socket soc) {
		try {
			BufferedReader is1 = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			PrintStream os1 = new PrintStream(soc.getOutputStream());
			os1.println("1. Main Menu");
			os1.println("2. Exit from server");
			String opt = is1.readLine();
			switch (opt) {
			case "1":
				menu();
				break;

			case "2":
				os1.println("Bye");
				is1.close();
				os1.close();
				soc.close();
				ChatServer.clients.remove(this.cid, this.soc);
				break;

			default:
				os1.println("Invalid Option");
				control(this.soc);
				break;

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
