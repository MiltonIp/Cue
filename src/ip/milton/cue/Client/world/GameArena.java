/**
 * @class_name GameArena
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This class serves as the game arena that the game will take place in 
 */

package ip.milton.cue.world;

import ip.milton.cue.execution.CueMain;
import ip.milton.cue.objects.Tanks;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameArena extends JPanel implements Runnable {

	//Variable Declaration
	private JFrame frame;

	private int[][] cBallArray = new int[3][2];
	private int[][] orbArray = new int[4][2];
	
	private ArrayList<Tanks> tankList = new ArrayList<Tanks>();

	private Thread thread;

	private Client client;

	private int minX, minY, maxX, maxY;

	private BufferedImage cBallImg;
	private BufferedImage orbImg;
	private BufferedImage backgroundImg;

	private GameEndScreen endPanel;

	public GameArena(JFrame frame, String hostName, int portNumber) {
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		this.frame = frame;

		//Adding the tanks
		final int PLAYERS = 2;
		for (int i = 0; i < PLAYERS; i++) {
			this.tankList.add(new Tanks(i + 1, this, this.frame));
		}

		this.setBackground(Color.WHITE);

		this.setOpaque(true);

		//Loading sprites
		try {
			this.cBallImg = ImageIO.read(new File("Images/CannonballNew.png"));
			this.orbImg = ImageIO.read(new File("Images/OrbNew.png"));
			this.backgroundImg = ImageIO.read(new File("Images/Background.png"));
		} catch (IOException e) {	//Sprites not found, relevant error screen pops up to inform user
			this.setVisible(false);
			GUI.setErrorMessage("Sprites not found, exit, check paths and try again");
			this.frame.add(GUI.getErrorPanel());
			GUI.getErrorPanel().revalidate();
			GUI.getErrorPanel().repaint();
			GUI.getErrorPanel().setVisible(true);
		}

		//Adding listeners 
		this.addKeyListener(new MyKeyListener(this, this.frame));
		this.addMouseMotionListener(new MyMouseMotionListener(this, this.frame));
		this.addMouseListener(new MyMouseListener(this, this.frame));

		this.setArenaLimits();

		//Starting thread...
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}

		// Adding panel to frame
		this.frame.add(this);

		this.requestFocusInWindow();
		
		//Creating client instance
		this.setClient(new Client(this, hostName, portNumber, frame));
	}

	/**
	 * setArenaLimits
	 * This method sets the limits of the game
	 * @param null
	 * @return void
	 */
	public void setArenaLimits() {
		//Hard coded values to match the visuals of the background
		this.setMinX(36);
		this.setMaxX((int) (this.frame.getWidth() - 36));
		this.setMinY(52);
		this.setMaxY((int) (this.frame.getHeight() - this.frame.getInsets().top - 52));
	}

	/**
	 * endGame
	 * This method ends the current game
	 * @param int - The player that has been slain
	 * @return void
	 */
	public void endGame(int player) {
		this.setVisible(false);
		// Temporarily disable game over screen and automatically restart game
		// this.endPanel = new GameEndScreen(player, this.frame, this);

		//Resetting values
		for (Tanks tank : this.tankList) {
			tank.setSlain(false);
			tank.getKeysPressed().clear();
		}
		this.client.sendRestart();
	}

	/**
	 * @class_name GameEndScreen
	 * @version Final
	 * @author Milton Ip
	 * @date 01/23/17
	 * This class extends a JPanel to inform players that the current game has ended
	 */
	public class GameEndScreen extends JPanel {
		//Variable Declaration
		private int playerKilled;
		private JFrame frame;
		private GameArena game;
		private GameEndScreen endScreen;
		private String displayMessage;

		public GameEndScreen(int player, JFrame frame, GameArena game) {
			//Initializing variables
			this.playerKilled = player;
			this.frame = frame;
			this.game = game;
			this.displayMessage = String.valueOf("Player " + this.playerKilled) + " has perished";
			endScreen = this;

			this.setBackground(Color.WHITE);
			this.revalidate();
			this.repaint();

			//Restart button so players can keep playing over and over without restarting program
			JButton restartButton = new JButton("Play Again");

			restartButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					endScreen.displayMessage = "Waiting for other player to press Play Again";
					endScreen.repaint();
					game.client.sendRestart();
				}
			});

			this.setLayout(null);
			
			//Adjusting play again button
			this.add(restartButton);
			
			restartButton.setBounds((int) (this.frame.getWidth() * 0.38) , (int) (this.frame.getHeight() * 0.6), (int) (this.frame.getWidth() * 0.25), (int) (this.frame.getHeight() * 0.1));

			this.setVisible(true);

			frame.add(this);
		}

		/**
		 * paintComponent
		 * This method draws visuals on the screen
		 * @param Graphics - An object that contains useful drawing methods
		 * @return void
		 */
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			//Loading end screen
    		BufferedImage endImage = null;
			try {
				endImage = ImageIO.read(new File(("Images/endScreen.png")));
			} catch (IOException e) {	//Error message 
				GUI.setErrorMessage("End screen not found, exit, check file paths and try again");
				this.frame.add(GUI.getErrorPanel());
				GUI.getErrorPanel().validate();
				GUI.getErrorPanel().repaint();
				GUI.getErrorPanel().setVisible(true);
			}
			
			//Drawing end screen
			Graphics2D graphics2D = (Graphics2D) g;
			
			graphics2D.drawImage(endImage, 0, 0, null);
			
			graphics2D.setColor(Color.WHITE);

			// Setting and centering font, as well as drawing message
			Font font = new Font("Helvetica", Font.PLAIN, 28);
			graphics2D.setFont(font);
			FontMetrics metrics = graphics2D.getFontMetrics();

			int messageX = (this.getWidth() - metrics.stringWidth(this.displayMessage)) / 2;
			int messageY = ((this.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

			graphics2D.drawString(this.displayMessage, messageX, messageY);
		}
	}

	/**
	 * setOrbs
	 * This method sets the location of the orbs at the beginning of the game
	 * @param String - Contains all the x y of the orbs
	 * @return void
	 */
	public void setOrbs(String orbs) {
		final int ORB_COUNT = Character.getNumericValue(orbs.charAt(0));
		int index = 0;
		
		//Loopa equal to amount of orbs
		for (int i = 0; i < ORB_COUNT; i++) {
			
			//x y of orbs padded with zeros to 5 decimal places
			int x = Integer
					.parseInt(orbs.substring(orbs.indexOf("orb|", index) + 4, orbs.indexOf("orb|", index) + 4 + 5));
			int y = Integer.parseInt(
					orbs.substring(orbs.indexOf("orb|", index) + 4 + 5, orbs.indexOf("orb|", index) + 4 + 5 + 5));
			
			index = orbs.indexOf("orb|", index) + 1;

			//Updating x y of orbs
			this.orbArray[i][0] = x;
			this.orbArray[i][1] = y;
		}
	}

	/**
	 * paintComponent
	 * This method draws all the game objects on the screen
	 * @param Graphics - A useful object that contains drawing methods
	 * @return void
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setDoubleBuffered(true);
		
		Graphics2D graphics2D = (Graphics2D) g;

		graphics2D.drawImage(this.backgroundImg, 0, 0, this);	//Drawing background

		//Draw the rest of the game objects
		drawTanks(graphics2D);
		drawCannonballs(graphics2D);
		drawOrbs(graphics2D);

	}

	/**
	 * drawTanks
	 * This method draws all the tanks
	 * @param Graphics2D - An object that contains useful drawing methods
	 * @return void
	 */
	public void drawTanks(Graphics2D graphics2D) {
		
		//Setting font and colour
		graphics2D.setColor(Color.WHITE);
		
		Font font = new Font("Helvetica", Font.BOLD, 10);
		graphics2D.setFont(font);
		
		//Loops through all tanks
		for (Tanks tank : tankList) {
			//Draws an indicator above the tank that you control
			try{
				if(tank.getPlayer() == this.getClient().getPlayer()){
					graphics2D.drawString("You (Player " + tank.getPlayer() + ")", tank.getX(), tank.getY() - 8);
				}
			} catch (NullPointerException e){
				// Do nothing
				// This exception is caused by an incorrect host name/port number, trust me
			}
			
			//Draws tank
			graphics2D.drawImage(tank.getTankBody(), tank.getX(), tank.getY(), this);

			//Calculations to rotate tank head towards where your cursor is pointing
			AffineTransform transform = new AffineTransform();

			transform.translate(tank.getX() + tank.getTankHead().getHeight() / 2,
					tank.getY() + tank.getTankHead().getHeight() / 2);

			transform.rotate(tank.getMouseRadians());
			transform.translate(-tank.getTankHead().getHeight() / 2, -tank.getTankHead().getHeight() / 2);

			graphics2D.drawImage(tank.getTankHead(), transform, null);

			//Checks if any tanks have been killed, if yes, end game
			if (tank.isSlain()) {
				this.endGame(tank.getPlayer());
			}
		}
	}

	/**
	 * drawCannonballs
	 * This method draws all the cannonballs
	 * @param Graphics2D - An object that contains useful drawing methods
	 * @return void
	 */
	public void drawCannonballs(Graphics2D graphics2D) {
		synchronized (CueMain.getLocka()){
			for (int i = 0, p = this.cBallArray.length; i < p; i++) {
				if (this.cBallArray[i][0] > -1) {	//If x is -1, that means it doesn't 'exist' and shouldn't be drawn
					graphics2D.drawImage(this.cBallImg, (this.cBallArray[i][0]), (this.cBallArray[i][1]), this);
				}
			}
		}
	}

	/**
	 * drawOrbs
	 * This method draws all the orbs
	 * @param Graphics2D - An object that contains useful drawing methods
	 * @return void
	 */
	public void drawOrbs(Graphics2D graphics2D) {
		for (int i = 0, p = this.orbArray.length; i < p; i++) {
			if (this.orbArray[i][0] > -1) {
				graphics2D.drawImage(this.orbImg, (this.orbArray[i][0]), (this.orbArray[i][1]), this);
			}
		}
	}

	/**
	 * run
	 * This method redraws the game 60 times a second for a indeterminate amount of time
	 * @param null
	 * @return void
	 */
	@Override
	public void run() {
		double fps = 60.0;	//Draw visuals 60 times a second
		double nanoSeconds = 1000000000 / fps;
		long lastTime = System.nanoTime();
		double deltaTime = 0;
		while (CueMain.isRunThreads()) {
			long currentTime = System.nanoTime();
			deltaTime += (currentTime - lastTime) / nanoSeconds;
			lastTime = currentTime;
			//Every 1/60th of a second
			while (deltaTime >= 1) {
				this.repaint();
				deltaTime--;
			}
		}
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public ArrayList<Tanks> getTankList() {
		return tankList;
	}

	public void setTankList(ArrayList<Tanks> t) {
		this.tankList = t;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client c) {
		this.client = c;
	}

	public int getMinX() {
		return minX;
	}

	public void setMinX(int minX) {
		this.minX = minX;
	}

	public int getMinY() {
		return minY;
	}

	public void setMinY(int minY) {
		this.minY = minY;
	}

	public int getMaxX() {
		return maxX;
	}

	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	public int getMaxY() {
		return maxY;
	}

	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

	public int[][] getcBallArray() {
		return cBallArray;
	}

	public void setcBallArray(int column, int x, int y) {
		this.cBallArray[column][0] = x;
		this.cBallArray[column][1] = y;
	}

	public BufferedImage getcBallImg() {
		return cBallImg;
	}

	public void setcBallImg(BufferedImage cBallImg) {
		this.cBallImg = cBallImg;
	}

	public BufferedImage getOrbImg() {
		return orbImg;
	}

	public void setOrbImg(BufferedImage orbImg) {
		this.orbImg = orbImg;
	}

	public int[][] getOrbArray() {
		return orbArray;
	}

	public void setOrbArray(int column, int x, int y) {
		this.orbArray[column][0] = x;
		this.orbArray[column][1] = y;
	}

	public GameEndScreen getEndPanel() {
		return endPanel;
	}

	public void setEndPanel(GameEndScreen endPanel) {
		this.endPanel = endPanel;
	}

}
