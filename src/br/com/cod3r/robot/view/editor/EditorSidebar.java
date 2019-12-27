package br.com.cod3r.robot.view.editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class EditorSidebar extends JPanel {

	private final List<BiConsumer<String, Integer>> functions = new ArrayList<>();
	
	public EditorSidebar() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(newAddTextButton("start", "[[startHere]]", 48, 0));
		add(newAddTextButton("b", "[[b]]", 49, 0));
		add(newAddTextButton("b,e", "[[b]][[e]]", 50, 0));
		add(newAddTextButton("e,b", "[[e]][[b]]", 51, 0));
		add(newAddTextButton("b,2e", "[[b]][[2e]]", 52, 0));
		add(newAddTextButton("b,v,2e", "[[b]][[v]][[2e]]", 53, 0));
		add(newAddTextButton("e", "[[e]]", 54, 0));
		add(newAddTextButton("t", "[[t]]", 55, 0));
		add(newAddTextButton("esc", "[[esc]]", 56, 0));
		add(newAddTextButton("end", "[[end]]", 57, 0));
		add(newAddTextButton("/", "[[//  ]]", 47, -3));
	}
	
	public void onEvent(BiConsumer<String, Integer> fn) {
		functions.add(fn);
	}

	private JButton newAddTextButton(final String label, final String text, final int keycode,
			final int caretPositionDiff) {
		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				functions.forEach(fn -> {
					fn.accept(text, caretPositionDiff);
				});
			}

		};
		
		KeyStroke keyStroke = KeyStroke.getKeyStroke(keycode, InputEvent.CTRL_DOWN_MASK);
		action.putValue(Action.ACCELERATOR_KEY, keyStroke);

		JButton button = new JButton(String.format("[%s] %s", (char) keycode, label));
		button.addActionListener(action);
		Dimension d = new Dimension(80, 33);
		button.setMinimumSize(d);
		button.setPreferredSize(d);
		button.setMaximumSize(d);

		button.getActionMap().put("action", action);
		button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY),
				"action");

		return button;
	}
}
