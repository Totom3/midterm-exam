package me.tomer.midterm;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The main panel for the midterm exam of the GUI programming class at Vanier,
 * Fall 2018.
 *
 * @author Tomer Moran
 * @date October 2nd, 2018
 */
public class MainPanel extends JPanel {

	/**
	 * Provides the format for weather status message. Arguments:
	 * <ol>
	 * <li>Time: Today/Tonight;</li>
	 * <li>Weather: clear sky/raining/snowing;</li>
	 * <li>Temperature value;</li>
	 * <li>Temperature unit (C/F).</li>
	 * </ol>
	 */
	private static final String MESSAGE_FORMAT = "%s is %s with a temperature of %.1f °%s";

	/**
	 * Provides the format for an invalid temperature error. Arguments:
	 * <ol>
	 * <li>The entered temperature.</li>
	 * </ol>
	 */
	private static final String ERR_BAD_TEMP = "ERROR: invalid temperature '%s'";

	/**
	 * The font used for the parameter labels.
	 */
	private static final Font FONT_TEXT = new Font("Calibri", Font.PLAIN, 16);

	/**
	 * The font used for the title label.
	 */
	private static final Font FONT_TITLE = new Font("Calibri", Font.BOLD, 24);

	/**
	 * Font for the status message when there is no error.
	 */
	private static final Font FONT_NORMAL_MESSAGE = new Font("Calibri", Font.PLAIN, 14);

	/**
	 * Font for the status message when there is an error.
	 */
	private static final Font FONT_ERROR_MESSAGE = new Font("Calibri", Font.BOLD, 14);

	/**
	 * The image to display when an error occurs.
	 */
	private static final BufferedImage ERROR_IMAGE;

	/**
	 * Holds all the weather status images.
	 */
	private static final Map<Integer, BufferedImage> IMAGES = new HashMap<>();

	private final JFrame frame;
	private final JLabel messageLabel;
	private final JTextField tempField;
	private final JCheckBox nightCheck;
	private final ImagePanel imagePanel;
	private final JCheckBox fahrenheitCheck;
	private final JCheckBox precipitationCheck;

	private MainPanel(JFrame frame) {
		this.frame = frame;
		
		// Instantiate GUI components
		this.messageLabel = new JLabel();
		this.imagePanel = new ImagePanel();
		this.tempField = new JTextField(3);
		this.nightCheck = new JCheckBox("Night?");
		this.fahrenheitCheck = new JCheckBox("Fahrenheit?");
		this.precipitationCheck = new JCheckBox("Precipitations?");

		initGUI();
		resetDefaults();
	}

