package components;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import persistence.FileStoreDB;
import concurrency.DecryptTask;
import concurrency.EncryptTask;

public class FolderEncrypt extends JPanel implements ActionListener {
	static private final String newline = "\n";
	private JButton srcPanelButton;
	private JTextArea log;
	private JFileChooser fc;
	private JTextField srcPanelText;
	private JTextField destPanelText;
	private JButton destPanelButton;
	private File source;
	private File destination;
	private String password;
	private JButton decryptButton;
	private JButton encryptButton;
	private static JFrame frame;

	public FolderEncrypt() {
		super(new GridBagLayout());
	}

	private void mainLayout() {
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		JPanel srcPanel = srcPanelLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = HORIZONTAL;
		c.weightx = 100;
		add(srcPanel, c);

		JPanel destPanel = destinationLayout();
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.fill = HORIZONTAL;
		c.weightx = 100;
		add(destPanel, c);
		
		JPanel commandPanel = commandPanelLayout();
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.fill = HORIZONTAL;
		c.weightx = 100;
		add(commandPanel, c);

		log = new JTextArea(5, 20);
		JScrollPane logScrollPane = new JScrollPane(log);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.fill = BOTH;
		c.weighty = 10;
		c.insets = new Insets(5,5,5,5);
		add(logScrollPane, c);
	}

	private JPanel srcPanelLayout() {
		JPanel srcPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JLabel srcPanelLabel = new JLabel("Source:");
		c.anchor = CENTER;
		c.ipadx = 10;
		c.ipady = 10;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5,5,5,5);
		srcPanel.add(srcPanelLabel, c);

		srcPanelText = new JTextField();
		c = new GridBagConstraints();
		c.fill = HORIZONTAL;
		c.weightx = 2;
		c.ipadx = 90;
		srcPanelText.setEditable(false);
		srcPanel.add(srcPanelText, c);

		srcPanelButton = new JButton("Browse...");
		c = new GridBagConstraints();
		c.ipadx = 10;
		c.insets = new Insets(5, 5, 5, 5);
		srcPanelButton.addActionListener(this);
		srcPanel.add(srcPanelButton, c);

