// package socketsExe;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MyClient {
	public static void main(String args[]) throws Exception {
		Socket s = new Socket("localhost", 6666);
		System.out.println("Connection Established");
		DataInputStream din = new DataInputStream(s.getInputStream());
		DataOutputStream dout = new DataOutputStream(s.getOutputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String userInput;

		Thread t = new Thread(() -> {
			String str;
			while (true) {
				try {
					str = br.readLine();
					dout.writeUTF(str);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();

		String str = "", str2 = "";
		while (true) {

			str2 = din.readUTF();
			System.out.println("Server says: " + str2);
		}
	}
}
