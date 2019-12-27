package br.com.cod3r.robot.view.editor;

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

import br.com.cod3r.robot.helper.OS;

@SuppressWarnings("serial")
public class EditorToolbar extends JPanel {

	private final JButton copyFileName = new JButton("FN");
	private final JTextField minSpeed = new JTextField("90", 4);
	private final JTextField maxSpeed = new JTextField("130", 4);
	private final JButton newButton = new JButton("Novo");
	private final JButton loadButton = new JButton("Carregar");
	private final JButton saveButton = new JButton("Salvar");
	private final JButton prevFileButton = new JButton("<");
	private final JButton nextFileButton = new JButton(">");
	private final JButton fontUpButton = new JButton("+");
	private final JButton fontDownButton = new JButton("-");
	private final JButton helpButton = new JButton("?");
	private final JLabel labelValidationMode = new JLabel("Modo Validação: ");
	private final JCheckBox boxValidationMode = new JCheckBox();

	private final List<Consumer<EditorEvent>> functions = new ArrayList<>();

	public EditorToolbar() {
		super(new FlowLayout());
		
		registerListeners();
		registerHotkeys();

		add(copyFileName);
		add(minSpeed);
		add(maxSpeed);
		add(newButton);
		add(loadButton);
		add(saveButton);
		add(prevFileButton);
		add(nextFileButton);
		add(fontUpButton);
		add(fontDownButton);
		add(helpButton);
		add(labelValidationMode);
		add(boxValidationMode);
	}

	public void onEvent(Consumer<EditorEvent> fn) {
		functions.add(fn);
	}

	public long getMinSpeed() {
		return Long.parseLong(minSpeed.getText());
	}

	public void setMinSpeed(long newSpeed) {
		if (newSpeed > 0) {
			minSpeed.setText(Long.toString(newSpeed));
		}
	}

	public long getMaxSpeed() {
		return Long.parseLong(maxSpeed.getText());
	}

	public void setMaxSpeed(long newSpeed) {
		if (newSpeed > 0) {
			maxSpeed.setText(Long.toString(newSpeed));
		}
	}

	public boolean isValidationMode() {
		return boxValidationMode.isSelected();
	}

	private void registerListeners() {
		newButton.addActionListener(e -> {
			functions.forEach(fn -> {
				fn.accept(EditorEvent.NEW_FILE);
			});
		});

		loadButton.addActionListener(e -> {
			functions.forEach(fn -> {
				fn.accept(EditorEvent.LOAD_FILE);
			});
		});

		saveButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				functions.forEach(fn -> {
					fn.accept(EditorEvent.SAVE_FILE);
				});
			}
		});
	
		prevFileButton.addActionListener(e -> {
			functions.forEach(fn -> {
				fn.accept(EditorEvent.PREV_FILE);
			});
		});

		nextFileButton.addActionListener(e -> {
			functions.forEach(fn -> {
				fn.accept(EditorEvent.NEXT_FILE);
			});
		});
		 
		fontUpButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				functions.forEach(fn -> {
					fn.accept(EditorEvent.FONT_INCREASE);
				});
			}
		});

		fontDownButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				functions.forEach(fn -> {
					fn.accept(EditorEvent.FONT_DECREASE);
				});
			}
		});

		helpButton.addActionListener(e -> {
			functions.forEach(fn -> {
				fn.accept(EditorEvent.HELP);
			});
		});

		boxValidationMode.addActionListener(e -> {
			if (boxValidationMode.isSelected()) {
				minSpeed.setText("35");
				maxSpeed.setText("50");
			} else {
				minSpeed.setText("90");
				maxSpeed.setText("130");
			}
		});

		copyFileName.addActionListener(e -> {
			functions.forEach(fn -> {
				fn.accept(EditorEvent.COPY_FILE_NAME);
			});
		});
	}
	
	private void registerHotkeys() {
		KeyStroke saveKey = OS.isMac() ? 
				KeyStroke.getKeyStroke("meta S") : 
				KeyStroke.getKeyStroke("control S");
		
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
}
