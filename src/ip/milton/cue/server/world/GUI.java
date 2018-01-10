/**
 * @class_name GUI
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This is a class that contains the user interface
 */

package ip.milton.cue.server.world;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JFrame {

	//Variable declaration
	private Dimension frame;
	private static JPanel mainPanel;
	
	private static String displayMessage;
	
	public GUI() {
		super("Cue Server");

		//Setting jframe up
		final int screenSizeY = 700;
		final int screenSizeX = 1100;

		setFrame(new Dimension(screenSizeX, screenSizeY));

		this.setSize(screenSizeX, screenSizeY);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Displays this computer's host name and the port the server is on
		String hostNameMessage = "";
		displayMessage = "Waiting for players";
		try {
			InetAddress ip = InetAddress.getLocalHost();
			String hostName = ip.getHostName();
			hostNameMessage = "Server Host Name : " + hostName;		
		} catch (UnknownHostException e) { //Error message
			hostNameMessage = "Error in getting IP address";
		}
			
		// Creating panels
		mainPanel = new serverPanel(hostNameMessage);
		mainPanel.setLayout(new FlowLayout());
		mainPanel.setOpaque(true);
		mainPanel.setBackground(Color.WHITE);

		// Adding panels to frame
		this.add(mainPanel);

		this.setVisible(true);
	}
	
	/**
	 * @class_name serverPanel
	 * @version Final
	 * @author Milton Ip
	 * @date 01/23/17
	 * This is a panel that displays useful information about the server and its connections
	 */
	public class serverPanel extends JPanel{
		private String hostName;
		
		public serverPanel(String hostName){
			this.hostName = hostName;
			
			this.revalidate();
			this.repaint();
		}
		
		/**
		 * paintComponent 
		 * This method draws messages on the server panel
		 * @param Graphics - An object that contains useful drawing methods
		 * @return void
		 */
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D graphics2D = (Graphics2D) g;
			graphics2D.setColor(Color.DARK_GRAY);
			
			//Setting and centering font
			Font font = new Font("Helvetica", Font.PLAIN, 28);
			graphics2D.setFont(font);
			FontMetrics metrics = graphics2D.getFontMetrics();
			
			//Drawing host name 
			int hostMessageX = (this.getWidth() - metrics.stringWidth(hostName)) / 2;
			int hostMessageY = ((this.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent() - (int) (this.getHeight() * 0.1);
			
			graphics2D.drawString(this.hostName, hostMessageX, hostMessageY);
			
			//Drawing port number
			String portMessage = "Port Number: " + String.valueOf(Server.getPortNumber());
			int portMessageX = (this.getWidth() - metrics.stringWidth(portMessage)) / 2;
			int portMessageY = ((this.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
			
			graphics2D.drawString(portMessage, portMessageX, portMessageY);
			
			//Drawing relevant information about this server's connections
			int displayMessageX = (this.getWidth() - metrics.stringWidth(displayMessage)) / 2;
			int displayMessageY = ((this.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent() + (int) (this.getHeight() * 0.1);
			
			graphics2D.drawString(displayMessage, displayMessageX, displayMessageY);
						
		}
	}

	public static JPanel getMainPanel() {
		return mainPanel;
	}

	public  Dimension getFrame() {
		return this.frame;
	}

	public  void setFrame(Dimension frame) {
		this.frame = frame;
	}

	public static String getDisplayMessage() {
		return displayMessage;
	}

	public static void setDisplayMessage(String d) {
		displayMessage = d;
	}
	
	
}
