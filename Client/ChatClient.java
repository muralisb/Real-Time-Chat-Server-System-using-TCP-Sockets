//@author: Rakshita Vibhu
//

package Client;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.net.UnknownHostException;

public class ChatClient implements Runnable {

	/*
	 * thread to read from the server. Read message from server. Continue reading
	 * until "Bye" is received.
	 */
	public void run() {

		String responseLine;
		try {
			while ((responseLine = inputstream.readLine()) != null) {
				System.out.println(responseLine);
				if (responseLine.indexOf("*** Bye") != -1)
					break;
			}
			closed = true;
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		}
	}

	private static Socket clientSocket = null;
	private static PrintStream outputstream = null;
	private static DataInputStream inputstream = null;

	private static BufferedReader inputLine = null;
	private static boolean closed = false;

	public static void main(String[] args) {

		int portNumber = 3333;
		String host = "localhost";

		// System.out.println("Host=" + host + ", PortNumber=" + portNumber);

		/*
		 * Open a socket.Open input and output streams.
		 */
		try {
			clientSocket = new Socket(host, portNumber);
			inputLine = new BufferedReader(new InputStreamReader(System.in));
			outputstream = new PrintStream(clientSocket.getOutputStream());
			inputstream = new DataInputStream(clientSocket.getInputStream());
			System.out.println("Connection Established");
		} catch (UnknownHostException e) {
			System.err.println("Server not found" + host);
		} catch (IOException e) {
			System.err.println("Server Down");
		}

		/*
		 * Write data to the socket.
		 */
		if (clientSocket != null && outputstream != null && inputstream != null) {
			try {

				/* Thread to read from the server. */
				new Thread(new ChatClient()).start();
				while (!closed) {
					String a = inputLine.readLine().trim();
					outputstream.println(a);
				}

				/*
				 * Close the input stream, close the output stream, close the socket.
				 */
				outputstream.close();
				inputstream.close();
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}
}