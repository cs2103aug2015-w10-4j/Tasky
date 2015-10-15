package ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import global.Task;
import ui.formatter.TaskListFormatter;

public class UI {
	
	/*
	 * Roughly, the UI can be modeled as the following grid :
	 * 
	 * +---+---+---+
	 * | 1 | 2 | 3 |
	 * +---+---+---+
	 * | 4 | 5 | 6 |
	 * +---+---+---+
	 * | 7 | 8 | 9 |
	 * +---+---+---+
	 * 
	 * where :
	 *  > displayArea occupies cell 1-3
	 *  > promptLabel occupies cell 4
	 *  > userInputField occupies cell 5-6
	 *  > statusBar occupies cell 7-9
	 */
	
	/*
	 * Declaration of variables
	 */
	private Logger logger = Logger.getGlobal();
	
	private static final int DISPLAY_ROW_COUNT = 30;
	private static final int DISPLAY_COLUMN_COUNT = 60;
	private static final int USER_INPUT_FIELD_CHAR_COUNT = 50;
	private static final int PROMPT_LABEL_CHAR_COUNT = 10;
	
	private static final int DISPLAY_AREA_POS_Y = 0;
	private static final int DISPLAY_AREA_POS_X = 0;
	private static final int DISPLAY_AREA_LEN_Y = 1;
	private static final int DISPLAY_AREA_LEN_X = 3;
	
	private static final int PROMPT_LABEL_POS_Y = 1;
	private static final int PROMPT_LABEL_POS_X = 0;
	private static final int PROMPT_LABEL_LEN_Y = 1;
	private static final int PROMPT_LABEL_LEN_X = 1;
	
	private static final int USER_INPUT_FIELD_POS_Y = 1;
	private static final int USER_INPUT_FIELD_POS_X = 1;
	private static final int USER_INPUT_FIELD_LEN_Y = 1;
	private static final int USER_INPUT_FIELD_LEN_X = 2;
	
	private static final int STATUS_BAR_POS_Y = 2;
	private static final int STATUS_BAR_POS_X = 0;
	private static final int STATUS_BAR_LEN_Y = 1;
	private static final int STATUS_BAR_LEN_X = 3;
	
	private static final String FRAME_TITLE = "Tasky";
	private static final String DEFAULT_PROMPT = "command ";
	private static final String DISPLAY_AREA_FONT_NAME = "Lucida Console";
	private static final int DISPLAY_AREA_FONT_SIZE = 12;
	
	/*
	 * Initialization of GUI variables
	 */
	private JFrame frame = new JFrame(FRAME_TITLE);
	private JTextArea displayArea = new JTextArea();
	private JLabel promptLabel = new JLabel(DEFAULT_PROMPT, PROMPT_LABEL_CHAR_COUNT);
	private JTextField userInputField = new JTextField(USER_INPUT_FIELD_CHAR_COUNT);
	private StatusBar statusBar = new StatusBar();
	
	private TaskListFormatter taskListFormatter = TaskListFormatter.getInstance();
	
	/*
	 * Constructor
	 */
	public UI() {
		prepareComponents();
		addComponentsToPane(frame.getContentPane());
		displayFrame();
	}
	
	private void addComponentsToPane(Container contentPane) {
		addDisplayArea(contentPane);
		addPromptLabel(contentPane);
		addUserInputField(contentPane);
		addStatusBar(contentPane);
	}

	private void addStatusBar(Container contentPane) {
		GridBagConstraints constraint = new GridBagConstraints();
		
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.gridx = STATUS_BAR_POS_X;
		constraint.gridy = STATUS_BAR_POS_Y;
		constraint.gridheight = STATUS_BAR_LEN_Y;
		constraint.gridwidth = STATUS_BAR_LEN_X;
		
		logger.log(Level.CONFIG, String.format("%s: fill = %s, gridx = %d, gridy = %d, "
				+ "gridheight = %d, gridwidth = %d", "STATUS_BAR", "GridBagConstraints.HORIZONTAL",
				STATUS_BAR_POS_X, STATUS_BAR_POS_Y, STATUS_BAR_LEN_Y, STATUS_BAR_LEN_X));
	
		contentPane.add(statusBar, constraint);
	}

	private void addUserInputField(Container contentPane) {
		GridBagConstraints constraint = new GridBagConstraints();
		
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.gridx = USER_INPUT_FIELD_POS_X;
		constraint.gridy = USER_INPUT_FIELD_POS_Y;
		constraint.gridheight = USER_INPUT_FIELD_LEN_Y;
		constraint.gridwidth = USER_INPUT_FIELD_LEN_X;
		
		logger.log(Level.CONFIG, String.format("%s: fill = %s, gridx = %d, gridy = %d, "
				+ "gridheight = %d, gridwidth = %d", "USER_INPUT_FIELD", "GridBagConstraints.HORIZONTAL",
				USER_INPUT_FIELD_POS_X, USER_INPUT_FIELD_POS_Y,
				USER_INPUT_FIELD_LEN_Y, USER_INPUT_FIELD_LEN_X));
		
		contentPane.add(userInputField, constraint);
	}

