/**
 * @class_name Client
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This class serves as the client so communication with the server is possible
 */

package ip.milton.cue.world;

import ip.milton.cue.execution.CueMain;
import ip.milton.cue.objects.Tanks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;

import javax.swing.JFrame;

public class Client implements Runnable {

	//Variable Declaration
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	
	private Integer player;

	private GameArena game;

	private JFrame frame;

	public Client(GameArena game, String hostName, int portNumber, JFrame frame) {
		this.game = game;
		this.frame = frame;

		String host = hostName;
		
		//Attempts to connect to the server
		try {
			socket = new Socket(host, portNumber);

			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream());

			//Receives which player we are from server
			final String PLAYER = input.readLine();
			this.player = Integer.parseInt(PLAYER);

			//Receives the spawn positions of the orbs
			final String ORBS = input.readLine();

			game.setOrbs(ORBS);

			//Starting thread...
			Thread thread = new Thread(this);
			thread.start();
		} catch (IOException e) {
			// Do nothing
			// This exception is already covered by an error screen elsewhere
		} catch (IllegalArgumentException ie){
			// Do nothing
			// This exception is already covered by an error screen elsewhere
		}
	}

	/**
	 * run
	 * This method receives the information about the game from the server and responds accodingly
	 * @param null
	 * @return void
	 */
	@Override
	public void run() {
		while (CueMain.isRunThreads()) {
			String command;
			//Receiving messages from server
			try {
				command = input.readLine();
				//Game should restart
				if (command.equals(("RESTART"))) {
					this.game.getEndPanel().setVisible(false);
					this.game.setVisible(true);
					this.game.requestFocusInWindow();
				} else {
					this.updateGame(command);	//Game is on-going
				}
			} catch (IOException e) {
				game.setVisible(false);
				GUI.setErrorMessage("Connection dropped with server, restart and try again");
				this.frame.add(GUI.getErrorPanel());
				GUI.getErrorPanel().revalidate();
				GUI.getErrorPanel().repaint();
				GUI.getErrorPanel().setVisible(true);
			}
		}
	}

	/**
	 * updateGame
	 * This method updates the position of everything in the game
	 * @param String - A string containing the x y of objects as well as other game information
	 * @return void
	 */
	public void updateGame(String command) {
		int tankIndexCount = 0;
		final int TANK_COUNT = 2;
		for (int i = 0; i < TANK_COUNT; i++) {
			Tanks tank = this.game.getTankList()
					.get(Character.getNumericValue(command.charAt(command.indexOf("*", tankIndexCount) - 1)) - 1);

			//Index to differentiate which part of string means what
			int tankIndex = command.indexOf("*", tankIndexCount);
			int slainIndex = command.indexOf("slain|", tankIndexCount);

			// x and y coordinates are padded with zeros to 5 decimal places
			tank.setX(Integer.parseInt(command.substring(tankIndex + 1, tankIndex + 1 + 5)));
			tank.setY(Integer.parseInt(command.substring(tankIndex + 1 + 5, tankIndex + 1 + 5 + 5)));
			
			tank.setMouseRadians(Double.parseDouble(command.substring(tankIndex + 1 + 5 + 5, slainIndex)));

			tank.setSlain(Boolean.valueOf(command.substring(slainIndex + 6, slainIndex + 6 + 4)));	//Add 6 because "slain|" is 6 characters

			tankIndexCount = slainIndex + 6 + 4;
		}

		int orbIndex = command.indexOf("orbs|") + 5; //Add 5 because "orbs|" is 5 characters

		//Loops equal to amount of orbs existing
		for (int i = 0, p = Integer.parseInt(command.substring(command.indexOf("orbs|") - 1, command.indexOf("orbs|"))); i < p; i++) {
			int x = -1;
			int y = -1;
			try {
				x = (Integer.parseInt(command.substring(orbIndex, orbIndex + 5)));
				orbIndex += 5;
				y = (Integer.parseInt(command.substring(orbIndex, orbIndex + 5)));
				orbIndex += 5;
			} catch (NumberFormatException e) {
				// Do nothing, command string will rarely not match fixed formatting, bears no effect on performance and is not noticeable 
			} catch (StringIndexOutOfBoundsException e) {
				// Do nothing, command string will rarely not match fixed formatting, bears no effect on performance and is not noticeable
			}
			this.game.setOrbArray(i, x, y);	//Updating x y coordinates of orbs
		}

		//Only update cannonball x y when there are cannonballs
		int ballCount = 0;
		int cBallIndex = 0;
		if (command.indexOf("cBall|") != -1) {
			ballCount = Character.getNumericValue(command.charAt(command.length() - 1));
			cBallIndex = command.indexOf("cBall|") + 6;
		}

		int ballCounter = 0;

		int x = -1;
		int y = -1;

		//Loops equals to amount of cannonballs existing
		for (int i = 0; i < ballCount; i++) {

			try {
				//x y coordinates of cannonball padded with zeros to 5 decimal places
				x = Integer.parseInt(command.substring(cBallIndex, cBallIndex + 5));
				cBallIndex += 5;

				y = Integer.parseInt(command.substring(cBallIndex, cBallIndex + 5));
				cBallIndex += 5;
			} catch (NumberFormatException e) {
				// Do nothing, command string will rarely not match fixed formatting, bears no effect on performance and is not noticeable 
			} catch (StringIndexOutOfBoundsException e) {
				// Do nothing, command string will rarely not match fixed formatting, bears no effect on performance and is not noticeable 
			}

			//Update cannonball x y 
			synchronized (CueMain.getLocka()) {
				this.game.setcBallArray(i, x, y);
			}

			ballCounter++;
		}

		//Fills in rest of array with - 1 to effectively 'delete' cannonballs, since array size is not dynamic
		for (int i = ballCounter, p = this.game.getcBallArray().length; i < p; i++) {
			this.game.setcBallArray(i, -1, -1);
		}

	}

	/**
	 * sendKeyPress
	 * This method sends the key pressed to the server
	 * @param String - A string containing the key pressed and the velocity that should updated
	 * @return void
	 */
	public void sendKeyPress(String press) {
		synchronized (CueMain.getLockb()) {
			output.println("PRESS |" + press);
			output.flush();
		}
	}

	/**
	 * sendKeyRelease
	 * This method sends the key released to the server
	 * @param String - A string containing the key released and the velocity that should updated
	 * @return void
	 */
	public void sendKeyRelease(String release) {
		synchronized (CueMain.getLockb()) {
			output.println("RELEASE |" + release);
			output.flush();
		}
	}

	/**
	 * sendRadians
	 * This method sends the angle between tank and the cursor relative to the x-axis to the server
	 * @param double - The angle in radians
	 * @return void
	 */
	public void sendRadians(double radians) {
		synchronized (CueMain.getLockb()) {
			output.println("MOUSE |" + radians);
			output.flush();
		}
	}

	/**
	 * sendRestart
	 * This method sends a restart request to the server
	 * @param null
	 * @return void
	 */
	public void sendRestart() {
		synchronized (CueMain.getLockb()) {
			output.println("RESTART| ");
			output.flush();
		}
	}

	/**
	 * sendCannonball
	 * This method sends a 'click' to the server
	 * @param null
	 * @return void
	 */
	public void sendCannonball() {
		synchronized (CueMain.getLockb()) {
			output.println("CLICK |");
			output.flush();
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket s) {
		this.socket = s;
	}

	public BufferedReader getInput() {
		return input;
	}

	public void setInput(BufferedReader i) {
		this.input = i;
	}

	public PrintWriter getOutput() {
		return output;
	}

	public void setOutput(PrintWriter o) {
		this.output = o;
	}

	public Integer getPlayer() {
		return player;
	}

	public void setPlayer(Integer p) {
		this.player = p;
	}

}
