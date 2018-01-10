/**
 * @class_name Start
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This class implements an actionlistener for the start button
 */

package ip.milton.cue.execution;

import ip.milton.cue.world.GameArena;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Start implements ActionListener {
	
	//Variable Declaration
	private JPanel mainPanel;
	private JFrame frame;
	private JTextField hostNameField;
	private JTextField portField;

	public Start(JPanel mainPanel, JFrame frame, JTextField hostNameField, JTextField portField) {
		this.mainPanel = mainPanel;
		this.frame = frame;
		this.hostNameField = hostNameField;
		this.portField = portField;
	}

	/**
	 * actionPerformed
	 * When fired, the host name and port number is taken in and an attempt to connect to the server is made
	 * @param ActionEvent
	 * @return void
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		int port = -1;
		try {
			if (portField.getText().equals("")) {
				this.portField.setText("Invalid input, please enter a port number!");
			} else {
				port = Integer.parseInt(portField.getText());
				this.mainPanel.setVisible(false);
				new GameArena(this.frame, this.hostNameField.getText(), port);
			}
		} catch (NumberFormatException e1) {
			this.portField.setText("Invalid input, please enter a port number!");
		}

	}
}
