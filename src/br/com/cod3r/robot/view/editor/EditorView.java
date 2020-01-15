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

import br.com.cod3r.robot.helper.Files;
import br.com.cod3r.robot.helper.StoreData;
import br.com.cod3r.robot.text.TextController;
import br.com.cod3r.robot.text.TextEventType;
import br.com.cod3r.robot.view.editor.toolbar.ToolbarEventType;
import br.com.cod3r.robot.view.editor.toolbar.Toolbar;

public class EditorView extends JPanel {

	private static final long serialVersionUID = 1L;

	private boolean changed = false;

	private File file;
	private UndoManager undoManager = new UndoManager();

	private final TextController textCtrl = TextController.getInstance();
	
	private final Frame parent;
	private final Toolbar toolbar = new Toolbar();
	private final EditorSidebar sidebar = new EditorSidebar();
	
	private final JPanel textAreas = new JPanel(new BorderLayout());
	private final JTextPane mainTextArea = new JTextPane();
	private final JScrollPane mainTextAreaScroll = new JScrollPane(mainTextArea);
	private final JTextPane previewTextArea = new JTextPane() {
		private static final long serialVersionUID = 1L;

		public boolean getScrollableTracksViewportWidth() {
			return getUI().getPreferredSize(this).width <= getParent().getSize().width;
		}
	};

	private final SyntaxHighlighting syntaxHighlighting = new SyntaxHighlighting(previewTextArea);;

	public EditorView(Frame parent) {
		this.parent = parent;		
		
		setupToolbar();
		setupTextAreas();		
		setupSidebar();				
		setupLayout();

		loadLastFile();
		setupTextController();
	}



	private void setupLayout() {
		this.setLayout(new BorderLayout());
		this.add(toolbar, BorderLayout.NORTH);
		this.add(textAreas, BorderLayout.CENTER);
		this.add(sidebar, BorderLayout.EAST);
	}

	private void setupTextAreas() {
		setupMainTextArea();
		setupPreviewTextArea();
		
		textAreas.setBackground(Color.BLACK);
		textAreas.add(new JScrollPane(previewTextArea, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.NORTH);
		textAreas.add(mainTextAreaScroll, BorderLayout.CENTER);
	}

	private void setupSidebar() {
		sidebar.onEvent((text, caretPositionDiff) -> {
			try {
				mainTextArea.getDocument().insertString(mainTextArea.getCaretPosition(), text, null);
				mainTextArea.setCaretPosition(mainTextArea.getCaretPosition() + caretPositionDiff);
			} catch (BadLocationException e1) {
			}
		});
	}

	private void setupPreviewTextArea() {
		Font fontPresentation = new Font("Verdana", Font.PLAIN, 55);

				previewTextArea.setFont(fontPresentation);
		previewTextArea.setBackground(Color.BLACK);
		previewTextArea.setForeground(Color.WHITE);
	}

	private void setupMainTextArea() {
		Font font = new Font("Verdana", Font.PLAIN, 35);
		mainTextArea.setFont(font);
		mainTextArea.setBackground(Color.BLACK);
		mainTextArea.setForeground(Color.WHITE);
		mainTextArea.setCaretColor(Color.WHITE);
		mainTextArea.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		configureUndoAndRedo();

		mainTextArea.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				TextController.getInstance().load(mainTextArea.getText());
				updatePreviewTextArea(mainTextArea.getText());
				markTextAsChanged();
			}

			public void insertUpdate(DocumentEvent e) {
				TextController.getInstance().load(mainTextArea.getText());
				updatePreviewTextArea(mainTextArea.getText());
				markTextAsChanged();
			}

			public void changedUpdate(DocumentEvent e) {
			}
		});
	}

	@SuppressWarnings("serial")
	private void configureUndoAndRedo() {
		mainTextArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				undoManager.addEdit(e.getEdit());
			}
		});

		InputMap im = mainTextArea.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap am = mainTextArea.getActionMap();
		
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

	private void setupToolbar() {		
		toolbar.onEvent(e -> {
			if (e.getType() == ToolbarEventType.COPY_FILE_NAME) {
				copyFileName();
			} else if (e.getType() == ToolbarEventType.MIN_WAITING_TIME) {
				textCtrl.setMinWaitingTime(e.getMinWaitingTime());
			} else if (e.getType() == ToolbarEventType.MAX_WAITING_TIME) {
				textCtrl.setMaxWaitingTime(e.getMaxWaitingTime());
			} else if (e.getType() == ToolbarEventType.NEW_FILE) {
				newFile();
			} else if (e.getType() == ToolbarEventType.LOAD_FILE) {
				showChooser();
			} else if (e.getType() == ToolbarEventType.SAVE_FILE) {
				saveFile();
			} else if (e.getType() == ToolbarEventType.PREV_FILE) {
				prevFile();
			} else if (e.getType() == ToolbarEventType.NEXT_FILE) {
				nextFile();
			} else if (e.getType() == ToolbarEventType.FONT_INCREASE) {
				fontIncrease();
			} else if (e.getType() == ToolbarEventType.FONT_DECREASE) {
				fontDecrease();
			} else if (e.getType() == ToolbarEventType.HELP) {
				HelpDialog.show(parent);
			} else if (e.getType() == ToolbarEventType.VALIDATION_MODE) {
				textCtrl.setIgnoreBlock(e.isValidationMode());				
			}
		});
	}
	
	private void setupTextController() {
		textCtrl.onEvent(e -> {
			if(e.getType() == TextEventType.TYPED) {
				updatePreviewTextArea(e.getRemainText());
			} else if(e.getType() == TextEventType.FINISHED && !changed) {
				nextFile();				
			}
		});
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
			Files.writeFile(file.getAbsolutePath(), mainTextArea.getText());
			StoreData.getInstance().setLastFile(file);
			StoreData.getInstance().save();
			parent.setTitle(file.getAbsolutePath());
			changed = false;
		}
	}

	private void newFile() {
		parent.setTitle("");
		file = null;
		updateMainTextArea(null);
		TextController.getInstance().clear();
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
	
	private void loadLastFile() {
		try {
			file = StoreData.getInstance().getLastFile();
			loadFile();
		} catch (Exception e) {
			file = null;
		}
	}
	
	private void loadFile() {		
		if(file != null) {
			updateMainTextArea(Files.readFile(file.getAbsolutePath()));
			mainTextArea.setCaretPosition(0);
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
					updateMainTextArea(Files.readFile(EditorView.this.file.getAbsolutePath()));
					mainTextArea.setCaretPosition(0);
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
					updateMainTextArea(Files.readFile(EditorView.this.file.getAbsolutePath()));
					mainTextArea.setCaretPosition(0);
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
		Font font = mainTextArea.getFont();
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize() + 2);
		mainTextArea.setFont(newFont);
	}

	private void fontDecrease() {
		Font font = mainTextArea.getFont();
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize() - 2);
		mainTextArea.setFont(newFont);
	}

	private void copyFileName() {
		if (file == null) {
			return;
		}

		String fileName = file.getName().split(Pattern.quote("."))[0];
		StringSelection stringSelection = new StringSelection(fileName);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
	}

	private void updateMainTextArea(String text) {
		mainTextArea.setText(text);
		updatePreviewTextArea(text);
	}

	private void updatePreviewTextArea(String text) {
		if (text == null) {
			previewTextArea.setText(null);
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

		previewTextArea.setEditable(true);
		previewTextArea.setText(text);
		previewTextArea.setEditable(false);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				syntaxHighlighting.apply();
			}
		});
	}
}
