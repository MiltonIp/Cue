/**
 * @class_name Server
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This is a class that contains the serve to communicate with the players
 */

package ip.milton.cue.server.world;

import ip.milton.cue.server.execution.ServerMain;
import ip.milton.cue.server.objects.Ball;
import ip.milton.cue.server.objects.Cannonballs;
import ip.milton.cue.server.objects.Orbs;
import ip.milton.cue.server.objects.Tanks;

import java.awt.Point;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.imageio.ImageIO;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.Iterator;

public class Server implements Runnable {
	
	//Variable declaration
	private static int portNumber;

	private ServerSocket serverSock;
	

	private ArrayList<PrintWriter> writers = new ArrayList<PrintWriter>();
	private ArrayList<Ball> ballList = new ArrayList<Ball>();
	private ArrayList<Tanks> tankList = new ArrayList<Tanks>();

	private int minX, maxX, minY, maxY;
	private int orbCount;

	private boolean playerOneRestart, playerTwoRestart;

	private String playerCommand;

	private GUI gui;

	public Server(GUI gui) {
		//Initializing values
		this.gui = gui;
		this.playerOneRestart = false;
		this.playerTwoRestart = false;

		///Attempting to create a server
		try {
			final int PORT = 0;
			serverSock = new ServerSocket(PORT);
			serverSock.setReuseAddress(true);
			GUI.setDisplayMessage("Server Created");
			GUI.getMainPanel().repaint();
			portNumber = serverSock.getLocalPort();

			this.setArenaLimits();

			//Setting up game (orbs, tanks)
			final String ORBS = this.generateOrbs();
			
			final int PLAYERS = 2;
			
			for (int i = 0; i < PLAYERS; i++) {
				new ClientHandler(serverSock.accept(), (i + 1), this, ORBS);
				tankList.add(new Tanks(i + 1, gui));
				GUI.setDisplayMessage("Player " + String.valueOf(i + 1) + " has connected");
				GUI.getMainPanel().repaint();
			}

			GUI.setDisplayMessage("All players connected, starting game...");
			GUI.getMainPanel().repaint();

			//Starting broadcast thread...
			Thread broadcast = new Thread(this);
			broadcast.start();

		} catch (Exception e) {	//Error in creating server
			GUI.setDisplayMessage("Unable to create server, please exit and try again.");
			GUI.getMainPanel().repaint();
			ServerMain.setRunThreads(false);
		}

	}
	
	/**
	 * setArenaLimit
	 * This method sets the boundaries of the game
	 * @param null
	 * @return void
	 */
	public void setArenaLimits() {
		this.setMinX(36);
		this.setMaxX((int) (this.gui.getWidth() - 36));
		this.setMinY(52);
		this.setMaxY((int) (this.gui.getHeight() - this.gui.getInsets().top - 52));
	}

	/**
	 * generateOrbs
	 * This method randomly generates the x y positions of orbs
	 * @param null
	 * @return String - A string containing the information of the orbs' position
	 */
	public String generateOrbs() {
		//Temporary arraylist to make sure orbs don't spawn on top of each other
		ArrayList<Point> tempOrbList = new ArrayList<Point>();

		int diameter = new Orbs(0, 0, 0, 0, 0, 0).getBodyImg().getHeight();

		this.orbCount = 4;

		String orbPoints = String.valueOf(this.orbCount);

		
		for (int q = 0; q < this.orbCount; q++) {
			// Randomly spawn orb
			int x = (int) (Math.random() * ((this.gui.getWidth() * .6 - diameter) + 1) + this.gui.getWidth() * 0.2);
			int y = (int) (Math.random() * ((this.gui.getHeight() * .6 - diameter) + 1) + this.gui.getHeight() * 0.2);

			//If the randomly spawned orb overlaps any existing orb, create new ones until it doesn't overlap any orbs
			for (int i = 0, p = tempOrbList.size(); i < p; i++) {
				if (Math.pow(x - tempOrbList.get(i).x, 2) + Math.pow(y - tempOrbList.get(i).y, 2) <= Math.pow(diameter,
						2)) {
					x = (int) (Math.random() * ((this.gui.getWidth() * .6 - diameter) + 1) + this.gui.getWidth() * 0.2);
					y = (int) (Math.random() * ((this.gui.getHeight() * .6 - diameter) + 1)
							+ this.gui.getHeight() * 0.2);
					i = -1;
				}
			}

			tempOrbList.add(new Point(x, y));
			//Adding the x y positions of teh orbs
			orbPoints += "orb|" + String.format("%05d", x) + String.format("%05d", y);

			final float SPEED = 0;
			final float X_VEL = 0;
			final float Y_VEL = 0;
			final double RADIANS = 0;

			//Adding all spawned orbs to ball list
			synchronized (ServerMain.getLocka()) {
				this.ballList.add(new Orbs(x, y, SPEED, X_VEL, Y_VEL, RADIANS));
			}
		}
		return orbPoints;
	}

