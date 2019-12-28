package br.com.cod3r.robot.text;

public class TextEvent {

	private final TextEventType type;
	private final String remainText;

	public TextEvent(TextEventType type, String remainText) {
		this.type = type;
		this.remainText = remainText;
	}

	public TextEventType getType() {
		return type;
	}

	public String getRemainText() {
		return remainText;
	}
}
