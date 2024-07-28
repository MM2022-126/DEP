import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Color;
import java.awt.SystemColor;


public class Client extends JFrame {

	
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private JTextField Type_Message;
	private JList ActiveUsers;
	private JTextArea Chat_Box;
	private JButton End_User_Btn;
	private JRadioButton One_to_N_Btn;
	private JRadioButton BroadCast_Btn;

	DataInputStream inputStream;
	DataOutputStream outStream;
	DefaultListModel<String> DM;
	String ID, ClientIDs = "";
	private JLabel lblNewLabel_2;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					Client window = new Client();
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//		    }
//		});
//	}

	/**
	 * Create the application.
	 */

	public Client() {
		initialize();
	}

	public Client(String ID, Socket s) { // constructor call, it will initialize required variables
		initialize(); // Initialize UI components
		this.ID = ID;
		try {
			frame.setTitle("Client View - " + ID); // set title of UI
			DM = new DefaultListModel<String>(); // default list used for showing active users on UI
			ActiveUsers.setModel(DM);// show that list on UI component JList named ActiveUsers
			inputStream = new DataInputStream(s.getInputStream()); // Initialize input and output stream
			outStream = new DataOutputStream(s.getOutputStream());
			new Read().start(); // create a new thread for reading the messages
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	class Read extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					String msg = inputStream.readUTF();  // read message from server, this will contain :;.,/=<comma seperated ClientsIDs>
					System.out.println("Inside read thread = " + msg); // print message for testing purpose
					if (msg.contains(":;.,/=")) { // prefix(i know its random)
						msg = msg.substring(6); // comma separated all active user IDs
						DM.clear(); // clear the list before inserting fresh elements
						StringTokenizer st = new StringTokenizer(msg, ","); // split all the ClientIDs and add to DM below
						while (st.hasMoreTokens()) {
							String usr = st.nextToken();
							if (!ID.equals(usr)) // we do not need to show own user ID in the active user list pane
								DM.addElement(usr); // add all the active user IDs to the defaultList to display on active
													// user pane on Client view
						}
					} else {
						Chat_Box.append("" + msg + "\n"); //otherwise print on the Clients message board
					}
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() { // initialize all the components of UI
		frame = new JFrame();
		frame.setBounds(100, 100, 926, 705);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Client View");
		frame.getContentPane().setLayout(null);

		Chat_Box = new JTextArea();
		Chat_Box.setBounds(12, 75, 530, 445);
		Chat_Box.setFont(new Font("Times New Roman", Font.BOLD, 14));
		Chat_Box.setEditable(false);
		frame.getContentPane().add(Chat_Box);

		Type_Message = new JTextField();
		Type_Message.setBounds(126, 533, 416, 84);
		Type_Message.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
		Type_Message.setHorizontalAlignment(SwingConstants.LEFT);
		frame.getContentPane().add(Type_Message);
		Type_Message.setColumns(10);

		JButton Send_Btn = new JButton("Send");
		Send_Btn.setBounds(554, 533, 137, 84);
		Send_Btn.setBackground(SystemColor.textHighlight);
		Send_Btn.setForeground(new Color(0, 0, 0));
		Send_Btn.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
		Send_Btn.addActionListener(new ActionListener() { // action to be taken on send message button
			public void actionPerformed(ActionEvent e) {
				String textAreaMessage = Type_Message.getText(); // get the message from textbox
				if (textAreaMessage != null && !textAreaMessage.isEmpty()) {  // only if message is not empty then send it further otherwise do nothing
					try {
						String messageToBeSentToServer = "";
						String cast = "broadcast"; // this will be an IDentifier to IDentify type of message
						int flag = 0; // flag used to check whether used has selected any Client or not for multicast 
						if (One_to_N_Btn.isSelected()) { // if 1-to-N is selected then do this
							cast = "multicast"; 
							List<String> ClientList = ActiveUsers.getSelectedValuesList(); // get all the users selected on UI
							if (ClientList.size() == 0) // if no user is selected then set the flag for further use
								flag = 1;
							for (String selectedUsr : ClientList) { // append all the usernames selected in a variable
								if (ClientIDs.isEmpty())
									ClientIDs += selectedUsr;
								else
									ClientIDs += "," + selectedUsr;
							}
							messageToBeSentToServer = cast + ":" + ClientIDs + ":" + textAreaMessage; // prepare message to be sent to server
						} else {
							messageToBeSentToServer = cast + ":" + textAreaMessage; // in case of broadcast we don't need to know userIDs
						}
						if (cast.equalsIgnoreCase("multicast")) { 
							if (flag == 1) { // for multicast check if no user was selected then prompt a message dialog
								JOptionPane.showMessageDialog(frame, "No user selected");
							} else { // otherwise just send the message to the user
								outStream.writeUTF(messageToBeSentToServer);
								Type_Message.setText("");
								Chat_Box.append(" You sent msg to " + ClientIDs + "---->" + textAreaMessage + "\n"); //show the sent message to the sender's message board
							}
						} else { // in case of broadcast
							outStream.writeUTF(messageToBeSentToServer);
							Type_Message.setText("");
							Chat_Box.append(" You sent msg to All ---->" + textAreaMessage + "\n");
						}
						ClientIDs = ""; // clear the all the Client IDs 
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(frame, "User does not exist anymore."); // if user doesn't exist then show message
					}
				}
			}
		});
		frame.getContentPane().add(Send_Btn);

		ActiveUsers = new JList();
		ActiveUsers.setBounds(554, 63, 327, 457);
		ActiveUsers.setToolTipText("Active Users");
		frame.getContentPane().add(ActiveUsers);

		End_User_Btn = new JButton("End Chat");
		End_User_Btn.setBounds(703, 533, 193, 84);
		End_User_Btn.setBackground(SystemColor.textHighlight);
		End_User_Btn.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
		End_User_Btn.addActionListener(new ActionListener() { // kill process event
			public void actionPerformed(ActionEvent e) {
				try {
					outStream.writeUTF("exit"); // closes the thread and show the message on server and Client's message
												// board
					Chat_Box.append("You are disconnected now.\n");
					frame.dispose(); // close the frame 
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		frame.getContentPane().add(End_User_Btn);

		JLabel lblNewLabel = new JLabel("Active Users");
		lblNewLabel.setBounds(559, 34, 95, 25);
		lblNewLabel.setBackground(Color.GRAY);
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 16));
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		frame.getContentPane().add(lblNewLabel);

		One_to_N_Btn = new JRadioButton("1 to N");
		One_to_N_Btn.setBounds(682, 24, 65, 27);
		One_to_N_Btn.setBackground(Color.GRAY);
		One_to_N_Btn.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
		One_to_N_Btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ActiveUsers.setEnabled(true);
			}
		});
		One_to_N_Btn.setSelected(true);
		frame.getContentPane().add(One_to_N_Btn);

		BroadCast_Btn = new JRadioButton("Broadcast");
		BroadCast_Btn.setBounds(774, 24, 107, 25);
		BroadCast_Btn.setBackground(Color.GRAY);
		BroadCast_Btn.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
		BroadCast_Btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ActiveUsers.setEnabled(false);
			}
		});
		frame.getContentPane().add(BroadCast_Btn);

		ButtonGroup btngrp = new ButtonGroup();
		btngrp.add(One_to_N_Btn);
		btngrp.add(BroadCast_Btn);
		
		JLabel lblNewLabel_1 = new JLabel("CHAT_BOX");
		lblNewLabel_1.setBounds(114, 29, 115, 14);
		lblNewLabel_1.setBackground(Color.GRAY);
		lblNewLabel_1.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 16));
		frame.getContentPane().add(lblNewLabel_1);
		
		lblNewLabel_2 = new JLabel("Type Message");
		lblNewLabel_2.setBackground(Color.GRAY);
		lblNewLabel_2.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 16));
		lblNewLabel_2.setBounds(12, 558, 104, 39);
		frame.getContentPane().add(lblNewLabel_2);

		frame.setVisible(true);
	}
}