package br.com.cod3r.robot.text;

public class Text {

	private static final String START = "[[startHere]]";
	private String text;
	private int index;

	public void load(String originalText) {
		this.text = removeComments(originalText).replace("\n", "").replace("\r", "");
		resetIndex();
	}

	public void clear() {
		load("");
	}

	public boolean hasNext() {
		if (text == null) {
			return false;
		}
		return index < text.length();
	}
	
	public String getRemainText() {
		return text.substring(index);
	}	

	public void resetIndex() {
		this.index = 0;

		if (this.text != null && this.text.contains(START)) {
			this.index = this.text.lastIndexOf(START) + START.length();
		}
	}

	public void rollbackIndex() {
		if (index == 0) {
			return;
		}
		index = text.substring(0, index - 1).lastIndexOf("[[b]]");
		if (index < 0) {
			index = 0;
		} else {
			index += "[[b]]".length();
		}
	}

	public String next() {
		if (!hasNext()) {
			return null;
		}

		String currentString = text.substring(index);

		if (currentString.startsWith("[[")) {
			String command = currentString.substring(0, currentString.indexOf("]]") + 2);
			index += command.length();
			return command;
		} else {
			return Character.toString(text.charAt(index++));
		}
	}

	private String removeComments(String text) {
		return text.replaceAll("\\[\\[//(.*)\\]\\]", "");
	}
}