	/**
	 * restart
	 * This method restarts the game
	 * @param String - A command informing what is to be done
	 * @return void
	 */
	public void restart(String command) {
			
		//Make sure game only restarts when both players choose to restart
		if (command.charAt(0) == '1') {
			this.playerOneRestart = true;
		} else {
			this.playerTwoRestart = true;
		}

		//Resetting values
		for (Tanks tank : this.tankList) {
			tank.setSlain(false);
		}

		if (playerOneRestart && playerTwoRestart) {
			this.playerOneRestart = false;
			this.playerTwoRestart = false;

			for (PrintWriter writer : this.writers) {
				writer.println("RESTART");
				writer.flush();
			}

		} else if (playerOneRestart || playerTwoRestart) {
			//Restting more values...
			for (Tanks tank : this.tankList) {

				if (tank.getPlayer() == 1) {
					tank.setX(0);
					tank.setY((int) ((this.gui.getHeight() / 2) - (tank.getTankBody().getHeight() / 2)));

				} else if (tank.getPlayer() == 2) {
					tank.setX((int) ((this.gui.getWidth()) - (tank.getTankBody().getWidth())));
					tank.setY((int) ((this.gui.getHeight() / 2) - (tank.getTankBody().getHeight() / 2)));
				}

				tank.setxVelocity(0);
				tank.setyVelocity(0);

			}

			synchronized (ServerMain.getLocka()) {
				this.ballList.clear();
			}

			this.generateOrbs();
		}

	}

	/**
	 * moveTankHead
	 * This method updates the tanks' angle between it and the cursor to for tank head rotation purposes
	 * @param String - A command informing what angle to set to
	 * @return void
	 */
	public void moveTankHead(String command) {
		int player = Character.getNumericValue(command.charAt(0));
		Tanks tank = this.getTankList().get(player - 1);

		tank.setMouseRadians(Double.parseDouble(command.substring(command.indexOf("|") + 1)));
	}

	/**
	 * moveTankVelocities
	 * This method updates the tanks' velocities
	 * @param String - A command informing what values to set velocities to
	 * @return void
	 */
	public void moveTankVelocities(String command) {
		int player = Character.getNumericValue(command.charAt(0));
		Tanks tank = this.getTankList().get(player - 1);

		//If player pressed a key, change velocity accordingly
		if (command.contains("PRESS")) {
			if (command.charAt(command.indexOf("|") + 1) == 'x') {
				tank.setxVelocity(Float.parseFloat(command.substring(command.lastIndexOf("|") + 1)));
			} else if (command.charAt(command.indexOf("|") + 1) == 'y') {
				tank.setyVelocity(Float.parseFloat(command.substring(command.lastIndexOf("|") + 1)));
			}
			//If player let go of a key, change velocity to 0
		} else if (command.contains("RELEASE")) {
			if (command.charAt(command.indexOf("|") + 1) == 'x') {
				tank.setxVelocity(0);
			} else if (command.charAt(command.indexOf("|") + 1) == 'y') {
				tank.setyVelocity(0);
			}
		}
	}

