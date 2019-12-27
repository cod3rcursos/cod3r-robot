package br.com.cod3r.robot.keyboard;

import java.util.regex.Pattern;

public class Command {
	final int times;
	final String text;

	public Command(String original) {
		if (original.startsWith("[[")) {
			Integer times = getTimes(original);
			this.text = getText(original, times);
			this.times = times != null ? times : 1;
		} else {
			this.times = 1;
			this.text = original;
		}
	}

	private String getText(String original, Integer times) {
		if (times == null) {
			return original;
		}
		String[] parts = original.split(Pattern.quote(times.toString()));
		return parts[0] + parts[1];
	}

	private Integer getTimes(String original) {
		Integer times = null;
		try {
			for (int i = 3; i < original.length(); i++) {
				times = Integer.parseInt(original.substring(2, i));
			}
		} catch (NumberFormatException e) {
		}
		return times;
	}
}
