import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.SystemColor;


public class LoginPage extends JFrame{

	private JFrame frame;
	private JTextField UserName;
	private int port = 12345;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) { // main function which will make UI visible
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginPage window = new LoginPage();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LoginPage() {
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() { // it will initialize the components of UI
		frame = new JFrame();
		frame.setBounds(100, 100, 619, 342);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Client Register");

		UserName = new JTextField();
		UserName.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 12));
		UserName.setBounds(207, 50, 276, 61);
		frame.getContentPane().add(UserName);
		UserName.setColumns(10);

		JButton Login_Btn = new JButton("Connect");
		Login_Btn.setBackground(SystemColor.textHighlight);
		Login_Btn.addActionListener(new ActionListener() { //action will be taken on clicking login button
			public void actionPerformed(ActionEvent e) {
				try {
					String ID = UserName.getText(); // username entered by user
					Socket skt = new Socket("localhost", port); // create a socket
					DataInputStream inputStream = new DataInputStream(skt.getInputStream()); // create input and output stream
					DataOutputStream outStream = new DataOutputStream(skt.getOutputStream());
					outStream.writeUTF(ID); // send username to the output stream
					
					String msgFromServer = new DataInputStream(skt.getInputStream()).readUTF(); // receive message on socket
					if(msgFromServer.equals("Username already taken")) {//if server sent this message then prompt user to enter other username
						JOptionPane.showMessageDialog(frame,  "Username already taken\n"); // show message in other dialog box
					}else {
						new Client(ID, skt); // otherwise just create a new thread of Client view and close the register jframe
						frame.dispose();
					}
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		
		Login_Btn.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 18));
		Login_Btn.setBounds(273, 145, 132, 61);
		frame.getContentPane().add(Login_Btn);

		JLabel lblNewLabel = new JLabel("Username");
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 18));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(44, 55, 132, 47);
		frame.getContentPane().add(lblNewLabel);
	}

	
}