	/**
	 * shootCannonballs
	 * This method updates the tanks' angle between it and the cursor to for tank head rotation purposes
	 * @param String - A command informing which angle the cannonball is to be fired at
	 * @return void
	 */
	public void shootCannonballs(String command) {
		int player = Character.getNumericValue(command.charAt(0));
		Tanks tank = this.getTankList().get(player - 1);


		double mouseRadians = tank.getMouseRadians();

		//Calculating cannonball start position
		int cannonballX = (int) ((tank.getTankBody().getHeight() / 2) * Math.cos(mouseRadians));
		int cannonballY = (int) (cannonballX * Math.tan(mouseRadians));
		cannonballX += (int) ((tank.getX() + (tank.getTankBody().getHeight() / 4)));
		double cannonballHeight = 0;

		//Loading sprite
		try {
			cannonballHeight = ImageIO.read(new File("Images/CannonballNew.png")).getHeight();
		} catch (IOException e) {	//Error loading sprite
			GUI.setDisplayMessage("Cannonball image not loaded");
			GUI.getMainPanel().repaint();
		}

		cannonballY += (int) ((tank.getY() + (cannonballHeight / 2)));

		//Calculating the speed and velocities of the cannonball using tank speed as a reference (3 times faster than tank speed)
		float speed = (float) (tank.getSpeed() * 3);
		float xVelocity = ((float) (speed * Math.cos(mouseRadians)));
		float yVelocity = ((float) (speed * Math.sin(mouseRadians)));

		//Adding cannonball to ball list
		synchronized (ServerMain.getLocka()) {
			this.getBallList()
					.add(new Cannonballs(cannonballX, cannonballY, speed, xVelocity, yVelocity, mouseRadians));

			//If total amount of balls exceed 7, remove the oldest cannonball in list of balls
			final int MAX_BALLS = 7;
			if (this.getBallList().size() > MAX_BALLS) {
				for (Ball ball : this.getBallList()) {
					if (ball instanceof Cannonballs) {
						this.getBallList().remove(ball);
						break;
					}
				}
			}
		}

	}

	/**
	 * moveBalls
	 * This method updates the all the ball's x y position
	 * @param null
	 * @return void
	 */
	public void moveBalls() {
		//Loops through all the balls
		synchronized (ServerMain.getLocka()) {
			Iterator<Ball> iterator = this.ballList.iterator();

			while (iterator.hasNext()) {
				Ball ball = iterator.next();
				ball.move();
				if (ball instanceof Orbs) {
					((Orbs) ball).wallCollide(minX, minY, maxX, maxY);	//Check for collisions
				} else {
					if (((Cannonballs) ball).isCollided()) {
						iterator.remove();
					} else {
						ball.wallCollide(minX, minY, maxX, maxY);	//Check for collisions
					}
				}

			}
		}

		synchronized (ServerMain.getLocka()) {
			this.calculateCollisions();	//Check for collisions
		}
	}

