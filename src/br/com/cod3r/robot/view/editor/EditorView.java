package br.com.cod3r.robot.view.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import br.com.cod3r.robot.exception.BlockException;
import br.com.cod3r.robot.exception.ChangeMaxSpeedException;
import br.com.cod3r.robot.exception.ChangeMinSpeedException;
import br.com.cod3r.robot.exception.WaitException;
import br.com.cod3r.robot.helper.Files;
import br.com.cod3r.robot.helper.OS;
import br.com.cod3r.robot.helper.StoreData;
import br.com.cod3r.robot.keyboard.KeySequence;
import br.com.cod3r.robot.keyboard.KeyboardMapper;
import br.com.cod3r.robot.keyboard.Typist;
import br.com.cod3r.robot.text.Text;
import br.com.cod3r.robot.text.TextListener;

public class EditorView extends JPanel implements TextListener, Runnable {

	private static final long serialVersionUID = 1L;

	private boolean block = true;
	private boolean changed = false;

	private File file;
	private UndoManager undoManager = new UndoManager();

	private final Text text = new Text();

	private final Frame parent;

	private final EditorToolbar toolbar = new EditorToolbar();
	private final EditorSidebar sidebar = new EditorSidebar();
	private final JTextPane previewText;
	private final JTextPane mainText;
	private JScrollPane mainTextScroll = null;

	private final SyntaxHighlighting syntaxHighlighting;

	public EditorView(Frame parent) {
		this.parent = parent;		
		this.setLayout(new BorderLayout());

		configToolbarEvents();
		this.add(toolbar, BorderLayout.NORTH);

		Font font = new Font("Verdana", Font.PLAIN, 35);

		mainText = new JTextPane();
		mainText.setFont(font);
		mainText.setBackground(Color.BLACK);
		mainText.setForeground(Color.WHITE);
		mainText.setCaretColor(Color.WHITE);
		mainText.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		Font fontPresentation = new Font("Verdana", Font.PLAIN, 55);

		previewText = new JTextPane() {
			private static final long serialVersionUID = 1L;

			public boolean getScrollableTracksViewportWidth() {
				return getUI().getPreferredSize(this).width <= getParent().getSize().width;
			}
		};
		previewText.setFont(fontPresentation);
		previewText.setBackground(Color.BLACK);
		previewText.setForeground(Color.WHITE);

		mainText.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				text.load(mainText.getText());
				setTextToTextAreaPresentation(mainText.getText());
				block = true;
				markTextAsChanged();
			}

			public void insertUpdate(DocumentEvent e) {
				text.load(mainText.getText());
				setTextToTextAreaPresentation(mainText.getText());
				block = true;
				markTextAsChanged();
			}

