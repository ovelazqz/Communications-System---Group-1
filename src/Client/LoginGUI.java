package Client;

import javax.swing.*;

import Common.Message;

import java.awt.*;
import java.awt.event.*;
import java.io.*;





public class LoginGUI implements UserInterface{
	
	private static Client client; // to access the character limit checker
	
	public LoginGUI() throws ClassNotFoundException {
        
        createUI();
  
 	}
           
// 		  ** for future image **
//        ImageIcon image = new ImageIcon(); // create an ImageIcon
//        frame.setIconImage(null); // change icon of frame
//        frame.getContentPane().setBackground(null); //change color of background
        
//        frame.setVisible(true);
//	}
	
	
	
	private static void createUI() throws ClassNotFoundException {
		// add GUI
		try {
			client = Client.getInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}
		JFrame frame = new JFrame();
		JButton loginButton = new JButton("Log In");
		loginButton.setEnabled(false);
		frame.setTitle("/chat."); // placeholder title 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit out of application
        frame.setSize(400, 500);
        frame.setResizable(false); // prevent frame from being resized
        
        frame.setLocationRelativeTo(null);
        
        
        // when the window is first created, the red will indicate that it is not connected
        // if connected then it will turn green
        if (client.isConnected()) {
        	frame.setBackground(Color.GREEN);
        } else {
        	frame.setBackground(Color.RED);
        }
        
        TextField usernameField = new TextField("Username");
        usernameField.setMaximumSize(new Dimension(250, usernameField.getPreferredSize().height)); 
        TextField passwordField = new TextField("Password");
        passwordField.setMaximumSize(new Dimension(250, passwordField.getPreferredSize().height)); 

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        
        mainPanel.add(usernameField);
        mainPanel.add(passwordField);
        

        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        
        mainPanel.add(usernameField);
        mainPanel.add(passwordField);
        mainPanel.add(loginButton);
        frame.add(mainPanel);
        frame.setVisible(true);
        
        
		loginButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Send(usernameField.getText(),passwordField.getText());
				// send()
				// read response
				// if yes -> close this gui and open next gui
				// if no  -> pop-up error message
			}
			
		});
		
	}
	 
	public static void Send(String username, String password) {
	    
	       
	}


	
	public void processCommands() {
		
		
	}
}