		return srcPanel;
	}

	private JPanel commandPanelLayout() {
		JPanel commandPanel = new JPanel(new GridBagLayout());

		encryptButtonLayout(commandPanel);
		encryptButtonListener();
		
		decryptButtonLayout(commandPanel);
		decryptButtonListener();

		return commandPanel;
	}

	private void decryptButtonLayout(JPanel commandPanel) {
		GridBagConstraints c = new GridBagConstraints();
		decryptButton = new JButton();
		decryptButton.setText("Decrypt");
		c.fill = HORIZONTAL;
		c.weightx = 1;
		c.insets = new Insets(5,5,5,5);
		commandPanel.add(decryptButton, c);
	}

	private void decryptButtonListener() {
		decryptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(validTask()) {
					password = passwordDialog();
					if(validPassword()) {
						new DecryptTask(FolderEncrypt.this).execute();
					}
				}
			}
		});
	}

	private void encryptButtonLayout(JPanel commandPanel) {
		GridBagConstraints c = new GridBagConstraints();
		encryptButton = new JButton();
		encryptButton.setText("Encrypt");
		c.fill = HORIZONTAL;
		c.weightx = 1;
		c.insets = new Insets(5,5,5,5);
		commandPanel.add(encryptButton, c);
	}

	private void encryptButtonListener() {
		encryptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(validTask()) {
					password = passwordDialog();
					if(validPassword()) {
						new EncryptTask(FolderEncrypt.this).execute();
					}
				}
			}
		});
	}

	private String passwordDialog() {
		String message = String.format("Source: %s\nDestination: %s\n\nEnter Password:", source.getAbsolutePath(), destination.getAbsolutePath());
		return (String)JOptionPane.showInputDialog(FolderEncrypt.this, message,
		                    "Password",
		                    JOptionPane.PLAIN_MESSAGE);
	}

	private JPanel destinationLayout() {
		JPanel destPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JLabel destPanelLabel = new JLabel("Destination:");
		c.anchor = CENTER;
		c.ipadx = 10;
		c.ipady = 10;
		c.insets = new Insets(5,5,5,5);
		c.fill = GridBagConstraints.NONE;
		destPanel.add(destPanelLabel, c);

		destPanelText = new JTextField();
		c = new GridBagConstraints();
		c.fill = HORIZONTAL;
		c.weightx = 2;
		c.ipadx = 90;
		destPanelText.setEditable(false);
		destPanel.add(destPanelText, c);

		destPanelButton = new JButton("Browse...");
		c = new GridBagConstraints();
		c.ipadx = 10;
		c.insets = new Insets(5, 5, 5, 5);
		destPanelButton.addActionListener(this);
		destPanel.add(destPanelButton, c);

		return destPanel;
	}

	public void actionPerformed(ActionEvent e) {
		if (sourceButtonPressed(e)) {
			showSourceFileBrowser();
		} else if (destButtonPressed(e)) {
			showDestFileBrowser();
		}
	}

	private void showDestFileBrowser() {
		int returnVal = fc.showOpenDialog(FolderEncrypt.this);
		if (approved(returnVal)) {
			File file = fc.getSelectedFile();
			log.append("Destination folder: " + file.getAbsolutePath()
					+ "." + newline);
			destination = file;
			destPanelText.setText(file.getAbsolutePath());
		} else {
			log.append("Operation cancelled by user." + newline);
		}
		log.setCaretPosition(log.getDocument().getLength());
	}

	private void showSourceFileBrowser() {
		int returnVal = fc.showOpenDialog(FolderEncrypt.this);

		if (approved(returnVal)) {
			File file = fc.getSelectedFile();
			log.append("Source folder: " + file.getAbsolutePath() + "."
					+ newline);
			source = file;
			srcPanelText.setText(file.getAbsolutePath());
		} else {
			log.append("Operation cancelled by user." + newline);
		}
		log.setCaretPosition(log.getDocument().getLength());
	}

	private boolean approved(int returnVal) {
		return returnVal == JFileChooser.APPROVE_OPTION;
	}

	private boolean destButtonPressed(ActionEvent e) {
		return e.getSource() == destPanelButton;
	}

	private boolean sourceButtonPressed(ActionEvent e) {
		return e.getSource() == srcPanelButton;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void startApplication() {
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		frame = new JFrame("FolderEncrypt");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Create and set up the content pane.
		FolderEncrypt app = new FolderEncrypt();
		app.mainLayout();
		app.setOpaque(true); // content panes must be opaque
		frame.setContentPane(app);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
		
		FileStoreDB.getInstance().getFileStore();
	}

	
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				startApplication();
			}
		});
	}
	public void sendMessage(String msg) {
		log.append(msg + newline);
	}

	public File getSrcDir() {
		return source;
	}

	public File getDestDir() {
		return destination;
	}

	public String getPassword() {
		return password;
	}

	public void enableButtons(boolean enable) {
		if(enable) {
			decryptButton.setEnabled(true);
			encryptButton.setEnabled(true);
		} else {
			decryptButton.setEnabled(false);
			encryptButton.setEnabled(false);
		}
	}
	
	public void clearTextFields() {
		srcPanelText.setText(null);
		source = null;
		destPanelText.setText(null);
		destination = null;
	}

	private boolean validTask() {
		if(source == null || destination ==  null) {
			log.append("Source or destination field is empty." + newline );
			return false;
		}
		if(!source.isDirectory() || !destination.isDirectory()) {
			log.append("Source or destination does not exist." + newline);
			return false;
		}
		return true;
	}

	private boolean validPassword() {
		if(password != null) {
			return true;
		} else {
			log.append("Operation cancelled. " + newline);
			return false;
		}
	}
	private void dispose() {
		FileStoreDB.getInstance().closeDB();
	}
}
