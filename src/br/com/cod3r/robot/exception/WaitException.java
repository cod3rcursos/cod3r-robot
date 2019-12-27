package br.com.cod3r.robot.exception;

public class WaitException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final long time;

	public WaitException(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}
}