			public void changedUpdate(DocumentEvent e) {
			}
		});

		syntaxHighlighting = new SyntaxHighlighting(previewText);

		configureUndoAndRedo();

		mainTextScroll = new JScrollPane(mainText);

		JPanel textAreas = new JPanel(new BorderLayout());
		textAreas.setBackground(Color.BLACK);

		textAreas.add(new JScrollPane(previewText, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.NORTH);
		textAreas.add(mainTextScroll, BorderLayout.CENTER);

		this.add(textAreas, BorderLayout.CENTER);
		this.add(sidebar, BorderLayout.EAST);

		sidebar.onEvent((text, caretPositionDiff) -> {
			try {
				mainText.getDocument().insertString(mainText.getCaretPosition(), text, null);
				mainText.setCaretPosition(mainText.getCaretPosition() + caretPositionDiff);
			} catch (BadLocationException e1) {
			}
		});
		
		file = StoreData.getInstance().getLastFile();
		loadFile();

		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	@SuppressWarnings("serial")
	private void configureUndoAndRedo() {
		mainText.getDocument().addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				undoManager.addEdit(e.getEdit());
			}
		});

		InputMap im = mainText.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap am = mainText.getActionMap();
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Redo");

		am.put("Undo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (undoManager.canUndo()) {
						undoManager.undo();
					}
				} catch (CannotUndoException exp) {
					exp.printStackTrace();
				}
			}
		});

		am.put("Redo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (undoManager.canRedo()) {
						undoManager.redo();
					}
				} catch (CannotUndoException exp) {
					exp.printStackTrace();
				}
			}
		});
	}

	private void configToolbarEvents() {
		toolbar.onEvent(event -> {
			if (event == EditorEvent.NEW_FILE) {
				newFile();
			} else if (event == EditorEvent.LOAD_FILE) {
				showChooser();
			} else if (event == EditorEvent.SAVE_FILE) {
				saveFile();
			} else if (event == EditorEvent.PREV_FILE) {
				prevFile();
			} else if (event == EditorEvent.NEXT_FILE) {
				nextFile();
			} else if (event == EditorEvent.FONT_INCREASE) {
				fontIncrease();
			} else if (event == EditorEvent.FONT_DECREASE) {
				fontDecrease();
			} else if (event == EditorEvent.HELP) {
				HelpDialog.show(parent);
			} else if (event == EditorEvent.COPY_FILE_NAME) {
				copyFileName();
			}
		});
	}

	@Override
	public void onType() {
		if (!block) {
			try {
				String keys = text.next();

				if (keys != null) {
					KeySequence keySequence = KeyboardMapper.getKeyCode(keys);
					Typist.type(keySequence);
				}

				if (needExtraSpace(keys)) {
					Typist.type(KeyboardMapper.getKeyCode(" "));
				}

				boolean finished = keys == null || !text.hasNext();
				if (finished && !changed) {
					nextFile();
				}

				setTextToTextAreaPresentation(text.getRemainText());
			} catch (BlockException e) {
				if (!toolbar.isValidationMode()) {
					block = true;
				}
			} catch (WaitException e) {
				waitFor(e.getTime());
			} catch (ChangeMinSpeedException e) {
				toolbar.setMinSpeed(e.getTime());
			} catch (ChangeMaxSpeedException e) {
				toolbar.setMaxSpeed(e.getTime());
			}
		}
	}

	private boolean needExtraSpace(String keys) {
		return ("\"".equals(keys) || "\'".equals(keys)) && OS.isMac();
	}

	@Override
	public void onUnblock() {
		block = false;
	}

	@Override
	public void onReset() {
		text.load(mainText.getText());
	}

	@Override
	public void onRollback() {
		text.rollbackIndex();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(getSleepInterval());
				onType();
			} catch (InterruptedException e) {
			}
		}
	}

	private long getSleepInterval() {
		long min = toolbar.getMinSpeed();
		long max = toolbar.getMaxSpeed();
		long diff = max - min;
		return (long) (Math.random() * diff + min);
	}

	private void waitFor(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

	private boolean ignoreChange() {
		if (!changed) {
			return true;
		}

		return JOptionPane.showConfirmDialog(parent, "Deseja continuar sem salvar?") == JOptionPane.YES_OPTION;
	}

	private void markTextAsChanged() {
		changed = true;
		putAsteriskOnTitle();
	}

	private void putAsteriskOnTitle() {
		if (!parent.getTitle().isEmpty() && !parent.getTitle().endsWith("*")) {
			parent.setTitle(parent.getTitle() + "*");
		}
	}

	private void saveFile() {
		if (file == null) {
			JFileChooser chooser = new JFileChooser();
			if (StoreData.getInstance().getLastFolder() != null) {
				chooser.setCurrentDirectory(StoreData.getInstance().getLastFolder());
			}
			int returnVal = chooser.showSaveDialog(EditorView.this);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}
			file = chooser.getSelectedFile();
		}

		if (file != null) {
			Files.writeFile(file.getAbsolutePath(), mainText.getText());
			StoreData.getInstance().setLastFile(file);
			StoreData.getInstance().save();
			parent.setTitle(file.getAbsolutePath());
			changed = false;
		}
	}

	private void newFile() {
		parent.setTitle("");
		file = null;
		setTextToTextArea(null);
		text.clear();
		saveFile();
	}

	private void showChooser() {
		if (!ignoreChange()) {
			return;
		}

		JFileChooser chooser = null;
		if (file != null && file.getParentFile() != null) {
			chooser = new JFileChooser(file.getParentFile());
		} else if (StoreData.getInstance().getLastFolder() != null) {
			chooser = new JFileChooser(StoreData.getInstance().getLastFolder());
		} else {
			chooser = new JFileChooser();
		}
		
		int returnVal = chooser.showOpenDialog(EditorView.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
			StoreData.getInstance().setLastFile(file);
			StoreData.getInstance().save();
			loadFile();
		}
	}
	
	private void loadFile() {
		if(file != null) {
			setTextToTextArea(Files.readFile(file.getAbsolutePath()));
			mainText.setCaretPosition(0);
			parent.setTitle(file.getAbsolutePath());
			changed = false;			
		}
	}

	private void prevFile() {
		if (!ignoreChange()) {
			return;
		}

		String prevName = null;
		for (String fileName : Files.getFolderFiles(StoreData.getInstance().getLastFolderPath())) {
			if (fileName.equals(EditorView.this.file.getName())) {
				String newFile = StoreData.getInstance().getLastFolder() + File.separator + prevName;
				File backup = EditorView.this.file;
				EditorView.this.file = new File(newFile);
				try {
					setTextToTextArea(Files.readFile(EditorView.this.file.getAbsolutePath()));
					mainText.setCaretPosition(0);
					parent.setTitle(EditorView.this.file.getAbsolutePath());
					changed = false;
				} catch (Exception e1) {
					EditorView.this.file = backup;
				}
				return;
			} else {
				prevName = fileName;
			}
		}
	}

	private void nextFile() {
		if (!ignoreChange()) {
			return;
		}

		String currName = null;
		for (String fileName : Files.getFolderFiles(StoreData.getInstance().getLastFolderPath())) {
			if (fileName.equals(EditorView.this.file.getName())) {
				currName = fileName;
			} else if (currName != null) {
				String newFile = StoreData.getInstance().getLastFolder() + File.separator + fileName;
				File backup = EditorView.this.file;
				EditorView.this.file = new File(newFile);
				try {
					setTextToTextArea(Files.readFile(EditorView.this.file.getAbsolutePath()));
					mainText.setCaretPosition(0);
					parent.setTitle(EditorView.this.file.getAbsolutePath());
					changed = false;
				} catch (Exception e1) {
					EditorView.this.file = backup;
				}
				return;
			}
		}
	}

	private void fontIncrease() {
		Font font = mainText.getFont();
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize() + 2);
		mainText.setFont(newFont);
	}

	private void fontDecrease() {
		Font font = mainText.getFont();
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize() - 2);
		mainText.setFont(newFont);
	}

	private void copyFileName() {
		if (file == null) {
			return;
		}

		String fileName = file.getName().split(Pattern.quote("."))[0];
		StringSelection stringSelection = new StringSelection(fileName);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
	}

	private void setTextToTextArea(String text) {
		mainText.setText(text);
		setTextToTextAreaPresentation(text);
	}

	private void setTextToTextAreaPresentation(String text) {
		if (text == null) {
			previewText.setText(null);
			return;
		}

		if (text.startsWith("[[b]]")) {
			text = text.substring(5);
		}

		String[] parts = text.split(Pattern.quote("[[startHere]]"));
		if (parts.length > 1) {
			text = parts[parts.length - 1];
		}

		text = text.replace("\n", "").replace("\r", "");

		if (text.length() > 140) {
			text = text.substring(0, 140);
		}

		previewText.setEditable(true);
		previewText.setText(text);
		previewText.setEditable(false);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				syntaxHighlighting.apply();
			}
		});
	}
}
