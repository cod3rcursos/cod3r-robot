package br.com.cod3r.robot.view.editor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

class SyntaxHighlighting {

	private static final String[] reservedWords = new String[] { 
		"async", "await", "bool", "boolean", "break", "char", "class",
		"const", "continue", "do", "double",
		"dynamic", "else", "extends", "final", "float",
		"for", "function", "if", "implements", "int", "let", "long",
		"new", "package", "private", "protected", "public", 
		"return", "static", "switch", "throw", "throws", 
		"var", "void", "while", "String", 
	};
	
	private static final Map<String, Color> theme = new LinkedHashMap<String, Color>();

	static {
		// Métodos .metodo(
		theme.put("(?<=\\.).*?(?=\\()", new Color(103, 150, 230));

		// palavras reservadas...
		StringBuilder reservedWordsRegex = new StringBuilder("");
		for (String reservedWord : reservedWords) {
			reservedWordsRegex.append(reservedWord);
			reservedWordsRegex.append("|");
		}

		reservedWordsRegex.deleteCharAt(reservedWordsRegex.length() - 1);
		theme.put("\\b(" + reservedWordsRegex + ")\\b", new Color(194, 93, 247));

		// Strings...
		theme.put("\\\"[\\s\\S]*?\\\"", new Color(206, 145, 120));
		theme.put("\\'[\\s\\S]*?\\'", new Color(206, 145, 120));
		
		// Ponto . ou ( ou )
		theme.put("\\.|\\(|\\)", Color.WHITE);
		
		// Meta...
		theme.put("\\[\\[.*?\\]\\]", Color.GRAY);

		// Comentários
		theme.put("\\[\\[\\/\\/.*?\\]\\]?", new Color(96, 139, 78));
		theme.put("(\\/\\*[\\s\\S]*?\\*\\/)", new Color(96, 139, 78));
		theme.put("\\/\\/.*?^(\\[\\[)", new Color(96, 139, 78));
	}

	private JTextPane textArea;
	private String text;

	public SyntaxHighlighting(JTextPane textArea) {
		this.textArea = textArea;
	}

	public void apply() {
		if (textArea.getText() == null || textArea.getText().trim().isEmpty()) {
			return;
		}

		text = textArea.getText();

		boolean editable = textArea.isEditable();
		textArea.setEditable(true);
		textArea.setText(null);

		Color[] colors = new Color[text.length()];
		Arrays.fill(colors, Color.WHITE);

		for (Entry<String, Color> entry : theme.entrySet()) {
			List<Chunck> chuncks = getChucks(entry.getKey());
			for (Chunck chunck : chuncks) {
				for (int i = 0; i < chunck.length; i++) {
					colors[chunck.index + i] = entry.getValue();
				}
			}
		}

		char[] textArray = text.toCharArray();
		for (int j = 0; j < colors.length; j++) {
			appendToPane(Character.toString(textArray[j]), colors[j]);
		}

		textArea.setCaretPosition(0);
		textArea.setEditable(editable);
	}

	private List<Chunck> getChucks(String regex) {
		List<Chunck> chuncks = new ArrayList<Chunck>();

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
			chuncks.add(new Chunck(matcher.start(), matcher.group().length()));
		}

		return chuncks;
	}

	private void appendToPane(String msg, Color c) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		int len = textArea.getText().length();
		textArea.setCaretPosition(len);
		textArea.setCharacterAttributes(aset, false);
		textArea.replaceSelection(msg);
	}

	private class Chunck {
		final int index;
		final int length;

		public Chunck(int index, int length) {
			super();
			this.index = index;
			this.length = length;
		}
	}
}
