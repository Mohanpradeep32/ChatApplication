// package socketsExe;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

public class MyClientGUI extends Frame {
	private Socket socket;
	private DataInputStream din;
	private DataOutputStream dout;
	private TextField messageField;
	private TextArea chatArea;
	private String username;

	public MyClientGUI() {
		super("Chat Client");
		setupGUI();
		connectToServer();
		receiveMessages();
	}

	private void setupGUI() {
		setLayout(new BorderLayout());

		// Message input field
		messageField = new TextField();
		add(messageField, BorderLayout.NORTH);

		// Chat display area
		chatArea = new TextArea();
		chatArea.setEditable(false);
		add(chatArea, BorderLayout.CENTER);

		// Send button
		Button sendButton = new Button("Send");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		add(sendButton, BorderLayout.SOUTH);

		// Handle window closing event
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					if (socket != null) {
						dout.writeUTF(username + " has left the chat.");
						socket.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				System.exit(0);
			}
		});

		// Set frame properties
		setSize(400, 300);
		setVisible(true);
	}

	private void connectToServer() {
		try {
			socket = new Socket("localhost", 6666);
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream(socket.getOutputStream());

			// Ask for username
			username = getUsername();

			// Send username to server
			dout.writeUTF(username);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getUsername() {
		String name = JOptionPane.showInputDialog(this, "Enter your username:");
		if (name == null || name.trim().isEmpty()) {
			name = "Anonymous";
		}
		return name;
	}

	private void sendMessage() {
		try {
			String message = messageField.getText();
			dout.writeUTF(username + ": " + message);
			messageField.setText(""); // Clear input field
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void receiveMessages() {
		Thread receiveThread = new Thread(() -> {
			try {
				while (true) {
					String receivedMessage = din.readUTF();
					chatArea.append(receivedMessage + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		receiveThread.start();
	}

	public static void main(String[] args) {
		new MyClientGUI();
	}
}
