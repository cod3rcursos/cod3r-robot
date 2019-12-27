package br.com.cod3r.robot.keyboard;

import java.awt.Robot;
import java.util.logging.Logger;

public class Typist {

	private static Robot robot;

	static {
		try {
			robot = new Robot();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static void type(KeySequence sequence) {
		if (sequence == null)
			return;

		sequence.forEach((keycode, action) -> {
			try {
				if (action == KeyAction.PRESS) {
					robot.keyPress(keycode);
				} else if (action == KeyAction.RELEASE) {
					robot.keyRelease(keycode);
				}
			} catch (Exception e) {
				Logger.getAnonymousLogger()
					.warning(keycode + " => " + e.getMessage());
			}
		});
	}
}
