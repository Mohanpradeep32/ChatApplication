// package socketsExe;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

class MyServer {
	public static void main(String args[]) throws Exception {
		ServerSocket ss = new ServerSocket(6666);

		Thread tcon = new Thread(() -> {
			try {
				while (true) {
					Socket s = ss.accept();
					DataInputStream din = new DataInputStream(s.getInputStream());
					DataOutputStream dout = new DataOutputStream(s.getOutputStream());
					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
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

					Thread t2 = new Thread(() -> {
						String str = "", str2 = "";
						try {
							while (true) {

								str2 = din.readUTF();
								System.out.println("Server says: " + str2);
							}
						} catch (Exception e2) {
							// TODO: handle exception
						}
					});
					t2.start();

				}

			} catch (Exception e) {

			}

		});

		tcon.start();
		// String str = "", str2 = "";

	}
}