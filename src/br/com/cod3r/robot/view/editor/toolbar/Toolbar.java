package br.com.cod3r.robot.view.editor.toolbar;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import br.com.cod3r.robot.helper.OS;

@SuppressWarnings("serial")
public class Toolbar extends JPanel {
	
	private final JButton copyFileNameButton = new JButton("FN");
	private final JTextField minWaitingTimeField = new JTextField("90", 4);
	private final JTextField maxWaitingTimeField = new JTextField("130", 4);
	private final JButton newButton = new JButton("Novo");
	private final JButton loadButton = new JButton("Carregar");
	private final JButton saveButton = new JButton("Salvar");
	private final JButton prevFileButton = new JButton("<");
	private final JButton nextFileButton = new JButton(">");
	private final JButton fontUpButton = new JButton("+");
	private final JButton fontDownButton = new JButton("-");
	private final JButton helpButton = new JButton("?");
	private final JLabel validationModeLabel = new JLabel("Modo Validação: ");
	private final JCheckBox validationModeCheck = new JCheckBox();

	private final List<Consumer<ToolbarEvent>> functions = new ArrayList<>();

	public Toolbar() {
		super(new FlowLayout());

		registerListeners();
		registerHotkeys();

		add(copyFileNameButton);
		add(minWaitingTimeField);
		add(maxWaitingTimeField);
		add(newButton);
		add(loadButton);
		add(saveButton);
		add(prevFileButton);
		add(nextFileButton);
		add(fontUpButton);
		add(fontDownButton);
		add(helpButton);
		add(validationModeLabel);
		add(validationModeCheck);
	}

	public void onEvent(Consumer<ToolbarEvent> fn) {
		functions.add(fn);
	}

	private void registerListeners() {
		newButton.addActionListener(e -> {
			notifyListeners(ToolbarEventType.NEW_FILE);
		});

		loadButton.addActionListener(e -> {
			notifyListeners(ToolbarEventType.LOAD_FILE);
		});

		saveButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				notifyListeners(ToolbarEventType.SAVE_FILE);
			}
		});

		prevFileButton.addActionListener(e -> {
			notifyListeners(ToolbarEventType.PREV_FILE);
		});

		nextFileButton.addActionListener(e -> {
			notifyListeners(ToolbarEventType.NEXT_FILE);
		});

		fontUpButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				notifyListeners(ToolbarEventType.FONT_INCREASE);
			}
		});

		fontDownButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				notifyListeners(ToolbarEventType.FONT_DECREASE);
			}
		});

		helpButton.addActionListener(e -> {
			notifyListeners(ToolbarEventType.HELP);
		});

		validationModeCheck.addActionListener(e -> {
			if (validationModeCheck.isSelected()) {
				minWaitingTimeField.setText("35");
				maxWaitingTimeField.setText("50");
			} else {
				minWaitingTimeField.setText("90");
				maxWaitingTimeField.setText("130");
			}
			
			notifyListeners(ToolbarEventType.VALIDATION_MODE);
			notifyListeners(ToolbarEventType.MIN_WAITING_TIME);
			notifyListeners(ToolbarEventType.MAX_WAITING_TIME);
		});

		copyFileNameButton.addActionListener(e -> {
			notifyListeners(ToolbarEventType.COPY_FILE_NAME);
		});

		
		DocumentListener docListener = new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				update();
			}

			public void removeUpdate(DocumentEvent e) {
				update();
			}

			public void insertUpdate(DocumentEvent e) {
				update();
			}

			public void update() {
				notifyListeners(ToolbarEventType.MIN_WAITING_TIME);
				notifyListeners(ToolbarEventType.MAX_WAITING_TIME);
			}
		};
		
		minWaitingTimeField.getDocument().addDocumentListener(docListener);
		maxWaitingTimeField.getDocument().addDocumentListener(docListener);
	}

	private void registerHotkeys() {
		KeyStroke saveKey = OS.isMac() ? KeyStroke.getKeyStroke("meta S") : KeyStroke.getKeyStroke("control S");

		Action saveAction = (Action) saveButton.getActionListeners()[0];
		saveAction.putValue(Action.ACCELERATOR_KEY, saveKey);
		saveButton.getActionMap().put("saveAction", saveAction);
		saveButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put((KeyStroke) saveAction.getValue(Action.ACCELERATOR_KEY), "saveAction");

		Action fontUpAction = (Action) fontUpButton.getActionListeners()[0];
		fontUpAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.CTRL_DOWN_MASK));
		fontUpButton.getActionMap().put("fontUpAction", fontUpAction);
		fontUpButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put((KeyStroke) fontUpAction.getValue(Action.ACCELERATOR_KEY), "fontUpAction");

		Action fontDownAction = (Action) fontDownButton.getActionListeners()[0];
		fontDownAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK));
		fontDownButton.getActionMap().put("fontDownAction", fontDownAction);
		fontDownButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put((KeyStroke) fontDownAction.getValue(Action.ACCELERATOR_KEY), "fontDownAction");

	}
	
	private void notifyListeners(ToolbarEventType type) {
		functions.forEach(fn -> {
			String minWT = minWaitingTimeField.getText();
			String maxWT = maxWaitingTimeField.getText();
			boolean validationMode = validationModeCheck.isSelected();
			fn.accept(new ToolbarEvent(type, minWT, maxWT, validationMode));
		});
	}
}
