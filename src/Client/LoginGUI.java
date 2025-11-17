package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;





public class LoginGUI implements UserInterface {
	
	Client client; // to access the character limit checker
	
	private JFrame frame;
	
	private JTextField username;
	private JTextField password;
	
	
	public LoginGUI() throws ClassNotFoundException {
		
		try {
			this.client = Client.getInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}
		createWindow();	}
	
	public void createWindow() {
		frame = new JFrame("Communication System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        // Setting the minimum allowed size that way the user can't just shrink the whole GUI down to nothing
        frame.setMinimumSize(new Dimension(400, 500));
        // Setting the maximum allowed size
        frame.setMaximumSize(new Dimension(400, 500));
        
        frame.setLocationRelativeTo(null);
        
        createUI(frame);
        
        // when the window is first created, the red will indicate that it is not connected
        frame.setBackground(Color.RED);
        
        // if connected then it will turn green
        if (client.isConnected()) {
        	frame.setBackground(Color.GREEN);
        }
//        frame.setForeground(Color.RED);
        //frame.getContentPane().setBackground(Color.BLUE);
        
        //createUI(frame);
        
        frame.setVisible(true);
	}
	
	public void createUI(JFrame frame) {
		// add GUI
	}
	 
	public void processCommands() {
		// will handle inputs?
		// will have the event listeners for the button press??
	}
}