	/**
	 * calculateCollisions
	 * This method calculates the balls' new trajectory after colliding with another ball - based on 2D elastic collision physics
	 * @param null
	 * @return void
	 */
	public void calculateCollisions() {

		double xDistance, yDistance;
		for (int i = 0; i < this.ballList.size(); i++) {

			Ball ballA = this.ballList.get(i);
			float aRadius = 0;
			
			//Looping through all the balls and comparing them against each other
			if (ballA instanceof Orbs) {
				aRadius = ((Orbs) ballA).getRadius();
			} else if (ballA instanceof Cannonballs) {
				aRadius = ((Cannonballs) ballA).getRadius();
			}
			for (int q = i + 1; q < this.ballList.size(); q++) {

				Ball ballB = this.ballList.get(q);
				float bRadius = 0;
				
				if (ballB instanceof Orbs) {
					bRadius = ((Orbs) ballB).getRadius();
				} else if (ballA instanceof Cannonballs) {
					bRadius = ((Cannonballs) ballB).getRadius();
				}

				xDistance = ballA.getCenter().x - ballB.getCenter().x;
				yDistance = ballA.getCenter().y - ballB.getCenter().y;

				double distanceSquared = Math.pow(xDistance, 2) + Math.pow(yDistance, 2);

				//If the distance between two balls is equals or less than their combined radius, a collision has occurred
				if (distanceSquared <= Math.pow((aRadius + bRadius), 2)) {
					//Calculating new velocities based on assumption that momentum is conserved after colliding
					double xVelocity = ballB.getxVelocity() - ballA.getxVelocity();
					double yVelocity = ballB.getyVelocity() - ballA.getyVelocity();

					double distanceVel = (xDistance * xVelocity) + (yDistance * yVelocity);

					// Collision only resolves if balls are moving towards each other or if
					// one is stationary to avoid balls 'sticking' to each other and 'overlapping' each other
					if (distanceVel > 0) {
						double collisionRatio = distanceVel / distanceSquared;
						double xCollision = xDistance * collisionRatio;
						double yCollision = yDistance * collisionRatio;

						double totalMass = ballA.getMass() + ballB.getMass();
						double weightA = 2 * ballB.getMass() / totalMass;
						double weightB = 2 * ballA.getMass() / totalMass;

						//Updating balls' new velocities
						ballA.setxVelocity((float) (ballA.getxVelocity() + weightA * xCollision));
						ballA.setyVelocity((float) (ballA.getyVelocity() + weightA * yCollision));

						ballB.setxVelocity((float) (ballB.getxVelocity() - weightB * xCollision));
						ballB.setyVelocity((float) (ballB.getyVelocity() - weightB * yCollision));

						//If a cannonball had collided, update that fact so it can get deleted from the ball list
						if (ballA instanceof Cannonballs && ballB instanceof Orbs) {
							((Cannonballs) ballA).setCollided(true);
						} else if (ballB instanceof Cannonballs && ballA instanceof Orbs) {
							((Cannonballs) ballB).setCollided(true);
						}

					}
				}
			}
		}

	}

	/**
	 * run
	 * This method broadbasts the state of the game to all the clients every 1/60th of a second
	 * @param null
	 * @return void
	 */
	@Override
	public void run() {

		double tick = 60.0;	//Tick rate
		double nanoSeconds = 1000000000 / tick;
		long lastTime = System.nanoTime();
		double deltaTime = 0;
		while (ServerMain.isRunThreads()) {
			long currentTime = System.nanoTime();
			deltaTime += (currentTime - lastTime) / nanoSeconds;
			lastTime = currentTime;

			//Every 1/60th of a second
			while (deltaTime >= 1) {
				//Move all the balls 
				moveBalls();
				String ballPositions = this.orbCount + "orbs|";

				boolean stringFlag = false;

				//Concatenate all the ball positions into a string for broadcast
				synchronized (ServerMain.getLocka()) {
					for (Ball ball : this.ballList) {
						if (ball instanceof Orbs) {
							ballPositions += String.format("%05d", ball.getX()) + String.format("%05d", ball.getY());
						} else if (ball instanceof Cannonballs) {
							if (!stringFlag) {
								ballPositions += "cBall|";
								stringFlag = true;
							}
							ballPositions += String.format("%05d", ball.getX()) + String.format("%05d", ball.getY());
						}
					}
				}

				//Concatenate all the tanks' informations into a string for broadcast
				String tankPositions = "";
				for (Tanks tank : tankList) {
					//Moving tanks, as well as checking for collisions
					tank.move();
					
					if (tank.orbCollide(tank, this.getBallList())) {
						tank.setSlain(true);		//If the tank has collided with an orb - now considered dead and client and respond accordingly	
					}
					
					tank.wallCollide(this.getMinX(), this.getMinY(), this.getMaxX(), this.getMaxY());

					tankPositions += tank.getPlayer() + "*" + String.format("%05d", tank.getX())
							+ String.format("%05d", tank.getY()) + tank.getMouseRadians() + "slain|" + tank.isSlain();

				}

				//Loops through the printwriters of teh clients to broadcast state of the game to all players
				for (PrintWriter writer : writers) {
					String message = tankPositions + ballPositions + String.valueOf(this.ballList.size() - 4);

					writer.println(message);
					writer.flush();
				}
				deltaTime--;
			}
		}
	}

