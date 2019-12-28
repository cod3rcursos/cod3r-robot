package br.com.cod3r.robot.view.editor.toolbar;

public class ToolbarEvent {

	private static final long DEFAULT_MIN_WAITING_TIME = 90;
	private static final long DEFAULT_MAX_WAITING_TIME = 130;

	private final ToolbarEventType type;
	private final String minWaitingTime;
	private final String maxWaitingTime;
	private final boolean validationMode;

	public ToolbarEvent(ToolbarEventType type, String minWaitingTime, String maxWaitingTime,
			boolean validationMode) {
		this.type = type;
		this.minWaitingTime = minWaitingTime;
		this.maxWaitingTime = maxWaitingTime;
		this.validationMode = validationMode;
	}

	public ToolbarEventType getType() {
		return type;
	}

	public long getMinWaitingTime() {
		try {
			return Long.parseLong(minWaitingTime);
		} catch (NumberFormatException e) {
			return DEFAULT_MIN_WAITING_TIME;
		}
	}

	public long getMaxWaitingTime() {
		try {
			return Long.parseLong(maxWaitingTime);
		} catch (NumberFormatException e) {
			return DEFAULT_MAX_WAITING_TIME;
		}
	}
	
	public boolean isValidationMode() {
		return validationMode;
	}
}