	private void addPromptLabel(Container contentPane) {
		GridBagConstraints constraint = new GridBagConstraints();
		
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.gridx = PROMPT_LABEL_POS_X;
		constraint.gridy = PROMPT_LABEL_POS_Y;
		constraint.gridheight = PROMPT_LABEL_LEN_Y;
		constraint.gridwidth = PROMPT_LABEL_LEN_X;
		
		logger.log(Level.CONFIG, String.format("%s: fill = %s, gridx = %d, gridy = %d, "
				+ "gridheight = %d, gridwidth = %d", "PROMPT_LABEL", "GridBagConstraints.HORIZONTAL",
				PROMPT_LABEL_POS_X, PROMPT_LABEL_POS_Y, PROMPT_LABEL_LEN_Y, PROMPT_LABEL_LEN_X));
		
		contentPane.add(promptLabel, constraint);
	}

	private void addDisplayArea(Container contentPane) {
		GridBagConstraints constraint = new GridBagConstraints();
		
		constraint.fill = GridBagConstraints.BOTH;
		constraint.gridx = DISPLAY_AREA_POS_X;
		constraint.gridy = DISPLAY_AREA_POS_Y;
		constraint.gridheight = DISPLAY_AREA_LEN_Y;
		constraint.gridwidth = DISPLAY_AREA_LEN_X;
		constraint.weightx = 1.0;
		constraint.weighty = 1.0;
		
		logger.log(Level.CONFIG, String.format("%s: fill = %s, gridx = %d, gridy = %d, "
				+ "gridheight = %d, gridwidth = %d, weightx = %.2f, weighty = %.2f",
				"DISPLAY_AREA", "GridBagConstraints.HORIZONTAL",
				STATUS_BAR_POS_X, STATUS_BAR_POS_Y, STATUS_BAR_LEN_Y, STATUS_BAR_LEN_X, 1.0, 1.0));
		
		contentPane.add(displayArea, constraint);
	}

	private void displayFrame() {
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void prepareComponents() {
		prepareFrame();
		prepareUserInput();
		prepareDisplayArea();
		preparePromptLabel();
	}

	private void preparePromptLabel() {
		promptLabel.setHorizontalAlignment(SwingConstants.CENTER);
	}

	private void prepareDisplayArea() {
		displayArea.setEditable(false);
		displayArea.setFont(new Font(DISPLAY_AREA_FONT_NAME, Font.PLAIN, DISPLAY_AREA_FONT_SIZE));
		displayArea.setRows(DISPLAY_ROW_COUNT);
		displayArea.setColumns(DISPLAY_COLUMN_COUNT);
		displayArea.setPreferredSize(new Dimension(DISPLAY_ROW_COUNT, DISPLAY_COLUMN_COUNT));
	}

	private void prepareUserInput() {
		userInputField.setEditable(false);
		userInputField.setColumns(USER_INPUT_FIELD_CHAR_COUNT);
		
		userInputField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronized (userInputField) {
					userInputField.notify();
				}
			}
		});
		
		/*
		 * Clears userInputField when "esc" is pressed
		 */
		@SuppressWarnings("serial")
		Action clearText = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        userInputField.setText("");
		    }
		};
		userInputField.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), clearText);
	}

	private void prepareFrame() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridBagLayout());
		
		/*
		 * Focus given to userInputField when window is activated
		 */
		frame.addWindowListener(new WindowAdapter() {
	        public void windowActivated(WindowEvent e) {
	        	userInputField.grabFocus();
	        }
		});
	}

	/**
	 * Prompt message and obtain user input
	 * @param prompt message to prompt user
	 * @return userInput
	 * @throws InterruptedException
	 */
	public String promptUser(String prompt) throws InterruptedException {
		logger.info("Entering promptUser(prompt = " + prompt + ")");
		
		promptLabel.setText(prompt);
		userInputField.setEditable(true);
		userInputField.grabFocus();
		waitForUserInput();
		String userInput = userInputField.getText();
		userInputField.setText("");
		
		logger.info("Returning from promptUser");
		
		return userInput;
	}

	private void waitForUserInput() throws InterruptedException {
		synchronized (userInputField) {
			while (userInputField.getText().isEmpty()) {
				userInputField.wait();
			}
		}
	}
	
	/**
	 * Asks the UI to display content to user
	 * @param stringToShow
	 * @return true if successful
	 */
	public boolean showToUser(String stringToShow) {
		displayArea.setText(stringToShow);
		return true;
	}
	
	/**
	 * Asks the UI to display the list of tasks
	 * @param tasks
	 * @return true if successful
	 */
	public boolean showTasks(List<Task> taskList) {
		String textFormatTaskList = taskListFormatter.formatTaskList(taskList);
		showToUser(textFormatTaskList);
		return true;
	}
	
	/**
	 * Asks the UI to display content to user in the status bar
	 * @param stringToShow
	 * @return true if successful
	 */
	public boolean showStatusToUser(String stringToShow) {
		logger.info("Entering showStatusToUser(stringToShow=" + stringToShow + ")");
		
		statusBar.setText(stringToShow);
		
		logger.info("Returning from showStatusToUser");
		
		return true;
	}
	
}
