package br.com.cod3r.robot.view.config;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import br.com.cod3r.robot.helper.StoreData;
import br.com.cod3r.robot.text.TextListener;
import net.miginfocom.swing.MigLayout;

public class ConfigView extends JPanel implements HotKeyListener {

	private static final long serialVersionUID = 1L;

	private final List<TextListener> listeners = new ArrayList<TextListener>();

	private Provider keyListenerProvider;

	private JLabel labelReset = new JLabel("Reiniciar: ");
	private JTextField fieldReset = new JTextField("F2", 10);
	private JCheckBox checkResetAlt = new JCheckBox("Alt");
	private JCheckBox checkResetMeta = new JCheckBox("Meta");
	private JCheckBox checkResetCtrl = new JCheckBox("Ctrl");
	private JCheckBox checkResetShift = new JCheckBox("Shift");

	private JLabel labelNext = new JLabel("Avançar: ");
	private JTextField fieldNext = new JTextField("0", 10);
	private JCheckBox checkNextAlt = new JCheckBox("Alt");
	private JCheckBox checkNextMeta = new JCheckBox("Meta");
	private JCheckBox checkNextCtrl = new JCheckBox("Ctrl");
	private JCheckBox checkNextShift = new JCheckBox("Shift");

	private JLabel labelPrev = new JLabel("Retroceder: ");
	private JTextField fieldPrev = new JTextField(",", 10);
	private JCheckBox checkPrevAlt = new JCheckBox("Alt");
	private JCheckBox checkPrevMeta = new JCheckBox("Meta");
	private JCheckBox checkPrevCtrl = new JCheckBox("Ctrl");
	private JCheckBox checkPrevShift = new JCheckBox("Shift");

	private JLabel labelFindKey = new JLabel("Encontrar: ");
	private JTextField fieldFindKey = new JTextField("", 10);
	private JLabel labelFindKeyResult = new JLabel("");

	private JButton updateKeys = new JButton("Atualizar");