	/**
	 * Initializes the GUI.
	 */
	private void initGUI() {
		setLayout(new GridBagLayout());

		// 1. Initialize components
		JLabel titleLabel = new JLabel("Weather Forecast");
		JLabel tempLabel = new JLabel("Degrees");
		JButton setIconButton = new JButton("Set Icon");
		JButton todayButton = new JButton("Today's Weather");

		// Initialize menu
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem openMenuItem = new JMenuItem("Override Icon");
		JMenuItem quitMenuItem = new JMenuItem("Quit");

		// 2. Style components
		tempLabel.setFont(FONT_TEXT);
		nightCheck.setFont(FONT_TEXT);
		fahrenheitCheck.setFont(FONT_TEXT);
		precipitationCheck.setFont(FONT_TEXT);

		todayButton.setFont(FONT_TEXT);
		setIconButton.setFont(FONT_TEXT);

		titleLabel.setFont(FONT_TITLE);
		messageLabel.setFont(FONT_NORMAL_MESSAGE);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		// Add action listeners & accelerators
		setIconButton.addActionListener(ae -> this.updateDisplay());
		todayButton.addActionListener(ae -> this.resetDefaults());
		openMenuItem.addActionListener(ae -> this.overrideIcon());
		quitMenuItem.addActionListener(ae -> System.exit(0));

		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));

		// 3. Build GUI
		final Insets parametersInsets = new Insets(10, 50, 10, 5);
		final int WEST = GridBagConstraints.WEST;
		final int CENTER = GridBagConstraints.CENTER;
		final int NORTHWEST = GridBagConstraints.NORTHWEST;
		this.add(titleLabel,	fillX(	constr(0, 0, 4, 1, CENTER,		new Insets(20, 5, 30, 5))));
		this.add(imagePanel, fillBoth(	constr(2, 1, 2, 4, CENTER,		new Insets(0, 0, 0, 20))));
		this.add(tempField,				constr(0, 1, 1, 1, NORTHWEST,	new Insets(10, 50, 10, 0)));
		this.add(tempLabel,				constr(1, 1, 1, 1, WEST,		new Insets(10, 5, 10, 5)));
		this.add(precipitationCheck,	constr(0, 2, 2, 1, NORTHWEST,	parametersInsets));
		this.add(nightCheck,			constr(0, 3, 2, 1, NORTHWEST,	parametersInsets));
		this.add(fahrenheitCheck,		constr(0, 4, 2, 1, NORTHWEST,	parametersInsets));
		this.add(setIconButton,	fillX(	constr(0, 5, 2, 1, CENTER,		new Insets(10, 20, 10, 20))));
		this.add(todayButton,	fillX(	constr(2, 5, 2, 1, CENTER,		new Insets(10, 0, 10, 20))));
		this.add(messageLabel,	fillX(	constr(0, 6, 4, 1, CENTER,		new Insets(10, 5, 10, 5))));

		// Build menu
		fileMenu.add(openMenuItem);
		fileMenu.add(quitMenuItem);
		menuBar.add(fileMenu);
		frame.setJMenuBar(menuBar);
	}

	/**
	 * Resets all settings to default:
	 * <ul>
	 * <li>10 degrees Celsius;</li>
	 * <li>Day time;</li>
	 * <li>Clear sky.</li>
	 * </ul>
	 */
	private void resetDefaults() {
		tempField.setText("10");
		nightCheck.setSelected(false);
		fahrenheitCheck.setSelected(false);
		precipitationCheck.setSelected(true);

		updateDisplay();
	}

	/**
	 * Updates the status message and the image icon to the appropriate
	 * settings.
	 */
	private void updateDisplay() {
		// Check temperature
		double temp;
		try {
			temp = Double.parseDouble(tempField.getText());
		} catch (NumberFormatException ex) {
			imagePanel.setImage(ERROR_IMAGE);
			messageLabel.setForeground(Color.RED);
			messageLabel.setFont(FONT_ERROR_MESSAGE);
			messageLabel.setText(String.format(ERR_BAD_TEMP, tempField.getText()));
			return;
		}

		// Extract parameters
		boolean night = nightCheck.isSelected();
		boolean precipitations = precipitationCheck.isSelected();
		boolean fahrenheit = fahrenheitCheck.isSelected();
		boolean positiveTemp = fahrenheit ? (temp > 32) : (temp > 0);

		// Build status message
		String time = night ? "Tonight" : "Today";
		String weather = precipitations ? (positiveTemp ? "raining" : "snowing") : "clear sky";
		String units = fahrenheit ? "F" : "C";

		// Update message
		messageLabel.setForeground(Color.BLACK);
		messageLabel.setFont(FONT_NORMAL_MESSAGE);
		messageLabel.setText(String.format(MESSAGE_FORMAT, time, weather, temp, units));

		// Update image
		imagePanel.setImage(IMAGES.get(imageKey(precipitations, positiveTemp, night)));
	}

	/**
	 * Opens the override icon file prompt and changes the icon accordingly.
	 */
	private void overrideIcon() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("images/"));
		fc.setFileFilter(new FileNameExtensionFilter("Images", "png", "jpg", "jpeg"));

		if (fc.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		BufferedImage img;
		File file = fc.getSelectedFile();
		try {
			img = ImageIO.read(file);
		} catch (IOException ex) {
			ex.printStackTrace();
			messageLabel.setForeground(Color.RED);
			messageLabel.setFont(FONT_ERROR_MESSAGE);
			messageLabel.setText("ERROR: could not open file.");
			imagePanel.setImage(ERROR_IMAGE);
			return;
		}

		imagePanel.setImage(img);
	}

	// =============================================================
	/**
	 * Loads all the weather status images.
	 */
	static {
		try {
			// Load images
			BufferedImage clearDay = loadImage("images/clear.png");
			BufferedImage clearNight = loadImage("images/clear_night.png");
			BufferedImage rainingDay = loadImage("images/raining.png");
			BufferedImage rainingNight = loadImage("images/raining_night.png");
			BufferedImage snowingDay = loadImage("images/snowing.png");
			BufferedImage snowingNight = loadImage("images/snowing_night.png");

			// Place images in map
			IMAGES.put(imageKey(false, true, false), clearDay);
			IMAGES.put(imageKey(false, false, false), clearDay);
			IMAGES.put(imageKey(false, true, true), clearNight);
			IMAGES.put(imageKey(false, false, true), clearNight);
			IMAGES.put(imageKey(true, true, false), rainingDay);
			IMAGES.put(imageKey(true, false, false), snowingDay);
			IMAGES.put(imageKey(true, true, true), rainingNight);
			IMAGES.put(imageKey(true, false, true), snowingNight);

			ERROR_IMAGE = loadImage("images/error.png");
		} catch (IOException ex) {
			// Should never happen
			throw new AssertionError(ex);
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			throw new AssertionError(ex);
		}

		JFrame frame = new JFrame();
		frame.setTitle("Weather Forecast");
		frame.setSize(550, 470);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(new MainPanel(frame));
		frame.setVisible(true);
	}

	/**
	 * Maps the given parameters to an integer key used to retrieve or place an
	 * image in the image map. The key is computed as follows:
	 * <pre>
	 * int key =  (night ? 0x1 : 0) | (precipitation ? 0x2 : 0) | (positiveTemp ? 0x4 : 0);
	 * </pre>
	 *
	 * @param precipitation whether or not the precipitation check box is
	 * selected.
	 * @param positiveTemp whether or not the temperature is greater than 0°C
	 * (32°F).
	 * @param night whether or not the night check box is selected.
	 * @return an integer key which is unique for every distinct combination of
	 * parameters.
	 */
	private static int imageKey(boolean precipitation, boolean positiveTemp, boolean night) {
		return (night ? 0x1 : 0) | (precipitation ? 0x2 : 0) | (positiveTemp ? 0x4 : 0);
	}

	/**
	 * Utility method to load an image from a given file path.
	 *
	 * @param path the path of the file to load the image from.
	 * @return the loaded {@link BufferedImage}.
	 * @throws IOException if an exception occurs.
	 */
	private static BufferedImage loadImage(String path) throws IOException {
		return ImageIO.read(new File(path));
	}

	/**
	 * Utility method to instantiate a {@code GridBagConstraints} given the
	 * following parameters.
	 *
	 * @param x the x coordinate of the cell.
	 * @param y the y coordinate of the cell.
	 * @param width the number of columns to span.
	 * @param height the number of rows to span.
	 * @param anchor where to anchor the component in the cell (see
	 * {@link GridBagConstaints} constants).
	 * @param ins the insets of the cell.
	 * @return a new {@code GridBagConstraints} object which meets the given
	 * parameters.
	 */
	private static GridBagConstraints constr(int x, int y, int width, int height, int anchor, Insets ins) {
		GridBagConstraints constr = new GridBagConstraints();
		constr.gridx = x;
		constr.gridy = y;
		constr.gridwidth = width;
		constr.gridheight = height;
		constr.insets = ins;
		constr.anchor = anchor;

		return constr;
	}

	/**
	 * Modifies a given {@code GridBagConstraints} so that it fills the
	 * horizontal space.
	 *
	 * @param constr the constraints to modify.
	 * @return the same {@code GridBagConstraints} instance.
	 */
	private static GridBagConstraints fillX(GridBagConstraints constr) {
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.weightx = 1;
		return constr;
	}

	/**
	 * Modifies a given {@code GridBagConstraints} so that it fills the
	 * horizontal and vertical space.
	 *
	 * @param constr the constraints to modify.
	 * @return the same {@code GridBagConstraints} instance.
	 */
	private static GridBagConstraints fillBoth(GridBagConstraints constr) {
		constr.fill = GridBagConstraints.BOTH;
		constr.weightx = 1;
		constr.weighty = 1;
		return constr;
	}
}