	/**
	 * @class_name ClientHandler
	 * @version Final
	 * @author Milton Ip
	 * @date 01/23/17
	 * This is a class that handles the server's clients
	 */
	public class ClientHandler extends Thread {
		//Variable Declaration
		private Socket client;
		private PrintWriter output;
		private BufferedReader input;
		private int player;
		private Server server;
		private String orbs;

		public ClientHandler(Socket socket, int player, Server server, String orbs) {
			//Initiliazing variables
			this.client = socket;
			this.player = player;
			this.server = server;
			this.start();
			this.orbs = orbs;
		}

		/**
		 * run
		 * This method receives messages from the clients to update the game accordingly
		 * @param null
		 * @return void
		 */
		public void run() {
	
			//Set up communication tools
			try {
				input = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
				output = new PrintWriter(this.client.getOutputStream());

				writers.add(output);

				output.println(this.player);
				output.println(this.orbs);

				while (ServerMain.isRunThreads()) {
					try {
						//Identify which player the command came from
						if (this.player == 1) {
							playerCommand = ("1" + input.readLine());
						} else if (this.player == 2) {
							playerCommand = ("2" + input.readLine());
						}

						//Updates game according to what the command comprises
						if (playerCommand.contains("RESTART")) {
							this.server.restart(playerCommand);
						} else if (playerCommand.contains("MOUSE")) {
							this.server.moveTankHead(playerCommand);
						} else if (playerCommand.contains("PRESS") || playerCommand.contains("RELEASE")) {
							this.server.moveTankVelocities(playerCommand);
						} else if (playerCommand.contains("CLICK")) {
							this.server.shootCannonballs(playerCommand);
						}

					} catch (IOException e) {	//A player has disconnected from the server
						GUI.setDisplayMessage(
								"Connection lost with player " + this.player + ", please restart server and clients");
						this.server.gui.repaint();
						ServerMain.setRunThreads(false);

					}

				}

			} catch (IOException e) {	//A player has disconnected from the server
				GUI.setDisplayMessage(
						"Connection lost with player " + this.player + ", please restart server and clients");
				this.server.gui.repaint();
				ServerMain.setRunThreads(false);
			}

		}
	}

	public ArrayList<Tanks> getTankList() {
		return tankList;
	}

	public void setTankList(ArrayList<Tanks> tankList) {
		this.tankList = tankList;
	}

	public int getMinX() {
		return minX;
	}

	public void setMinX(int minX) {
		this.minX = minX;
	}

	public int getMaxX() {
		return maxX;
	}

	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	public int getMinY() {
		return minY;
	}

	public void setMinY(int minY) {
		this.minY = minY;
	}

	public int getMaxY() {
		return maxY;
	}

	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

	public ServerSocket getServerSock() {
		return serverSock;
	}

	public void setServerSock(ServerSocket s) {
		this.serverSock = s;
	}

	public GUI getGui() {
		return gui;
	}

	public void setGui(GUI gui) {
		this.gui = gui;
	}

	public ArrayList<PrintWriter> getWriters() {
		return writers;
	}

	public void setWriters(ArrayList<PrintWriter> writers) {
		this.writers = writers;
	}

	public ArrayList<Ball> getBallList() {
		return ballList;
	}

	public void setBallList(ArrayList<Ball> ballList) {
		this.ballList = ballList;
	}

	public String getPlayerCommand() {
		return playerCommand;
	}

	public void setPlayerCommand(String playerCommand) {
		this.playerCommand = playerCommand;
	}

	public boolean isPlayerOneRestart() {
		return playerOneRestart;
	}

	public void setPlayerOneRestart(boolean playerOneRestart) {
		this.playerOneRestart = playerOneRestart;
	}

	public boolean isPlayerTwoRestart() {
		return playerTwoRestart;
	}

	public void setPlayerTwoRestart(boolean playerTwoRestart) {
		this.playerTwoRestart = playerTwoRestart;
	}

	public int getOrbCount() {
		return orbCount;
	}

	public void setOrbCount(int orbCount) {
		this.orbCount = orbCount;
	}

	public static int getPortNumber() {
		return portNumber;
	}

	public static void setPortNumber(int portNumber) {
		Server.portNumber = portNumber;
	}
	
	

}
