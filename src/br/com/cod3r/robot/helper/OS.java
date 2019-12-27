package br.com.cod3r.robot.helper;

public class OS {

	private static String operatingSystem;

	private OS() {
	}

	public static String getOsName() {
		if (operatingSystem == null) {
			operatingSystem = System.getProperty("os.name");
		}
		return operatingSystem;
	}

	public static boolean isWindows() {
		return getOsName().toLowerCase().contains("windows");
	}

	public static boolean isMac() {
		return getOsName().toLowerCase().contains("mac");
	}

	public static boolean isLinux() {
		return getOsName().toLowerCase().contains("linux");
	}
}
