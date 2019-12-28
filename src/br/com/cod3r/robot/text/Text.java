package br.com.cod3r.robot.text;

class Text {

	private static final String START = "[[startHere]]";
	private String text;
	private int index;
	private boolean blocked = true;

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

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
		if (!hasNext() || isBlocked()) {
			return null;
		}

		String remainText = getRemainText();

		if (remainText.startsWith("[[")) {
			String command = remainText.substring(0, remainText.indexOf("]]") + 2);
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
