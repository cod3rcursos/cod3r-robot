package br.com.cod3r.robot.exception;

public class ChangeMaxSpeedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final long time;

	public ChangeMaxSpeedException(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}
}
