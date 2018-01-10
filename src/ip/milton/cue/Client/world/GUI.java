/**
 * @class_name GUI
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This class contains the user interface
 */

package ip.milton.cue.world;

import ip.milton.cue.execution.Exit;
import ip.milton.cue.execution.Instructions;
import ip.milton.cue.execution.Start;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUI extends JFrame {

	// Variable declarations
	private static Dimension frame;

	private static JPanel errorPanel;

	private JPanel mainPanel;

	private JPanel instructionsPanel;

	private static String errorMessage;

	public GUI() {
		super("Cue");

		// Setting up jframe
		final int screenSizeX = 1100;
		final int screenSizeY = 700;

		setFrame(new Dimension(screenSizeX, screenSizeY));

		this.setSize(screenSizeX, screenSizeY);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Creating error screen
		errorMessage = "";

		errorPanel = new errorScreen();

		// Creating main menu
		mainPanel = new JPanel() {
			/**
			 * paintComponent 
			 * This method draws the background of the main menu
			 * @param Graphics - An object that contains useful drawing methods
			 * @return void
			 */
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				BufferedImage backgroundImage = null;
				// Loading background of main menu
				try {
					backgroundImage = ImageIO.read(new File(("Images/mainBackground.png")));
				} catch (IOException e) {
					GUI.setErrorMessage("Background file not found, exit, check paths and try again");
					this.add(GUI.getErrorPanel());
					GUI.getErrorPanel().validate();
					GUI.getErrorPanel().repaint();
					GUI.getErrorPanel().setVisible(true);
				}

				g.drawImage(backgroundImage, 0, 0, null);
			}
		};

		mainPanel.repaint();

		mainPanel.setLayout(null);

		// Creating jtextfields
		JTextField hostNameField = new JTextField("Enter server host name here");
		JTextField portField = new JTextField("Enter port number here");

		// Creating buttons
		JButton startB = new JButton("Connect to game");
		startB.addActionListener(new Start(mainPanel, this, hostNameField, portField));

		JButton instructionB = new JButton("Instructions");
		instructionB.addActionListener(new Instructions(this));

		JButton exitB = new JButton("Exit");
		exitB.addActionListener(new Exit());

		// Adding components to main panel
		mainPanel.add(startB);
		mainPanel.add(hostNameField);
		mainPanel.add(portField);
		mainPanel.add(instructionB);
		mainPanel.add(exitB);

		// Adjusting positions of components
		Insets insets = mainPanel.getInsets();

		startB.setBounds((int) (this.getWidth() * .33 + insets.left), (int) (this.getHeight() * .40 + insets.top),
				(int) (this.getWidth() * .33), (int) (this.getHeight() * .1));

		hostNameField.setBounds((int) (this.getWidth() * .33 + insets.left),
				(int) (this.getHeight() * .50 + insets.top), (int) (this.getWidth() * .33),
				(int) (this.getHeight() * .05));

		portField.setBounds((int) (this.getWidth() * .33 + insets.left), (int) (this.getHeight() * .55 + insets.top),
				(int) (this.getWidth() * .33), (int) (this.getHeight() * .05));

		instructionB.setBounds((int) (this.getWidth() * .33 + insets.left), (int) (this.getHeight() * .65 + insets.top),
				(int) (this.getWidth() * .33), (int) (this.getHeight() * .1));

		exitB.setBounds((int) (this.getWidth() * .33 + insets.left), (int) (this.getHeight() * .86 + insets.top),
				(int) (this.getWidth() * .33), (int) (this.getHeight() * .09));

		// Creating instructions screen
		instructionsPanel = new JPanel() {
			/**
			 * paintComponent 
			 * This method draws the instruction screen
			 * @param Graphics - An object that contains useful drawing methods
			 * @return void
			 */
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				BufferedImage backgroundImage = null;
				try {
					backgroundImage = ImageIO.read(new File(("Images/instructionScreen.png")));
				} catch (IOException e) {
					GUI.setErrorMessage("Instructions screen not found, exit, check file path and try again");
					this.add(GUI.getErrorPanel());
					GUI.getErrorPanel().validate();
					GUI.getErrorPanel().repaint();
					GUI.getErrorPanel().setVisible(true);
				}

				g.drawImage(backgroundImage, 0, 0, null);
			}
		};

		instructionsPanel.repaint();

		instructionsPanel.setLayout(null);

		// Creating back button for instructions screen
		JButton backB = new JButton("Back");
		backB.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 
			 * This method when fired takes user back to the main menu
			 * @param ActionEvent
			 * @return void
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				instructionsPanel.setVisible(false);
				mainPanel.revalidate();
				mainPanel.repaint();
				mainPanel.setVisible(true);
			}

		});

		instructionsPanel.add(backB);

		// Setting size and position of back button
		backB.setBounds((int) (this.getWidth() * 0.05), (int) (this.getHeight() * 0.05), (int) (this.getWidth() * 0.1),
				(int) (this.getHeight() * 0.05));

		// Adding panels to frame
		this.add(this.instructionsPanel);
		this.add(this.mainPanel);

		this.setVisible(true);
	}

	/**
	 * @class_name errorScreen
	 * @version Final
	 * @author Milton Ip
	 * @date 01/23/17 This class is the error screen
	 */
	public class errorScreen extends JPanel {
		/**
		 * paintComponent 
		 * This method draws the error screen
		 * @param Graphics - An object that contains useful drawing methods
		 * @return void
		 */
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g;

			BufferedImage errorImage = null;
			try {
				errorImage = ImageIO.read(new File(("Images/errorScreen.png")));
			} catch (IOException e) { // what do you do when loading your error screen gives you an error? You jump
										// ship of course, program closes because there is no error screen to
										// notify the user of the error, ironic
				System.exit(0);
			}

			g.drawImage(errorImage, 0, 0, null);

			g2d.setColor(Color.YELLOW);

			// Setting and centering font
			Font font = new Font("Helvetica", Font.PLAIN, 32);
			g2d.setFont(font);
			FontMetrics metrics = g2d.getFontMetrics();

			int errorMessageX = (int) ((this.getWidth() - metrics.stringWidth(GUI.errorMessage)) / 2);
			int errorMessageY = (int) (((this.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent()
					+ this.getHeight() * 0.2);

			//Displaying error message
			g2d.drawString(GUI.errorMessage, errorMessageX, errorMessageY);
		}
	}

	public static Dimension getFrame() {
		return frame;
	}

	public static void setFrame(Dimension frame) {
		GUI.frame = frame;
	}

	public static JPanel getErrorPanel() {
		return errorPanel;
	}

	public static void setErrorPanel(JPanel errorPanel) {
		GUI.errorPanel = errorPanel;
	}

	public static String getErrorMessage() {
		return errorMessage;
	}

	public static void setErrorMessage(String errorMessage) {
		GUI.errorMessage = errorMessage;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(JPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	public JPanel getInstructionsPanel() {
		return instructionsPanel;
	}

	public void setInstructionsPanel(JPanel instructionsPanel) {
		this.instructionsPanel = instructionsPanel;
	}

}