	public ConfigView() {
		loadData();

		setLayout(new BorderLayout());
		this.add(createFieldsPanel(), BorderLayout.CENTER);

		this.updateKeys.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				StoreData.getInstance().setResetKey(Integer.parseInt(fieldReset.getText()));
				StoreData.getInstance().setNextKey(Integer.parseInt(fieldNext.getText()));
				StoreData.getInstance().setPrevKey(Integer.parseInt(fieldPrev.getText()));
				
				int resetModifiers = checkResetAlt.isSelected() ? InputEvent.ALT_DOWN_MASK : 0;
				resetModifiers += checkResetMeta.isSelected() ? InputEvent.META_DOWN_MASK : 0;
				resetModifiers += checkResetCtrl.isSelected() ? InputEvent.CTRL_DOWN_MASK : 0;
				resetModifiers += checkResetShift.isSelected() ? InputEvent.SHIFT_DOWN_MASK : 0;
				StoreData.getInstance().setResetModifiers(resetModifiers);
				
				int nextModifiers = checkNextAlt.isSelected() ? InputEvent.ALT_DOWN_MASK : 0;
				nextModifiers  += checkNextMeta.isSelected() ? InputEvent.META_DOWN_MASK : 0;
				nextModifiers  += checkNextCtrl.isSelected() ? InputEvent.CTRL_DOWN_MASK : 0;
				nextModifiers += checkNextShift.isSelected() ? InputEvent.SHIFT_DOWN_MASK : 0;
				StoreData.getInstance().setNextModifiers(nextModifiers);

				int prevModifiers = checkPrevAlt.isSelected() ? InputEvent.ALT_DOWN_MASK : 0;
				prevModifiers  += checkPrevMeta.isSelected() ? InputEvent.META_DOWN_MASK : 0;
				prevModifiers  += checkPrevCtrl.isSelected() ? InputEvent.CTRL_DOWN_MASK : 0;
				prevModifiers += checkPrevShift.isSelected() ? InputEvent.SHIFT_DOWN_MASK : 0;
				StoreData.getInstance().setPrevModifiers(prevModifiers);

				StoreData.getInstance().save();
				
				refreshKeys();
			}
		});
	}
	
	private void loadData() {
		fieldReset.setText(Integer.toString(StoreData.getInstance().getResetKey()));
		fieldNext.setText(Integer.toString(StoreData.getInstance().getNextKey()));
		fieldPrev.setText(Integer.toString(StoreData.getInstance().getPrevKey()));
		
		boolean[] resetMod = getAltMetaCtrlShift(StoreData.getInstance().getResetModifiers());
		checkResetAlt.setSelected(resetMod[0]);
		checkResetMeta.setSelected(resetMod[1]);
		checkResetCtrl.setSelected(resetMod[2]);
		checkResetShift.setSelected(resetMod[3]);
		
		boolean[] nextMod = getAltMetaCtrlShift(StoreData.getInstance().getNextModifiers());
		checkNextAlt.setSelected(nextMod[0]);
		checkNextMeta.setSelected(nextMod[1]);
		checkNextCtrl.setSelected(nextMod[2]);
		checkNextShift.setSelected(nextMod[3]);

		boolean[] prevMod = getAltMetaCtrlShift(StoreData.getInstance().getPrevModifiers());
		checkPrevAlt.setSelected(prevMod[0]);
		checkPrevMeta.setSelected(prevMod[1]);
		checkPrevCtrl.setSelected(prevMod[2]);
		checkPrevShift.setSelected(prevMod[3]);
		
		refreshKeys();
	}

	private boolean[] getAltMetaCtrlShift(int modifiers) {
		boolean[] m = new boolean[4];
		if (modifiers >= InputEvent.ALT_DOWN_MASK) {
			m[0] = true;
			modifiers -= InputEvent.ALT_DOWN_MASK;
		}
		
		if (modifiers >= InputEvent.META_DOWN_MASK) {
			m[1] = true;
			modifiers -= InputEvent.META_DOWN_MASK;
		}

		if (modifiers >= InputEvent.CTRL_DOWN_MASK) {
			m[2] = true;
			modifiers -= InputEvent.CTRL_DOWN_MASK;
		}
		
		if (modifiers >= InputEvent.SHIFT_DOWN_MASK) {
			m[3] = true;
			modifiers -= InputEvent.SHIFT_DOWN_MASK;
		}
		
		return m;
	}

	private Component createFieldsPanel() {
		JPanel panel = new JPanel(new MigLayout());

		panel.add(labelReset);
		panel.add(fieldReset);
		panel.add(checkResetAlt);
		panel.add(checkResetMeta);
		panel.add(checkResetCtrl);
		panel.add(checkResetShift, "wrap");

		panel.add(labelNext);
		panel.add(fieldNext);
		panel.add(checkNextAlt);
		panel.add(checkNextMeta);
		panel.add(checkNextCtrl);
		panel.add(checkNextShift, "wrap");

		panel.add(labelPrev);
		panel.add(fieldPrev);
		panel.add(checkPrevAlt);
		panel.add(checkPrevMeta);
		panel.add(checkPrevCtrl);
		panel.add(checkPrevShift, "wrap");

		panel.add(labelFindKey);
		panel.add(fieldFindKey);
		panel.add(labelFindKeyResult, "wrap");

		panel.add(updateKeys);

		Font font = labelReset.getFont();
		Font newFont = new Font(font.getName(), font.getStyle(), 40);

		labelReset.setFont(newFont);
		fieldReset.setFont(newFont);
		checkResetAlt.setFont(newFont);
		checkResetMeta.setFont(newFont);
		checkResetCtrl.setFont(newFont);
		checkResetShift.setFont(newFont);

		labelNext.setFont(newFont);
		fieldNext.setFont(newFont);
		checkNextAlt.setFont(newFont);
		checkNextMeta.setFont(newFont);
		checkNextCtrl.setFont(newFont);
		checkNextShift.setFont(newFont);

		labelPrev.setFont(newFont);
		fieldPrev.setFont(newFont);
		checkPrevAlt.setFont(newFont);
		checkPrevMeta.setFont(newFont);
		checkPrevCtrl.setFont(newFont);
		checkPrevShift.setFont(newFont);

		labelFindKey.setFont(newFont);
		fieldFindKey.setFont(newFont);
		labelFindKeyResult.setFont(newFont);
		updateKeys.setFont(newFont);

		fieldFindKey.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				labelFindKeyResult.setText("= " + e.getKeyCode());
				labelFindKeyResult.validate();
				labelFindKeyResult.repaint();
			}
		});

		return panel;
	}

	public void addListener(TextListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void onHotKey(HotKey key) {
		int keyCode = key.keyStroke.getKeyCode();
		int modifiers = key.keyStroke.getModifiers();

		waitForKeyReleased();

		int resetKey = StoreData.getInstance().getResetKey();
		int resetModifiers = StoreData.getInstance().getResetModifiers();
		int nextKey = StoreData.getInstance().getNextKey();
		int nextModifiers = StoreData.getInstance().getNextModifiers();
		int prevKey = StoreData.getInstance().getPrevKey();
		int prevModifiers = StoreData.getInstance().getPrevModifiers();
		
		if (keyCode == resetKey && modifiers == resetModifiers) {
			for (TextListener listener : listeners) {
				listener.onReset();
			}
		} else if (keyCode == nextKey && modifiers == nextModifiers) {
			for (TextListener listener : listeners) {
				listener.onUnblock();
			}
		} else if (keyCode == prevKey && modifiers == prevModifiers) {
			for (TextListener listener : listeners) {
				listener.onRollback();
			}
		}
	}

	private void refreshKeys() {

		if (keyListenerProvider != null) {
			keyListenerProvider.reset();
			keyListenerProvider.stop();
		}

		keyListenerProvider = Provider.getCurrentProvider(false);

		int resetKey = StoreData.getInstance().getResetKey();
		int resetModifiers = StoreData.getInstance().getResetModifiers();
		int nextKey = StoreData.getInstance().getNextKey();
		int nextModifiers = StoreData.getInstance().getNextModifiers();
		int prevKey = StoreData.getInstance().getPrevKey();
		int prevModifiers = StoreData.getInstance().getPrevModifiers();

		KeyStroke clearKeyStroke = KeyStroke.getKeyStroke(resetKey, resetModifiers);
		keyListenerProvider.register(clearKeyStroke, this);

		KeyStroke nextKeyStroke = KeyStroke.getKeyStroke(nextKey, nextModifiers);
		keyListenerProvider.register(nextKeyStroke, this);

		KeyStroke prevKeyStroke = KeyStroke.getKeyStroke(prevKey, prevModifiers);
		keyListenerProvider.register(prevKeyStroke, this);
	}

	private void waitForKeyReleased() {
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
	}
}
